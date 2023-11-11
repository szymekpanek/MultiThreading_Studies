package Producent_Konsument_Problem;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//solving problem with BlockingQueue
class Producent implements Runnable {
    private BlockingQueue<Integer> queue;

    public Producent(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 100; ++i) {
                // Wrzuć element na kolejkę
                queue.put(i);
                System.out.println("Producent dodał:" + i);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Producent czas wykonania: " + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Konsument extends Thread {
    private BlockingQueue<Integer> queue;

    public Konsument(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 100; ++i) {
                // Pobierz element z kolejki
                int item = queue.take();
                System.out.println("Konsument pobral: " + item);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Konsument czas wykonania: " + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class PKmon {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Początkowy czas wykonania
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        Producent producent = new Producent(queue);
        Konsument konsument = new Konsument(queue);

        Thread producerThread = new Thread(producent);
        Thread consumerThread = new Thread(konsument);

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis(); // Końcowy czas wykonania
        System.out.println("Całkowity czas wykonania programu: " + (endTime - startTime) + " ms");
    }
}
