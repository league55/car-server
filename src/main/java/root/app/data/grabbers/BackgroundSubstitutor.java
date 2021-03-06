package root.app.data.grabbers;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;

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
    private static BackgroundSubtractor subtractorKNN = Video.createBackgroundSubtractorMOG2(400, 1000, false);

    public static void getObjects(Mat frame, Mat frame2, Mat erodedMat) {
        Mat imgFrame1Copy = new Mat();
        Mat imgFrame2Copy = new Mat();
        Mat imgDifference = new Mat();
        Mat imgThresh = new Mat();

        Imgproc.cvtColor(frame, imgFrame1Copy, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(frame2, imgFrame2Copy, Imgproc.COLOR_BGR2GRAY);
//
//        Imgproc.GaussianBlur(imgFrame1Copy, imgFrame1Copy, new Size(5, 5), 0);
//        Imgproc.GaussianBlur(imgFrame2Copy, imgFrame2Copy, new Size(5, 5), 0);

//        Imgproc.threshold(imgFrame1Copy, imgFrame1Copy, 90, 300, CV_THRESH_BINARY);
//        Imgproc.threshold(imgFrame2Copy, imgFrame2Copy, 90, 300, CV_THRESH_BINARY);
//        Imgproc.threshold(imgFrame2Copy, frame2, 90, 300, 1);

//        Core.absdiff(imgFrame1Copy, imgFrame2Copy, imgDifference);
//        Core.absdiff(imgFrame1Copy, imgFrame2Copy, frame2);
//        Core.subtract(imgFrame1Copy, imgFrame2Copy, frame2);

        subtractorKNN.apply(imgFrame1Copy, imgFrame2Copy);

//        Imgproc.threshold(imgDifference, imgThresh, 30, 255.0, CV_THRESH_BINARY);

// this place may change, maybe there's sense to make it configurable
        Imgproc.dilate(imgFrame2Copy, imgFrame2Copy, structuringElement3x3);
        Imgproc.dilate(imgFrame2Copy, imgFrame2Copy, structuringElement9x9);
        Imgproc.dilate(imgFrame2Copy, imgFrame2Copy, structuringElement9x9);
        Imgproc.dilate(imgFrame2Copy, imgFrame2Copy, structuringElement9x9);
        Imgproc.dilate(imgFrame2Copy, imgFrame2Copy, structuringElement9x9);
//
//        Imgproc.erode(imgFrame2Copy, imgFrame2Copy, structuringElement5x5);
//        Imgproc.erode(imgFrame2Copy, imgFrame2Copy, structuringElement5x5);
//        Imgproc.erode(imgThresh, imgFrame2Copy, structuringElement5x5);
//      Imgproc.dilate(imgThresh, imgThresh, structuringElement9x9);

        //convert the image to black and white does (8 bit), commenting this crashes
        Imgproc.Canny(imgFrame2Copy, erodedMat, 30, 30);

        //apply gaussian blur to smoothen lines of dots, commenting this crashes
//        Imgproc.GaussianBlur(erodedMat, erodedMat, new Size(5, 5), 5);
    }
}
