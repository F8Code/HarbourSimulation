package Package;

import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random rand = new Random();
        int numShips = 5;
        int numCars = 23;
        int capacity = 10;
        int numSequences = 60;
        Harbour harbour = new Harbour(capacity);

        Thread[] ships = new Thread[numShips];
        Thread[] cars = new Thread[numCars];
        for (int i = 0; i < numShips; i++) {
            ships[i] = new Thread(new Ship(i, 5, 5000, 1000, harbour));
            ships[i].start();
        }
        for (int i = 0; i < numCars; i++) {
            cars[i] = new Thread(new Car(i, rand.nextInt(100)*10, harbour));
            cars[i].start();
        }

        try {
            TimeUnit.SECONDS.sleep(numSequences);
        } catch (InterruptedException e) {
            for (int i = 0; i < numShips; i++) {
                ships[i].interrupt();
            }
            for (int i = 0; i < numCars; i++) {
                cars[i].interrupt();
            }
            e.printStackTrace();
        }

        for (int i = 0; i < numShips; i++) {
            try {
                ships[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < numCars; i++) {
            try {
                cars[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

