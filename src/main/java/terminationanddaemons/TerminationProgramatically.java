package terminationanddaemons;

import java.math.BigInteger;

public class TerminationProgramatically {

    public static void main(String[] args) {
        Thread smallTask = new Thread(new BigCalculation("2", "3")); // for small values, it's no problem to stop a thread
        Thread largeTask = new Thread(new BigCalculation("20000", "300000000"));

        smallTask.start();
        largeTask.start();

        // When we call interrupt, it's up to the Thread implementation to check the interruption
        // status and to do something about it; i.e. to return out of an expensive operation
        largeTask.interrupt();

    }

    public static class BigCalculation implements Runnable {

        private final BigInteger base;
        private final BigInteger pow;

        public BigCalculation(String base, String pow) {
            this.base = new BigInteger(base);
            this.pow = new BigInteger(pow);
        }

        public BigInteger pow(BigInteger base, BigInteger pow) throws InterruptedException{
            BigInteger result = BigInteger.ONE;
            for(BigInteger i = BigInteger.ZERO; i.compareTo(pow) != 0; i=i.add(BigInteger.ONE)) {
                // Here is our expensive operation. Each iteration we check if the thread
                // has been interrupted. If it has, then we just return a number.
                // But in practice what we could also do is flag it as interrupted and then
                // throw an Interrupted exception.
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("Large task was interrupted");

                    // Conventional way to respond to the interrupt
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("Expensive calculation was interrupted");

                    // Another (simpler) way is to just return from the expensive operation / end the for-loop
                    //return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }
            return result;
        }

        @Override
        public void run() {

            // Conventional way
            try {
                System.out.println(base + "^" + pow + " = " + pow(base, pow));
            } catch (InterruptedException e) {
                System.out.println("The big calculation thread was interrupted");
            }

            // Simpler way
//            System.out.println(base + "^" + pow + " = " + pow(base, pow));

        }
    }
}
