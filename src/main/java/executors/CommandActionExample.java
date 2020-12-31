package executors;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandActionExample {

    public static void main(String[] args) {

        // Initialize some starting params
        Random random = new Random();
        int maxActions = 20;
        int maxCommands = 5;

        // Generate some commands, with a random number of actions.
        // We'll use the indexes as the command and action names to keep it simple/readable
        List<Command> commands = new ArrayList<>();
        for(Integer c = 0; c < maxCommands; c++) {
            Command command = new Command(String.format("%d", c+1));
            for(Integer a = 0; a < random.nextInt(maxActions); a++) {
                Action action = new Action(random, String.format("%d", a+1));
                command.addAction(action);
            }
            commands.add(command);
        }

        // Print out the commands we'll execute, again to keep the results readable/understandable
        System.out.println("Commands to execute: \n" + commands.stream().map(Command::toString).collect(Collectors.joining("\n")) + "\n");

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for(Command command:commands) executorService.submit(command);

        // When we execute the results, the actions should be executed in-order within a command at some point in the future
        // (not started all at once), so something like:
        // 0  Command-2:Action-1 scheduled at 34
        // 0  Command-1:Action-1 scheduled at 21
        // 0  Command-3:Action-1 scheduled at 4
        // 4  Command-3:Action2 scheduled at ...
        // 21 Command-1:Action-2 scheduled at ...
        // 34 Command-1-Action-2 scheduled at ...
        // ...
        // Now how to test this...Maybe with JUnit inOrder.verify(...).run() ?

    }

    public static class Action implements Runnable {

        private Command command;
        private final Random random;
        private final String name;

        public Action(Random random, String name) {
            this.random = random;
            this.name = name;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        @Override
        public void run() {

            // Simply sleep for a random period of time. This simulates pieces of work being done (network request, etc.)
            long msTime = random.nextInt(1000);
            System.out.println(new Timestamp(System.currentTimeMillis()) + ": Command-" + command.name + ":Action-" + name + " executing on Thread '" + Thread.currentThread().getName() + "' executing for " + msTime + "ms");
            try {
                Thread.sleep(msTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Action{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class Command implements Runnable {

        private final String name;
        private final List<Action> actions = new ArrayList<>();

        public Command(String name) {
            this.name = name;
        }

        public void addAction(Action action) {
            action.setCommand(this);
            actions.add(action);
        }

        @Override
        public void run() {
            // If there are no actions, then do nothing
            if(actions.isEmpty()) return;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            for(Action action:actions) executor.submit(action);

        }

        @Override
        public String toString() {
            return "Command{" +
                    "name='" + name + '\'' +
                    ", actions=" + actions +
                    '}';
        }
    }

}
