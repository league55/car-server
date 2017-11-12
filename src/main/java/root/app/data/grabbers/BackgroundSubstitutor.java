package root.app.data.grabbers;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

/**
 * Finds moving object comparing 2 frames after some image transformation
 */
public class BackgroundSubstitutor {
    private static final int CV_THRESH_BINARY = 0;
    private static final Mat structuringElement3x3 = Imgproc.getStructuringElement(MORPH_RECT, new Size(3, 3));
    private static final Mat structuringElement5x5 = Imgproc.getStructuringElement(MORPH_RECT, new Size(5, 5));
    private static final Mat structuringElement7x7 = Imgproc.getStructuringElement(MORPH_RECT, new Size(7, 7));
    private static final Mat structuringElement9x9 = Imgproc.getStructuringElement(MORPH_RECT, new Size(9, 9));

    public static void getObjects(Mat frame, Mat frame2, Mat erodedMat) {
        Mat imgFrame1Copy = frame.clone();
        Mat imgFrame2Copy = frame2.clone();
        Mat imgDifference = new Mat();
        Mat imgThresh = new Mat();

        Imgproc.cvtColor(frame, imgFrame1Copy, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(frame2, imgFrame2Copy, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(imgFrame1Copy, imgFrame1Copy, new Size(5, 5), 0);
        Imgproc.GaussianBlur(imgFrame2Copy, imgFrame2Copy, new Size(5, 5), 0);

        Core.absdiff(imgFrame1Copy, imgFrame2Copy, imgDifference);

        Imgproc.threshold(imgDifference, imgThresh, 30, 255.0, CV_THRESH_BINARY);

// this place may change, maybe there's sense to make it configurable
//      Imgproc.erode(imgThresh, imgThresh, structuringElement3x3);
        Imgproc.dilate(imgThresh, imgThresh, structuringElement9x9);
        Imgproc.dilate(imgThresh, imgThresh, structuringElement9x9);

        Imgproc.erode(imgThresh, imgThresh, structuringElement5x5);
        Imgproc.erode(imgThresh, imgThresh, structuringElement5x5);
//        Imgproc.erode(imgThresh, frame2, structuringElement5x5);
//      Imgproc.dilate(imgThresh, imgThresh, structuringElement9x9);

        Mat imgThreshCopy = imgThresh.clone();

        //convert the image to black and white does (8 bit), commenting this crashes
        Imgproc.Canny(imgThreshCopy, erodedMat, 30, 30);

        //apply gaussian blur to smoothen lines of dots, commenting this crashes
        Imgproc.GaussianBlur(erodedMat, erodedMat, new Size(5, 5), 5);

    }
}
