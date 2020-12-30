package locks;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {

    private static final int MAX_PRICE = 1000;

    public static void main(String[] args) {

        InventoryDatabase inventoryDatabase = new InventoryDatabase();

        // Populate the database
        Random random = new Random(9384572093475L);
        for (int i=0; i < 10000; i++) {
            inventoryDatabase.addItem(random.nextInt(MAX_PRICE));
        }

        // Create a writer thread that adds and removes items at random
        Thread writer = new Thread(() -> {
           while(true) {
               inventoryDatabase.addItem(random.nextInt(MAX_PRICE));
               inventoryDatabase.removeItem(random.nextInt(MAX_PRICE));

               try {
                   Thread.sleep(10);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });

        writer.setDaemon(true);
        writer.start();

        // Create many reader threads and set some benchmarks for performance
        int numReaderThreads = 7;
        List<Thread> readerThreads = new ArrayList<>(numReaderThreads);
        for(int threadIndex=0; threadIndex<numReaderThreads; threadIndex++) {
            Thread reader = new Thread(() -> {
               for(int i=0; i<100000; i++) {
                   int upperBoundPrice = random.nextInt(MAX_PRICE);
                   int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                   inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
               }
            });
            reader.setDaemon(true);
            readerThreads.add(reader);
        }

        long start = System.currentTimeMillis();
        for(Thread reader:readerThreads) {
            reader.start();
        }
        for(Thread reader:readerThreads) {
            try {
                reader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        System.out.println(String.format("Reader thread work took %d ms", end - start));
    }

    public static class InventoryDatabase {

        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantLock lock = new ReentrantLock();                   // Takes approx 2000 ms
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // Takes approx 600 ms
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
//            lock.lock();
            readLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);

                if(fromKey == null || toKey == null) return 0;

                NavigableMap<Integer, Integer> rangeOfPrice = priceToCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;
                for(int numberOfItemsForPrice:rangeOfPrice.values()) {
                    sum += numberOfItemsForPrice;
                }

                return sum;
            } finally {
//                lock.unlock();
                readLock.unlock();
            }
        }

        public void addItem(int price) {
//            lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if(numberOfItemsForPrice == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice + 1);
                }
            } finally {
//                lock.unlock();
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
//            lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if(numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice - 1);
                }
            } finally {
//                lock.unlock();
                writeLock.unlock();
            }
        }
    }

}
