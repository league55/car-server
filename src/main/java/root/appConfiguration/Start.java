package root.appConfiguration;

import com.intel.analytics.bigdl.opencv.OpenCV;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"root.app", "root.app.model", "root.appConfiguration"})
@EnableCaching
public class Start {

    public static void main(String[] args) {
        // load the native OpenCV library
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        log.info(System.getProperty("java.library.path"));
        log.info(String.valueOf(OpenCV.isOpenCVLoaded()));
        SpringApplication.run(Start.class, args);

    }

}
