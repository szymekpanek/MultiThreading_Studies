package Philosophers_problem.Studies.A;

class Widelec {
    private boolean podniesiony = false;

    public synchronized void podnies() throws InterruptedException {
        while (podniesiony) {
            wait();
        }
        podniesiony = true;
    }

    public synchronized void odloz() {
        podniesiony = false;
        notifyAll();
    }

    public synchronized boolean czyPodniesiony() {
        return podniesiony;
    }
}

class Lokaj {
    private Widelec[] widelce;

    public Lokaj(int liczbaFilozofow) {
        widelce = new Widelec[liczbaFilozofow];
        for (int i = 0; i < liczbaFilozofow; i++) {
            widelce[i] = new Widelec();
        }
    }

    public boolean podniesWidelec(int numerFilozofa) throws InterruptedException {
        int lewyWidelec = numerFilozofa;
        int prawyWidelec = (numerFilozofa + 1) % widelce.length;

        widelce[lewyWidelec].podnies();
        if (!widelce[prawyWidelec].czyPodniesiony()) {
            widelce[lewyWidelec].odloz(); // zwolnij lewy widelec
            return false;
        }

        return true;
    }

    public void odlozWidelec(int numerFilozofa) {
        int lewyWidelec = numerFilozofa;
        int prawyWidelec = (numerFilozofa + 1) % widelce.length;

        widelce[lewyWidelec].odloz();
        widelce[prawyWidelec].odloz();
    }
}

class Filozof extends Thread {
    private int _licznik = 0;
    private int numerFilozofa;
    private Lokaj lokaj;
    private long startTime;

    public Filozof(int numerFilozofa, Lokaj lokaj) {
        this.numerFilozofa = numerFilozofa;
        this.lokaj = lokaj;
        this.startTime = System.currentTimeMillis();
    }

    public void run() {
        while (_licznik < 1000000) {
            try {
                if (lokaj.podniesWidelec(numerFilozofa)) {
                    // jedzenie
                    ++_licznik;
                    // koniec jedzenia

                    lokaj.odlozWidelec(numerFilozofa);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Filozof " + numerFilozofa + " zjadł 1000000 razy. Czas: " + (endTime - startTime) + " ms");
        System.exit(0); // Zatrzymaj program po zakończeniu 10000 posiłków
    }
}


public class Fil5mon {
    public static void main(String[] args) {
        int liczbaFilozofow = 5;
        Lokaj lokaj = new Lokaj(liczbaFilozofow);

        Filozof[] filozofowie = new Filozof[liczbaFilozofow];
        for (int i = 0; i < liczbaFilozofow; i++) {
            filozofowie[i] = new Filozof(i, lokaj);
            filozofowie[i].start();
        }
    }
}
