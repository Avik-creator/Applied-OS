class PetersonLock {
    private volatile int turn;
    private volatile boolean[] interested = new boolean[2];

    public void enterRegion(int process) {
        int other = 1 - process;
        interested[process] = true;
        turn = process;
        while (turn == process && interested[other]) {
        }
    }

    public void leaveRegion(int process) {
        interested[process] = false;
    }
}

class SharedCounter {
    private int x = 0;

    public void increment() {
        x++;
    }

    public int getValue() {
        return x;
    }
}

class ThreadTask implements Runnable {
    private final int process;
    private final PetersonLock lock;
    private final SharedCounter counter;

    public ThreadTask(int process, PetersonLock lock, SharedCounter counter) {
        this.process = process;
        this.lock = lock;
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            lock.enterRegion(process);
            try {
                counter.increment();
                System.out.println("Thread " + process + " x = " + counter.getValue());
            } finally {
                lock.leaveRegion(process);
            }
        }
    }
}

public class petersonSolution {
    public static void main(String[] args) {
        PetersonLock lock = new PetersonLock();
        SharedCounter counter = new SharedCounter();

        Thread t1 = new Thread(new ThreadTask(0, lock, counter));
        Thread t2 = new Thread(new ThreadTask(1, lock, counter));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}