import java.util.concurrent.locks.ReentrantLock;

class ThreadObject implements Runnable {
    private final ReentrantLock lock;
    private final Counter counter;

    public ThreadObject(ReentrantLock lock, Counter counter) {
        this.lock = lock;
        this.counter = counter;
    }

    @Override // Purpose -> Telling the Thread what to do when the thread is Executed. It
              // overrides the default Run method of Runnable Interface.
    public void run() {
        for (int i = 0; i < 10000; i++) {
            lock.lock();
            try {
                counter.increment();
            } finally {
                lock.unlock();
            }
        }
    }
}

class Counter {
    private int value;

    public Counter() {
        this.value = 0;
    }

    public void increment() {

        value += 1;
    }

    public int getValue() {
        return value;
    }
}

public class Main {
    public static void main(String[] args) {
        Counter counter = new Counter();
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread(new ThreadObject(lock, counter));
        Thread t2 = new Thread(new ThreadObject(lock, counter));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final Value of x: " + counter.getValue());
    }
}
