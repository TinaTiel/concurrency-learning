package atomicoperations;

public class RaceConditions {

    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();

        Thread thread1 = new Thread(() -> {
            for(int i=0; i<Integer.MAX_VALUE; i++) {
                sharedClass.increment();
            }
        });
        Thread thread2 = new Thread(() -> {
            for(int i=0; i<Integer.MAX_VALUE; i++) {
                sharedClass.checkForRaceCondition();
            }
        });

        thread1.start();
        thread2.start();
    }

    public static class SharedClass {

//        private int i = 0;
//        private int j = 0;
        private volatile int i = 0;
        private volatile int j = 0;

        public void increment() {
            // without the volatile keyword, since the below two lines can be logically
            // reorganized in a different order, then the OS may schedule them out of order
            // BUT if we use the volatile keyword it guarantees lines before and lines after
            // use of a volatile variable will be executed (no re-ordering)
            i++;
            j++;
        }

        public void checkForRaceCondition() {
            // The SAME logical reordering ALSO applies to comparison statements like these!
            // We can have j > i, but we can also logically have i < j.
            // One of these causes a race condition on my computer, but not the other!
//            if(j > i) {
            if(i < j) {
                System.out.println("Race condition detected, j > i");
            }
        }
    }

}
