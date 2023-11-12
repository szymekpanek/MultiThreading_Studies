class Sync {
    public int tura = 1; // 1 - tura procesu 1, 2 - tura procesu 2
    public int tury = 0; // Licznik tur

    public synchronized void kolejnaTura() {
        tura = (tura == 1) ? 2 : 1;
        tury++;

        if (tury >= 10) {
            notifyAll(); // Odblokuj wszystkie wątki, które mogą czekać
        }
    }
}

class T1 extends Thread {
    Sync s;

    public T1(Sync s) {
        this.s = s;
    }

    public void run() {
        while (true) {
            synchronized (s) {
                if (s.tura == 1) { // sprawdzenie czy jest tura procesu 1
                    System.out.println("1");
                    s.kolejnaTura();
                } else if (s.tury >= 9) {
                    break; // Wyjście z pętli po 10 turach
                }
            }
        }
    }
}

class T2 extends Thread {
    Sync s;
    public T2(Sync s) {
        this.s = s;
    }

    public void run() {
        while (true) {
            synchronized (s) {
                if (s.tura == 2) {
                    System.out.println("2");
                    s.kolejnaTura();
                } else if (s.tury >= 9) {
                    break; // Wyjście z pętli po 10 turach
                }
            }
        }
    }
}

public class Tury {

    public static void main(String args[]) {
        Sync s = new Sync();
        T1 t1 = new T1(s);
        T2 t2 = new T2(s);
        t1.start();
        t2.start();

        try {
            t1.join(); // Poczekaj aż wątki zakończą swoje działanie
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
