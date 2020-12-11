package terminationanddaemons;

public class TerminationByCatchingInterrupException {

    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingWork());

        thread.start();

        thread.interrupt(); // interrupt the Thread referenced by the thread variable

        System.out.println("finished " + Thread.currentThread().getName());
    }

    public static class BlockingWork implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(99999999);    // This particular method will throw the InterruptedException when interrupted
                                                // so we can catch it and do something
            } catch (InterruptedException e) {
                System.out.println("Ended the blocking work/thread");
            }
        }
    }

}
