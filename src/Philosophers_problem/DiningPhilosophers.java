package Philosophers_problem;

import java.util.concurrent.ThreadLocalRandom;

public class DiningPhilosophers {

    static class Fork {
        private final int number;

        public Fork(int number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "Fork " + number;
        }
    }

    static class Philosopher extends Thread {
        private final int number;
        private final Fork leftFork, rightFork;

        public Philosopher(int number, Fork leftFork, Fork rightFork) {
            this.number = number;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        @Override
        public String toString() {
            return "Philosopher " + number;
        }

        private void think() {
            System.out.println(this + " is thinking");
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(30));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void eat() {
            synchronized (leftFork) {
                System.out.println(this + " got " + leftFork + ", waiting for " + rightFork);
                synchronized (rightFork) {
                    System.out.println(this + " is eating");
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(20));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                think();
                eat();
            }
        }
    }

    public static void main(String[] args) {
        final int n = 5;
        final Fork[] forks = new Fork[n];
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Fork(i+1);
        }
        for (int i = 0; i < forks.length-1; i++) {
            new Philosopher(i+1, forks[i], forks[i+1]).start();
        }
//        new Philosopher(n, forks[n-1], forks[0]).start();
        new Philosopher(n, forks[0], forks[n-1]).start();
    }
}
