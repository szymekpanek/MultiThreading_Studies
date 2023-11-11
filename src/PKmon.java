import java.util.LinkedList;

class Producent extends Thread {
    private Bufor _buf; // Bufor, do którego producent będzie dodawał elementy
    private int producentId;

    public Producent(Bufor buf, int id) {
        _buf = buf;
        producentId = id;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            long startTime = System.nanoTime();
            _buf.put(i);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // czas w milisekundach
            //System.out.println("Producent " + producentId + " dodal do bufora: " + i + " w czasie " + duration + " ms");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Konsument extends Thread {
    private Bufor _buf;
    private int konsumentId;

    public Konsument(Bufor buf, int id) {
        _buf = buf;
        konsumentId = id;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            long startTime = System.nanoTime();
            int value = _buf.get();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // czas w milisekundach
            //System.out.println("Konsument " + konsumentId + " pobral: " + value + " w czasie " + duration + " ms");
            try {
                Thread.sleep(100); // Symulacja czasu konsumpcji
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Bufor {
    private LinkedList<Integer> buffer = new LinkedList<>();
    private int capacity = 10;

    public synchronized void put(int i) {
        while (buffer.size() == capacity) {
            try {
                wait(); // Czekaj, jeśli bufor jest pełny
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        buffer.add(i);
        notify(); // Powiadom konsumentów, że dodano element do bufora
    }

    public synchronized int get() {
        while (buffer.isEmpty()) {
            try {
                wait(); // Czekaj, jeśli bufor jest pusty
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int value = buffer.removeFirst();
        notify(); // Powiadom producentów, że pobrano element z bufora
        return value;
    }
}

public class PKmon {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Początkowy czas wykonania

        Bufor buf = new Bufor();
        Producent producent = new Producent(buf, 1);
        Konsument konsument = new Konsument(buf, 1);

        producent.start();
        konsument.start();

        try {
            producent.join();
            konsument.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis(); // Końcowy czas wykonania
        long totalExecutionTime = endTime - startTime; // Całkowity czas wykonania w milisekundach

        System.out.println("Całkowity czas wykonania programu: " + totalExecutionTime + " ms");
    }
}
