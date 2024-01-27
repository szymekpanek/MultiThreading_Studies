package PI_finder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class MonteCarlo {
    Watek watek;
    int allPoints;
    int pointsInCircle;
    static List<Double> all_pi = new ArrayList<>();
    int numThreads;

    public MonteCarlo(int allPoints, int numThreads) {
        this.allPoints = allPoints;
        this.pointsInCircle = 0;
        all_pi = new ArrayList<>();
        this.numThreads = numThreads;
        this.watek = new Watek(this);
    }

    public void runMonteCarlo() {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(watek);
        }
        executorService.shutdown();
    }

    public static void main(String[] args) {
        int allPoints = 1000000;
        int numThreads = 10;

        MonteCarlo monteCarlo = new MonteCarlo(allPoints, numThreads);
        monteCarlo.runMonteCarlo();
    }
}