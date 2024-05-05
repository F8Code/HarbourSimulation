package Package;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class Car implements Runnable {
    private int id;
    private int speed;
    private Harbour harbour;
    private Circle dot;
    private int harbourPos;
    public Car(int id, int speed, Harbour harbour) {
        this.id = id;
        this.speed = speed;
        this.harbour = harbour;
        dot = new Circle(10);
        dot.setFill(Color.RED);
        Platform.runLater(() -> {
            MainFx.animationPane.getChildren().add(dot);
        });
    }

    public int getId() {
        return id;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            harbourPos = harbour.carEnterHarbour(this, dot);
            harbour.carTrip(this, dot, harbourPos);
            harbour.carLeaveHarbour(this);
            try {
                Thread.sleep(speed*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}