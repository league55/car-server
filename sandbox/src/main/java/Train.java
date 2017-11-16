import org.opencv.core.*;
import org.opencv.ml.SVM;

import java.io.File;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;


public class Train {

    protected static final String PATH_POSITIVE = "C:\\Users\\maksym\\IdeaProjects\\opencv-samples\\vehicle-detection-hog\\DATASET\\POSITIVE";
    protected static final String PATH_NEGATIVE = "C:\\Users\\maksym\\IdeaProjects\\opencv-samples\\vehicle-detection-hog\\DATASET\\NEGATIVE";
    protected static final String XML = "C:\\Users\\maksym\\IdeaProjects\\carsmonitoring\\sandbox";
    protected static final String FILE_TEST = "C:\\Users\\maksym\\IdeaProjects\\opencv-samples\\vehicle-detection-hog\\DATASET\\NEGATIVE\\0001.jpg";

    private static Mat trainingImages;
    private static Mat trainingLabels;
    private static Mat trainingData;
    private static Mat classes;
    private static SVM clasificador;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        trainingImages = new Mat();
        trainingLabels = new Mat();
        trainingData = new Mat();
        classes = new Mat();
    }

    public static void main(String[] args) {
        trainPositive();
//        trainNegative();
        train();
        test();
    }

    protected static void test() {
        Mat in = imread(new File(FILE_TEST).getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
        clasificador.load(new File(XML).getAbsolutePath());
        System.out.println(clasificador);
        Mat out = new Mat();
        in.convertTo(out, CvType.CV_32FC1);
        out = out.reshape(1, 1);
        System.out.println(out);
        System.out.println(clasificador.predict(out));
    }

    protected static void train() {

        trainingImages.copyTo(trainingData);
        trainingData.convertTo(trainingData, CvType.CV_32FC1);
        trainingLabels.copyTo(classes);
        clasificador = SVM.load("C:\\Users\\maksym\\IdeaProjects\\opencv-samples\\vehicle-detection-hog\\vehicle_detector.yml"); // trainingData, classes, new Mat(), new Mat(), params


          clasificador.setCoef0(0.0);
        clasificador.setDegree(3);
        clasificador.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 100, 1e-3));
        clasificador.setGamma(0);
        clasificador.setKernel(SVM.LINEAR);
        clasificador.setNu(0.5);
        clasificador.setP(0.1); // for EPSILON_SVR, epsilon in loss function?
        clasificador.setC(0.01); // From paper, soft classifier
        clasificador.setType(SVM.EPS_SVR); // C_SVC; // EPSILON_SVR; // may be also NU_SVR; // do regression task
        clasificador.setClassWeights(classes);

        clasificador.save(XML);

    }

    protected static void trainPositive() {
        for (File file : new File(PATH_POSITIVE).listFiles()) {
            Mat img = getMat(file.getAbsolutePath());
            trainingImages.push_back(img.reshape(1, 1));
            trainingLabels.push_back(Mat.ones(new Size(1, 1), CvType.CV_32FC1));
        }
    }

    protected static void trainNegative() {
        for (File file : new File(PATH_NEGATIVE).listFiles()) {
            Mat img = getMat(file.getAbsolutePath());
            trainingImages.push_back(img.reshape(1, 1));
            trainingLabels.push_back(Mat.zeros(new Size(1, 1), CvType.CV_32FC1));
        }
    }

    protected static Mat getMat(String path) {
        Mat img = new Mat();
        Mat con = imread(path, CV_LOAD_IMAGE_GRAYSCALE);
        con.convertTo(img, CvType.CV_32FC1, 1.0 / 255.0);
        return img;
    }

}