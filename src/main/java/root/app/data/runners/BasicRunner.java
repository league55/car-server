package root.app.data.runners;


import com.google.common.collect.Lists;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.videoio.VideoCapture;
import root.app.data.detectors.Detector;
import root.app.data.processors.DetectedCarProcessor;
import root.app.data.services.*;
import root.app.data.services.impl.ImageScaleServiceImpl.ScreenSize;
import root.app.model.Car;
import root.app.model.PolygonDTO;
import root.app.model.RoadWay;
import root.app.properties.*;
import root.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public abstract class BasicRunner implements Runner {
    private final static int FRAME_WIDTH = 800;
    protected Button button;

    private ImageView imageView;
    private Pane containerPane;
    // the OpenCV object that realizes the video capture
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    protected VideoCapture capture = new VideoCapture();
    // a flag to change the button behavior
    protected boolean cameraActive = false;
    private boolean isFirstFrame = true;
    // the id of the camera to be used
    protected static int cameraId = 0;
    private static List<RoadWay> wayList;
    private long carCount = 0;


    protected final AppConfigService appConfigService;
    private final Detector carsDetector;
    private final DetectedCarProcessor carProcessor;
    private final DrawingService drawingService;
    private final ZoneCrossingService zoneCrossingService;
    private final LineCrossingService lineCrossingService;
    private final RoadWaysConfigService zoneConfigService;
    private final SpeedService speedService;
    private final CVShowing cvShowing;
    private final DataOutputService dataOutputService;
    private final PolygonConfigService polygonConfigService;
    private List<Car> cars = new ArrayList<>();
    private final ImageScaleService scaleService;

    protected BasicRunner(DataOutputService dataOutputService, AppConfigService appConfigService, Detector carsDetector, DetectedCarProcessor carProcessor, DrawingService drawingService,
                          ZoneCrossingService zoneCrossingService, LineCrossingService lineCrossingService, SpeedService speedService, RoadWaysConfigService zoneConfigService,
                          CVShowing cvShowing, PolygonConfigService polygonConfigService, ImageScaleService scaleService) {
        this.dataOutputService = dataOutputService;
        this.appConfigService = appConfigService;
        this.carsDetector = carsDetector;
        this.carProcessor = carProcessor;
        this.drawingService = drawingService;
        this.zoneCrossingService = zoneCrossingService;
        this.lineCrossingService = lineCrossingService;
        this.speedService = speedService;
        this.zoneConfigService = zoneConfigService;
        this.cvShowing = cvShowing;
        this.polygonConfigService = polygonConfigService;
        this.scaleService = scaleService;
    }


    @Override
    public void startCapturing() {

        // set a fixed width for the frame
        this.imageView.setFitWidth(FRAME_WIDTH);
        log.debug("Frame width is {}px", FRAME_WIDTH);
//         preserve image ratio
//        this.imageView.setPreserveRatio(true);
        if (!this.cameraActive) {
            this.cameraActive = true;

            openCamera();
            // grab a frame every 33 ms (30 frames/sec)
            Runnable frameGrabber = () -> {
                // effectively grab and process a single frame
                Mat frame = grabFrame();

                // convert and show the frame
                Image imageToShow = Utils.mat2Image(frame);
                updateImageView(imageView, imageToShow);
            };

            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
            final String one = appConfigService.findOne(ConfigAttribute.TimeBetweenOutput).getValue();
            dataOutputService.writeOnFixedRate(Integer.parseInt(one));

        } else {
            this.stopCapturing();
            stopCamera();
            this.cameraActive = false;
            dataOutputService.stopWriting();

            // stop the timer
        }
    }


    @Override
    public void stopCapturing() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
        wayList = Lists.newArrayList();
    }

    protected abstract void openCamera();

    protected abstract void stopCamera();

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private Mat grabFrame() {
        // init everything
        Mat frame1 = new Mat();
        Mat frame2 = new Mat();
        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame1
                this.capture.read(frame1);
                this.capture.read(frame2);

                if (wayList == null || wayList.size() == 0) {
                    wayList = zoneConfigService.findAll();
                }

                if (!frame1.empty() && !frame2.empty() && wayList.size() > 0) {
                    final ScreenSize cvMatSize = new ScreenSize(frame1.height(), frame1.width());
                    Rect roi = getRoi(cvMatSize);

                    // car detection
                    List<Car> currentFrameCars = carsDetector.detectCars(frame1, frame2, roi);

                    carProcessor.matchCurrentFrameDetectedCarsToExistingDetectedCars(cars, currentFrameCars);
                    //white boxes  drawingService.drawAndShowContours(frame2, frame2.size(), cars);

                    currentFrameCars.clear();

                    lineCrossingService.setCrossingTimeMarks(cars, cvMatSize);

                    speedService.countSpeed(cars);
                    long newCarCount = zoneCrossingService.countCars(cars);

                    cars = cars.stream().filter(Car::isStillTracked).collect(Collectors.toList());

                    dataOutputService.updateCarData(cars);
                    boolean isCarCrossing = newCarCount != carCount;
                    if (isCarCrossing) {
                        log.debug("New cars, now count: {}", carCount);
                        carCount = newCarCount;
                    }

//                    drawingService.showLines(frame2, wayList, isCarCrossing);
                    cvShowing.drawRect(frame2, roi);
                    cvShowing.drawCarInfoOnImage(cars, frame2);
                    cvShowing.drawCarCountOnImage(carCount, frame2);
//                    cvShowing.drawZones(frame2, cvMatSize, cars);

                    return frame2;
                }
                return frame2;
            } catch (Exception e) {
                // log the error
                log.error(e.toString() + "\n { }", e);
                System.exit(0);
            }
        }

        return frame1;
    }


    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    public void setActionButton(Button button) {
        this.button = button;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setContainerPane(Pane pane) {
        this.containerPane = pane;
    }

    private Rect getRoi(ScreenSize cvMatSize) {
        final PolygonDTO roi = polygonConfigService.findOne(PolygonDTO.Destination.ROI);
        if (roi != null) {
            PolygonDTO ROI = scaleService.fixScale(cvMatSize, roi);

            Double height = Math.max(ROI.getBotLeft().getY() - ROI.getTopLeft().getY(), ROI.getBotRight().getY() - ROI.getTopRight().getY());
            Double width = Math.max(ROI.getTopRight().getX() - ROI.getTopLeft().getX(), ROI.getBotRight().getX() - ROI.getBotLeft().getX());

            final int x = ROI.getTopLeft().getX().intValue();
            final int y = ROI.getTopLeft().getY().intValue();
            if (x + width > cvMatSize.getWindowWidth()) {
                width -= x + width - cvMatSize.getWindowWidth();
            }
            if (y + height > cvMatSize.getWindowHeight()) {
                height -= y + height - cvMatSize.getWindowHeight();
            }
            return new Rect(x, y, width.intValue(), height.intValue());
        }
        return null;
    }
}

