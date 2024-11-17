class TurnManager {
    private int turn;

    public TurnManager(int initialTurn) {
        this.turn = initialTurn;
    }

    public void waitTurn(int process) {
        while (turn != process) {
        }
    }

    public void setTurn(int process) {
        this.turn = process;
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
    private final TurnManager turnManager;
    private final SharedCounter counter;

    public ThreadTask(int process, TurnManager turnManager, SharedCounter counter) {
        this.process = process;
        this.turnManager = turnManager;
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            turnManager.waitTurn(process);
            try {
                counter.increment();
                System.out.println("Thread " + process + " x = " + counter.getValue());
            } finally {
                turnManager.setTurn(1 - process);
            }
        }
    }
}

public class waitTurn {
    public static void main(String[] args) {
        TurnManager turnManager = new TurnManager(0);
        SharedCounter counter = new SharedCounter();

        Thread t1 = new Thread(new ThreadTask(0, turnManager, counter));
        Thread t2 = new Thread(new ThreadTask(1, turnManager, counter));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final value of x = " + counter.getValue());
    }
}
