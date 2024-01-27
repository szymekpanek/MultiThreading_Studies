package PI_finder;

import java.util.Random;

class Watek implements Runnable {
    private final MonteCarlo monteCarlo;
    int counter;

    public Watek(MonteCarlo monteCarlo) {
        this.monteCarlo = monteCarlo;
        this.counter = 0;
    }

    @Override
    public void run() {
        Random random = new Random();
        int pointsInCircle = 0;

        for (int i = 0; i < monteCarlo.allPoints; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();

            double distanceFromCenter = Math.sqrt(x * x + y * y);

            if (distanceFromCenter <= 1) {
                pointsInCircle++;
            }
        }

        double pi = ((double) pointsInCircle / (double) monteCarlo.allPoints) * 4;
        System.out.println("Thred ID: " + Thread.currentThread().getId() + " pi = " + pi);
        MonteCarlo.all_pi.add(pi);
        counter++;

        averageCounter();
    }

    public void averageCounter() {
        if (counter == monteCarlo.numThreads) {
            double sumPi = 0;
            for (Double pi : MonteCarlo.all_pi) {
                sumPi += pi;
            }
            double definitivePi = sumPi / monteCarlo.numThreads;

            System.out.println("Definitive Pi: " + definitivePi);
        }
    }
}