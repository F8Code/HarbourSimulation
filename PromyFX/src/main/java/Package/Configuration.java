package Package;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Configuration {
    public static int shipNumber;
    public static int shipCapacity;
    public static int shipTripTime;
    public static int carNumber;
    public static int carMaxSpeed;
    public static int carMinSpeed;
    public static int shipWaitTime;
    public static int harbourCapacity;
    public static int speed;
    static Thread[] ships;
    static Thread[] cars;
    public static int[][] parkingPos;
    public static int[][] harbourPos;
    public static int[][] shipPos;

    public static void simSpeed(int speed) {
        Configuration.speed = speed;
    }

    public static void pauseAnimation() {

    }

    public static void resumeAnimation() {

    }

    public static void startAnimation() {
        Random rand = new Random();
        Harbour harbour = new Harbour(harbourCapacity);
        Thread[] ships = new Thread[shipNumber];
        Thread[] cars = new Thread[carNumber];
        for (int i = 0; i < shipNumber; i++) {
            ships[i] = new Thread(new Ship(i, shipCapacity, shipTripTime, shipWaitTime, harbour));
            ships[i].start();
        }
        for (int i = 0; i < carNumber; i++) {
            cars[i] = new Thread(new Car(i, rand.nextInt(carMaxSpeed - carMinSpeed) + carMinSpeed, harbour));
            cars[i].start();
        }

        for (int i = 0; i < shipNumber; i++) {
            try {
                ships[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < carNumber; i++) {
            try {
                cars[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void interruptAnimation() {
        for (int i = 0; i < shipNumber; i++) {
            ships[i].interrupt();
        }
        for (int i = 0; i < carNumber; i++) {
            cars[i].interrupt();
        }
    }

    public static void draw() {
        parkingPos = new int[carNumber][2];
        harbourPos = new int[harbourCapacity][2];
        shipPos = new int[shipNumber*shipCapacity][2];
        //display ship
        int squareSize = 30;
        int gapSize = 10;
        double totalShipWidth = (squareSize * shipNumber) + (gapSize * (shipNumber - 1));
        double shipStartX = (MainFx.animationPane.getPrefWidth() - totalShipWidth) / 2;
        for (int i = 0; i < shipNumber; i++) {
            Rectangle rectangle = new Rectangle(shipStartX + (i * (squareSize + gapSize)), MainFx.animationPane.getPrefHeight() / 2, squareSize, shipCapacity * squareSize);
            rectangle.setFill(Color.BLUE);
            MainFx.animationPane.getChildren().add(rectangle);
            for (int j = 0; j < shipCapacity; j++) {
                Rectangle innerRectangle = new Rectangle(rectangle.getX(), rectangle.getY() + j * squareSize, squareSize, squareSize);
                innerRectangle.setFill(Color.LIGHTGRAY);
                innerRectangle.setStroke(Color.BLACK);
                MainFx.animationPane.getChildren().add(innerRectangle);
                shipPos[i*shipCapacity+j][0] = (int) innerRectangle.getX() + 15; //x coordinate
                shipPos[i*shipCapacity+j][1] = (int) innerRectangle.getY() + 15; //y coordinate
            }
        }
        //display harbour
        double totalHarborWidth = (squareSize * harbourCapacity);
        double harborStartX = (MainFx.animationPane.getPrefWidth() - totalHarborWidth) / 2;
        int y = 250;
        for (int i = 0; i < harbourCapacity; i++) {
            Rectangle rectangle = new Rectangle(harborStartX + (i * squareSize), y, squareSize, squareSize);
            rectangle.setFill(Color.CADETBLUE);
            rectangle.setStroke(Color.BLACK);
            MainFx.animationPane.getChildren().add(rectangle);
            harbourPos[i][0] = (int) rectangle.getX() + 15; //x coordinate
            harbourPos[i][1] = (int) rectangle.getY() + 15; //y coordinate
        }
        //display parking
        int parkingHeight = (int) Math.ceil(carNumber / 20.0);
        double totalParkingWidth = squareSize * 20;
        double totalParkingHeight = squareSize * parkingHeight;
        double parkingStartX = (MainFx.animationPane.getPrefWidth() - totalParkingWidth) / 2;
        double parkingStartY = y - totalParkingHeight - 50;
        for (int i = 0; i < parkingHeight; i++) {
            for (int j = 0; j < 20; j++) {
                Rectangle rectangle = new Rectangle(parkingStartX + (j * squareSize), parkingStartY + (i * squareSize), squareSize, squareSize);
                rectangle.setFill(Color.DARKGRAY);
                rectangle.setStroke(Color.BLACK);
                MainFx.animationPane.getChildren().add(rectangle);
                if (i*20+j < carNumber) {
                    parkingPos[i * 20 + j][0] = (int) rectangle.getX() + 15; //x coordinate
                    parkingPos[i * 20 + j][1] = (int) rectangle.getY() + 15; //y coordinate
                }
            }
        }
    }
}
