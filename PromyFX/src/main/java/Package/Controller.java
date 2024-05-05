package Package;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    private Button runButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button pauseResumeButton;
    @FXML
    private TextField shipNumberField;
    @FXML
    private TextField shipTripTimeField;
    @FXML
    private TextField carNumberField;
    @FXML
    private TextField carMaxSpeedField;
    @FXML
    private TextField carMinSpeedField;
    @FXML
    private TextField shipCapacityField;
    @FXML
    private TextField shipWaitTimeField;
    @FXML
    private TextField harbourCapacityField;
    @FXML
    private Slider speedSlider;

    @FXML
    public void initialize() {
        saveButton.setDisable(false);
        runButton.setDisable(true);
        stopButton.setDisable(true);
        pauseResumeButton.setDisable(true);
    }

    @FXML
    protected void onRunButtonAction() {
        runButton.setDisable(true);
        saveButton.setDisable(true);
        stopButton.setDisable(false);
        pauseResumeButton.setDisable(false);
        MainFx.animationStatus = Animation.Status.RUNNING;
        Thread mainThread = new Thread(() -> Configuration.startAnimation());
        mainThread.start();
    }

    @FXML
    protected void onStopButtonAction() {
        runButton.setDisable(false);
        saveButton.setDisable(false);
        stopButton.setDisable(true);
        pauseResumeButton.setDisable(true);
        MainFx.animationStatus = Animation.Status.STOPPED;
        MainFx.config.interruptAnimation();
        MainFx.animationPane.getChildren().clear();
    }

    @FXML
    protected void onPauseResumeButtonAction() {
        if (MainFx.animationStatus == Animation.Status.RUNNING) {
            MainFx.animationStatus = Animation.Status.PAUSED;
            MainFx.config.pauseAnimation();
        } else if (MainFx.animationStatus == Animation.Status.PAUSED) {
            MainFx.animationStatus = Animation.Status.RUNNING;
            MainFx.config.resumeAnimation();
        }
    }

    @FXML
    protected void onSpeedSliderChanged() {
        MainFx.config.simSpeed((int) speedSlider.getValue());
    }

    @FXML
    protected void onSaveButtonAction() {
        try {
            MainFx.animationPane.getChildren().clear();
            if (shipNumberField == null) System.out.println("NULL");
            try {
                MainFx.config.shipNumber = Integer.parseInt(shipNumberField.getText());
            } catch (NumberFormatException e) {
                shipNumberField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.shipCapacity = Integer.parseInt(shipCapacityField.getText());
            } catch (NumberFormatException e) {
                shipCapacityField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.shipTripTime = Integer.parseInt(shipTripTimeField.getText());
            } catch (NumberFormatException e) {
                shipTripTimeField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.shipWaitTime = Integer.parseInt(shipWaitTimeField.getText());
            } catch (NumberFormatException e) {
                shipWaitTimeField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.carNumber = Integer.parseInt(carNumberField.getText());
            } catch (NumberFormatException e) {
                carNumberField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.carMinSpeed = Integer.parseInt(carMinSpeedField.getText());
            } catch (NumberFormatException e) {
                carMinSpeedField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.carMaxSpeed = Integer.parseInt(carMaxSpeedField.getText());
            } catch (NumberFormatException e) {
                carMaxSpeedField.setText("Error");
                e.printStackTrace();
            }
            try {
                MainFx.config.harbourCapacity = Integer.parseInt(harbourCapacityField.getText());
            } catch (NumberFormatException e) {
                harbourCapacityField.setText("Error");
                e.printStackTrace();
            }
            Configuration.draw();
            runButton.setDisable(false);
        } finally {
        }
    }
}

