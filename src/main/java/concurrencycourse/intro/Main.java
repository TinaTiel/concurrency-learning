package concurrencycourse.intro;

public class Main {
    public static void main(String[] args) {

        // Here we instantiate a thread, defining what it will do when run by passing in a Runnable
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Integer someVariableInsideWorkerThread = 57;
                System.out.println("We are now in thread: " + Thread.currentThread().getName());
                System.out.println("The thread's priority is: " + Thread.currentThread().getPriority());
                throw new RuntimeException("Some intentional exception");
            }
        });

        Integer someMainThreadVar = 27;

        // Set the thread's name so it's easier to debug
        thread.setName("My Worker Thread");

        // Set the priority of our thread
        thread.setPriority(Thread.MAX_PRIORITY);

        // Set the exception handler so something happens when an exception is thrown inside the thread
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                System.out.println("An exception was thrown in thread " + thread.getName() + ": " + throwable.getMessage());
            }
        });

        System.out.println("We are now in thread: " + Thread.currentThread().getName() + " before starting a new thread");
        thread.start(); // creates a new thread and invokes run()
        System.out.println("We are now in thread: " + Thread.currentThread().getName() + " after starting a new thread");

    }
}
