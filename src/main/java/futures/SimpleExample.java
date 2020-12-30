package futures;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleExample {

    public static void main(String[] args) {

        // Generate a bunch of random actions, and execute them in order
        Random random = new Random();
        int maxActions = 20;
        int maxCommands = 5;

        List<Command> commands = new ArrayList<>();
        for(Integer c = 0; c < maxCommands; c++) {
            Command command = new Command(String.format("%d", c+1));
            for(Integer a = 0; a < random.nextInt(maxActions); a++) {
                Action action = new Action(random, String.format("%d", a+1));
                command.addAction(action);
            }
            commands.add(command);
        }

        System.out.println("Commands to execute: \n" + commands.stream().map(Command::toString).collect(Collectors.joining("\n")) + "\n");
        try {
            CompletableFuture.allOf(commands.stream()
                    .map((Function<Command, CompletableFuture<Void>>) CompletableFuture::runAsync)
                    .collect(Collectors.toList())
                    .toArray(CompletableFuture[]::new)
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        commands.get(0).run();

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
            if(actions.isEmpty()) return;
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(actions.remove(0));
            Collections.reverse(actions);
            for(int i=0; i< actions.size(); i++) {
                completableFuture.thenRun(actions.get(i));
            }
            try {
                completableFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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
