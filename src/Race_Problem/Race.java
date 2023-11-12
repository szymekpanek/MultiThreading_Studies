package Race_Problem;
// solving race problem
class Counter {
    private int _val; // licznik
    public Counter(int n) { // konstruktor
        _val = n;
    }
    public void inc() { // inkrementacja
        _val++;
    }
    public void dec() { // dekrementacja
        _val--;
    }
    public int value() { // odczyt licznika
        return _val;
    }
}

class Semaphore {
    private boolean _stan = true;
    public synchronized void P() {
        while (!_stan) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _stan = false;
    }

    public synchronized void V() {
        _stan = true;
        notify();
    }
}

// Watek, ktory inkrementuje licznik 100.000 razy
class IThread extends Thread {
    // Watek, ktory inkrementuje licznik 100.000 razy
    private Counter counter;
    private Semaphore semaphore;

    public IThread(Counter cnt, Semaphore semaphore) {
        this.counter = cnt;
        this.semaphore = semaphore;
    }


    @Override
    public void run() {
        for (int i = 0; i < 100000 ; i++) {
            semaphore.P();
            counter.inc();
            System.out.println("IThread: " + counter.value());
            semaphore.V();
        }
    }
}

// Watek, ktory dekrementuje licznik 100.000 razy
class DThread extends Thread {
    private final Counter counter;
    private Semaphore semaphore;

    public DThread(Counter counter, Semaphore semaphore) { // konstruktor wątku
        this.counter = counter;
        this.semaphore = semaphore;
    }
    @Override
    public void run () {
        for (int i = 0; i < 100000 ; i++) {
            semaphore.P();
            counter.dec();
            System.out.println("DThread: " + counter.value());
            semaphore.V();
        }
    }
}

public class Race {
    public static void main(String[] args) throws InterruptedException {
        int[] results = new int[10];

        for (int run = 0; run < 10; run++) {
            Counter cnt = new Counter(0);
            Semaphore semaphore = new Semaphore();

            IThread iThread = new IThread(cnt, semaphore);
            DThread dThread = new DThread(cnt, semaphore);

            iThread.start();
            dThread.start();

            iThread.join();
            dThread.join();

            int finalValue = cnt.value();
            results[run] = finalValue;
            System.out.println("Wartosc licznika: " + cnt.value());
        }

    }
}