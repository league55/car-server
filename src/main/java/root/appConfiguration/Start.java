package root.appConfiguration;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"root.app", "root.app.model", "root.appConfiguration"})
public class Start extends Application {
    private ConfigurableApplicationContext springContext;
    private Parent root;

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(Start.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Start.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Scene scene = new Scene(root, 1300, 800);
            // scene
            primaryStage.setTitle("Speedy");
            primaryStage.setScene(scene);

            // show the GUI
            primaryStage.show();

            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        springContext.stop();
    }

    public static void main(String[] args) {
        // load the native OpenCV library
        log.info(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_ffmpeg331_64");

        launch(Start.class, args);
    }

}
