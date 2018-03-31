package root.appConfiguration;

import lombok.extern.slf4j.Slf4j;
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
        try {
            System.loadLibrary("opencv_java331");

        } catch (Exception e) {
            log.error("failed to load dll from system, trying file", e);
            System.load("/tmp/opencv_java331.dll");
        }
        SpringApplication.run(Start.class, args);

    }

}
