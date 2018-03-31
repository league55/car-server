package root.appConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.xeustechnologies.jcl.JarClassLoader;

import java.net.URL;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"root.app", "root.app.model", "root.appConfiguration"})
@EnableCaching
public class Start {
    private final static String ACWRAPPER = "opencv_java331";

    public static void main(String[] args) {
        // load the native OpenCV library
//        nu.pattern.OpenCV.loadShared();
//        nu.pattern.OpenCV.loadLocally();
        try {
            JarClassLoader jcl = new JarClassLoader();

            jcl.add(new URL("opencv_java331.dll"));

        } catch (Exception e) {
            log.error("failed to load dll from jar", e);
            System.loadLibrary("opencv_java331");
        }
        int rate = 10;
        int amount = 1 - rate/100*1 - rate/100;
        SpringApplication.run(Start.class, args);

    }

}
