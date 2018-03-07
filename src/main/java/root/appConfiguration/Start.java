package root.appConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        log.info(System.getProperty("java.library.path"));
        SpringApplication.run(Start.class, args);

    }

}
