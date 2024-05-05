package Package;

import javafx.scene.shape.Circle;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

class Harbour {
    private int capacity;
    private ReentrantLock harbourLock;
    private ReentrantLock shipInOutLock;
    private ReentrantLock carInOutLock;
    private Queue<Ship> shipsInHarbour;
    private Queue<Car> carsInHarbour;
    private Semaphore semaphore;
    private ReentrantLock waitLock;
    private TranslateTransition animation;

    public Harbour(int capacity) {
        this.capacity = capacity;
        this.harbourLock = new ReentrantLock();
        this.shipInOutLock = new ReentrantLock();
        this.carInOutLock = new ReentrantLock();
        this.shipsInHarbour = new LinkedList<Ship>();
        this.carsInHarbour = new LinkedList<Car>();
        this.semaphore = new Semaphore(1);
        this.waitLock = new ReentrantLock();
    }

    public void shipEnterHarbour(Ship ship) {
        try {
            semaphore.acquire();
            shipInOutLock.lock();
            shipsInHarbour.add(ship);
            System.out.println("  Ship " + ship.getId() + " entered harbour ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shipInOutLock.unlock();
        }
    }

    public void shipLeaveHarbour(Ship ship) {
        try {
            semaphore.release();
            shipInOutLock.lock();
            shipsInHarbour.remove(ship);
        } finally {
            shipInOutLock.unlock();
        }
    }

    public int carEnterHarbour(Car car, Circle dot) {
        while (true) {
            try {
                while (carsInHarbour.size() == capacity)
                    Thread.sleep(100);
                carInOutLock.lock();
                if (carsInHarbour.size() < capacity) {
                    Thread.sleep(car.getSpeed());
                    carsInHarbour.add(car);
                    System.out.println("Harbour <<< Car " + car.getId() + " (" + carsInHarbour.size() + "/" + capacity + ")");
                    animation = new TranslateTransition(Duration.seconds(car.getSpeed()), dot);
                    animation.setFromX(Configuration.parkingPos[car.getId()][0]);
                    animation.setFromY(Configuration.parkingPos[car.getId()][1]);
                    animation.setToX(Configuration.harbourPos[carsInHarbour.size()-1][0]);
                    animation.setToY(Configuration.harbourPos[carsInHarbour.size()-1][1]);
                    animation.play();
                    return carsInHarbour.size();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                carInOutLock.unlock();
            }
        }
    }

    private Ship waitForShip() {
        waitLock.lock();
        while (true) {
            while (shipsInHarbour.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            harbourLock.lock();
            try {
                Ship ship = shipsInHarbour.peek();
                if (ship != null)
                    if (ship.getCarsOnBoard() < ship.getCarCapacity()) {
                        return ship;
                    }
            } finally {
                harbourLock.unlock();
            }
        }
    }

    public void carTrip(Car car, Circle dot, int harbourPos) {
        Ship ship = waitForShip();
        carsInHarbour.remove(car);
        harbourLock.lock();
        ship.embarkCar(car, dot, harbourPos);
        harbourLock.unlock();
        waitLock.unlock();
        ship.disembarkCar(car, dot);
    }

    public void carLeaveHarbour(Car car) {
        carInOutLock.lock();
        try {
            Thread.sleep(car.getSpeed());
            System.out.println("Harbour >>> Car " + car.getId() + " (" + carsInHarbour.size() + "/" + capacity + ")");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            carInOutLock.unlock();
        }
    }
}
