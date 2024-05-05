package Package;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

class Ship implements Runnable {
    private int id;
    private int carCapacity;
    private int tripTime;
    private int waitTime;
    private Harbour harbour;
    private ReentrantLock embarkLock;
    private ReentrantLock disembarkLock;
    private Queue<Car> carsOnBoard;
    private boolean tripComplete;
    private boolean newCar;
    private TranslateTransition animation;

    public Ship(int id, int carCapacity, int tripTime, int waitTime, Harbour harbour) {
        this.id = id;
        this.carCapacity = carCapacity;
        this.tripTime = tripTime;
        this.waitTime = waitTime;
        this.harbour = harbour;
        this.embarkLock = new ReentrantLock();
        this.disembarkLock = new ReentrantLock();
        this.carsOnBoard = new LinkedList<Car>();
        this.tripComplete = false;
        this.newCar = false;
    }

    public int getId() {
        return id;
    }

    public int getCarsOnBoard() {
        return carsOnBoard.size();
    }

    public int getCarCapacity() {
        return carCapacity;
    }

    public void addCar(Car car) {
        carsOnBoard.add(car);
    }

    public void removeCar(Car car) {
        carsOnBoard.remove(car);
    }

    public void embarkCar(Car car, Circle dot, int harbourPos) {
        embarkLock.lock();
        try {
            while (tripComplete)
                Thread.sleep(100);
            newCar = true;
            Thread.sleep(car.getSpeed());
            carsOnBoard.add(car);
            System.out.println("  Ship " + this.getId() + " <<< " + "Car " + car.getId() + " [" + carsOnBoard.size() + "/" + carCapacity + "]");
            animation = new TranslateTransition(Duration.seconds(car.getSpeed()), dot);
            animation.setFromX(Configuration.harbourPos[harbourPos-1][0]);
            animation.setFromY(Configuration.harbourPos[harbourPos-1][1]);
            animation.setToX(Configuration.shipPos[id*carCapacity+carsOnBoard.size()-1][0]);
            animation.setToY(Configuration.shipPos[id*carCapacity+carsOnBoard.size()-1][1]);
            animation.play();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            embarkLock.unlock();
        }
    }

    public void disembarkCar(Car car, Circle dot) {
        disembarkLock.lock();
        while (!tripComplete) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(car.getSpeed());
            carsOnBoard.remove(car);
            System.out.println("  Ship " + this.getId() + " >>> " + "Car " + car.getId() + " [" + carsOnBoard.size() + "/" + carCapacity + "]");
            animation = new TranslateTransition(Duration.seconds(car.getSpeed()), dot);
            animation.setFromX(Configuration.shipPos[id*carCapacity+carsOnBoard.size()][0]);
            animation.setFromY(Configuration.shipPos[id*carCapacity+carsOnBoard.size()][1]);
            animation.setToX(Configuration.parkingPos[car.getId()][0]);
            animation.setToY(Configuration.parkingPos[car.getId()][1]);
            animation.play();
            disembarkLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        harbour.shipEnterHarbour(this);
        while (!Thread.interrupted()) {
            try {
                while (carsOnBoard.size() < carCapacity) {
                    Thread.sleep(waitTime);
                    if (newCar) newCar = false;
                    else break;
                }
                System.out.println("  Ship " + id + " left harbour with " + carsOnBoard.size() + " cars on board.");
                harbour.shipLeaveHarbour(this);
                try {
                    Thread.sleep(tripTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                harbour.shipEnterHarbour(this);
                tripComplete = true;
                while (!carsOnBoard.isEmpty()) {
                    Thread.sleep(100);
                }
                tripComplete = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
