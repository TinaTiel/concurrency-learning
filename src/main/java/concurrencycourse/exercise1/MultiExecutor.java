package concurrencycourse.exercise1;

import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;
import java.lang.Runnable;

public class MultiExecutor {

    // Add any necessary member variables here
    private final List<Runnable> tasks = new ArrayList<>();

    /*
     * @param tasks to executed concurrently
     */
    public MultiExecutor(List<Runnable> tasks) {
        // Complete your code here
        tasks.addAll(tasks);
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        // complete your code here
        for(Runnable task:tasks) {
            Thread thread = new Thread(task);
            thread.start();
        }
    }
}