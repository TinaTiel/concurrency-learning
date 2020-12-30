package atomicoperations;

import java.util.Random;

public class Deadlocks {

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread trainAThread = new Thread(new TrainA(intersection));
        Thread trainBThread = new Thread(new TrainB(intersection));
        trainAThread.start();
        trainBThread.start();
    }

    public static class Intersection {
        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            // Acquire a lock on Road A so we can take it
            synchronized (roadA) {
                System.out.println("Road A is locked by Thread " + Thread.currentThread().getName());

                // Acquire a lock on road B so that another train cannot pass through it
                synchronized (roadB) {
                    System.out.println("Train is passing through Road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        // We can PREVENT a deadlock by ensuring the locks are taken in the same order as takeRoadA
        public void takeRoadB() {
            // Acquire a lock on Road B so we can pass through it
//            synchronized (roadB) { // causes deadlock
            synchronized (roadA) {
                System.out.println("Road B is locked by Thread " + Thread.currentThread().getName());

                // Acquire a lock on Road A so another train cannot pass through it while we cross the intersection
//                synchronized (roadA) { // causes deadlock
                synchronized (roadB) {
                    System.out.println("Train is passing through Road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while(true) {
                long nextDeparture = random.nextInt(5);
                try {
                    Thread.sleep(nextDeparture);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intersection.takeRoadA();
            }
        }
    }

    public static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while(true) {
                long nextDeparture = random.nextInt(5);
                try {
                    Thread.sleep(nextDeparture);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intersection.takeRoadB();
            }
        }
    }

}
