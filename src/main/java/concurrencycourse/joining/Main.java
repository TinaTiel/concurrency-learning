package concurrencycourse.joining;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Long> inputs = Arrays.asList(0L, 32L, 2342L, 456464L, 455L, 20L, 9899L, 67894L, 23L);

        List<FactorialThread> factorialThreads = new ArrayList<>();
        for(Long input:inputs) factorialThreads.add(new FactorialThread(input));
        for(Thread factorialThread:factorialThreads) {
            factorialThread.setDaemon(true); // If we have a long-running thread, we want it to stop when main stops
            factorialThread.start();

            // Make the parent thread (main) wait until the factorial thread has finished its work; i.e. until the factorial
            // thread has joined the parent.
            // However, if we have a long running calculation this could mean the main thread waits "too long". So, we can
            // provide a timeout argument (2 seconds in this case) to interrupt the long-running factorial thread after
            // timeout. (BUT bear in mind that, again, it's up to us to make the thread respond to the interrupt otherwise
            // it will happily continue running despite the interrupt signal -- above, we've set it to be a daemon to solve
            // that problem. Refer to the concurrencycourse.terminationanddaemons package for the programatic example)
            try {
                factorialThread.join(2000);
            } catch (InterruptedException e) {
                System.out.println("Could not make factorial thread " + factorialThread.getName() + " join " + Thread.currentThread().getName());
                e.printStackTrace();
            }
        }

        // Print the calculation results
        for(int i = 0; i< inputs.size(); i++) {
            FactorialThread correspondingThread = factorialThreads.get(i);
            if(correspondingThread.isComplete()) {
                System.out.println("Factorial of " + inputs.get(i) + " is: " + correspondingThread.getResult());
            } else {
                System.out.println("Factorial of " + inputs.get(i) + " could not be calculated in-time");
            }
        }


    }

    public static class FactorialThread extends Thread {
        private final long input;
        private BigInteger result;
        private boolean complete;

        public FactorialThread(long input) {
            this.input = input;
            this.complete = false;
            this.result = BigInteger.ZERO;
        }

        public BigInteger factorial(long input) {
            BigInteger result = BigInteger.ONE;
            for(long i = input; i > 0; i--) {
                result = result.multiply(new BigInteger(String.valueOf(i)));
            }
            return result;
        }

        @Override
        public void run() {
            result = factorial(input);
            complete = true;
        }

        public boolean isComplete() { return complete; }

        public BigInteger getResult() { return result; }

    }
}
