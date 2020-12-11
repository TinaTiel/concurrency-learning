package hackers;

import java.util.Random;

public class Main {

    private static final int MAX_PASSWORD = 9999;

    public static void main(String[] args) {

        // Init the vault w/ password
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        Thread ascendingHacker = new AscendingHacker(vault);
        Thread descendingHacker = new DescendingHacker(vault);
        Thread referee = new Referee();

        ascendingHacker.start();
        descendingHacker.start();
        referee.start();

    }

    public static class Vault {
        private int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int guess) {
            // To slow down 'hackers' we delay the response
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {}
            boolean result = guess == password;
            System.out.println(Thread.currentThread().getName() + " guessed " + guess + (result ? " correctly" : " incorrectly"));
            return guess == password;
        }
    }

    public static abstract class AbstractHackerThread extends Thread {

        protected Vault vault;

        public AbstractHackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(MAX_PRIORITY);
        }

        @Override
        public synchronized void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }

    public static class AscendingHacker extends AbstractHackerThread {

        public AscendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int i=0; i<=MAX_PASSWORD; i++) {
                if(vault.isCorrectPassword(i)) {
                    System.out.println(this.getName() + " guessed the password: " + i);
                    System.exit(0);
                }
            }
        }
    }

    public static class DescendingHacker extends AbstractHackerThread {

        public DescendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int i = MAX_PASSWORD; i >= 0; i--) {
                if(vault.isCorrectPassword(i)) {
                    System.out.println(this.getName() + " guessed the password: " + i);
                    System.exit(0);
                }
            }
        }
    }

    public static class Referee extends Thread {

        public Referee() {
            this.setName("Referee");
        }

        @Override
        public void run() {
            for(int i = 10; i > 0 ; i--) {
                System.out.println("<<<<" +     "<<<<<<<<<<<<<<<<<");
                System.out.println(">>> " + i + "seconds remain...");
                System.out.println("<<<<" +     "<<<<<<<<<<<<<<<<<");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Nice try hackers, game over");
            System.exit(0);
        }

        @Override
        public synchronized void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }

}
