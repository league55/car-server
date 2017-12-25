package main.java;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Train extends Application {

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 260, 80);
        stage.setScene(scene);


        Group g = new Group();
        PerspectiveTransform pt = new PerspectiveTransform();
        pt.setUlx(10.0);
        pt.setUly(10.0);
        pt.setUrx(310.0);
        pt.setUry(40.0);
        pt.setLrx(310.0);
        pt.setLry(60.0);
        pt.setLlx(10.0);
        pt.setLly(90.0);

        g.setEffect(pt);
        g.setCache(true);

        Rectangle r = new Rectangle();
        r.setX(10.0);
        r.setY(10.0);
        r.setWidth(280.0);
        r.setHeight(80.0);
        r.setFill(Color.BLUE);

        Text t = new Text();
        t.setX(20.0);
        t.setY(65.0);
        t.setText("JavaFX");
        t.setFill(Color.YELLOW);
        t.setFont(Font.font(null, FontWeight.BOLD, 36));

        g.getChildren().add(r);
        g.getChildren().add(t);

        scene.setRoot(g);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}