import java.util.*;
import java.util.concurrent.Semaphore;

public class producerConsumer {
    private static final int MAX_BUFFER_SIZE = 10;
    private static final Queue<Integer> buffer = new LinkedList<>();
    private static final Semaphore emptySlots = new Semaphore(MAX_BUFFER_SIZE);
    private static final Semaphore filledSlots = new Semaphore(0);
    private static final Object lock = new Object();

    public static void producer(int val) {
        try {
            emptySlots.acquire(); // Wait for an empty slot

            synchronized (lock) {
                buffer.add(val);
                System.out.println("Produced: " + val);
                System.out.println("Buffer size After Producing: " + buffer.size());
            }

            filledSlots.release(); // Signal that an item is available
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void consumer() {
        try {
            // Wait until there's at least one item in the buffer
            // This decrements the filledSlots permit count
            filledSlots.acquire();

            int val;
            synchronized (lock) {
                val = buffer.poll(); // Retrieve and remove the head of the queue
                System.out.println("Consumed: " + val);
                System.out.println("Buffer size After Consuming: " + buffer.size());
            }

            emptySlots.release(); // Signal that an empty slot is available
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Thread producerThread = new Thread(() -> {
            for (int i = 1; i <= 20000; i++) {
                producer(i);
            }
        });

        Thread consumerThread = new Thread(() -> {
            for (int i = 1; i <= 20000; i++) {
                consumer();
            }
        });

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Processing complete.");
    }

}
