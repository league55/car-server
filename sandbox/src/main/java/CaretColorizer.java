package main.java;

import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CaretColorizer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextField redCaretTextField = new TextField("Big black caret");
        redCaretTextField.setSkin(
                new TextFieldCaretControlSkin(
                        redCaretTextField,
                        Color.RED
                )
        );

        VBox layout = new VBox(10, redCaretTextField);

        layout.setPadding(new Insets(10));
        stage.setScene(new Scene(layout));

        stage.show();
    }

    public class TextFieldCaretControlSkin extends TextFieldSkin {
        public TextFieldCaretControlSkin(TextField textField, Color caretColor) {
            super(textField);


            caretPath.strokeProperty().unbind();
            caretPath.fillProperty().unbind();
            caretPath.setStrokeWidth(10);
            caretPath.setStroke(Color.BLACK);
            caretPath.setFill(Color.BLACK);
            caretPath.setTranslateX(10);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}