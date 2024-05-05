package Package;

import javafx.animation.Animation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFx extends Application {
    public static AnchorPane animationPane;
    public static Configuration config;
    public static Animation.Status animationStatus = Animation.Status.STOPPED;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlloader = new FXMLLoader(MainFx.class.getResource("view.fxml"));
        SplitPane root = fxmlloader.load();
        animationPane = (AnchorPane) root.getItems().get(1);
        Scene scene = new Scene(root, 1010, 600);
        stage.setTitle("Symulacja projektu promy");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}