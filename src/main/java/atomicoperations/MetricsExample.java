package atomicoperations;

import java.util.Random;

public class MetricsExample {

    public static void main(String[] args) {

        Metrics metrics = new Metrics();
        BusinessLogic businessLogic1 = new BusinessLogic(metrics);
        BusinessLogic businessLogic2 = new BusinessLogic(metrics);
        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

        businessLogic1.start();
        businessLogic2.start();
        metricsPrinter.start();

    }

    static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;  // <- VOLATILE keyword allows threads to use the variable simulataneously
                                                // but ENSURES any updates are visible to all threads; due to OS optimizations
                                                // it's possible a given thread may NEVER see an update due to cache incoherence.
                                                // FURTHER any write to a volatile field always happens before the read

        public synchronized void addSample(long sample) { // <- SYNCHRONIZED only allows ONE thread to call method at a
                                                          // time; has nothing to do with memory visibility, like in the volatile keyword
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        public double getAverage() {
            return average;
        }

    }

    // Simulate some business logic by sleeping a random amount of time
    static class BusinessLogic extends Thread {

        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                long start = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {}

                long end = System.currentTimeMillis();

                metrics.addSample(end - start);
            }
        }
    }

    // prints metrics to the screen
    static class MetricsPrinter extends Thread {

        private Metrics metrics;

        public MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                double currentAverage = metrics.getAverage();
                System.out.println("Current average is: " + currentAverage);
            }
        }
    }

}
