package concurrencycourse.terminationanddaemons;

import java.math.BigInteger;

public class TerminationWithMain {

    public static void main(String[] args) {
        Thread smallTask = new Thread(new BigCalculation("2", "3")); // for small values, it's no problem to stop a thread
        Thread largeTask = new Thread(new BigCalculation("20000", "300000000"));

        // Set the large task as a daemon; it will not prevent the process from exiting when the main thread terminates
        // or in other words, a daemon will exit with the main thread
        largeTask.setDaemon(true);

        smallTask.start();
        largeTask.start();
        largeTask.interrupt();
    }

    public static class BigCalculation implements Runnable {

        private final BigInteger base;
        private final BigInteger pow;

        public BigCalculation(String base, String pow) {
            this.base = new BigInteger(base);
            this.pow = new BigInteger(pow);
        }

        public BigInteger pow(BigInteger base, BigInteger pow) {
            BigInteger result = BigInteger.ONE;
            for(BigInteger i = BigInteger.ZERO; i.compareTo(pow) != 0; i=i.add(BigInteger.ONE)) {
                result = result.multiply(base);
            }
            return result;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + pow + " = " + pow(base, pow));
        }
    }
}
