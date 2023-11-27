package LockMechanism;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class FineGrainedLockedList {
    static class Node {
        Object value;
        Node next;
        Lock lock;

        Node(Object value) {
            this.value = value;
            this.next = null;
            this.lock = new ReentrantLock();
        }
    }

    private Node head;

    FineGrainedLockedList() {
        this.head = new Node(null); // Dummy head node
    }

    // Drobnokończeniowe blokowanie
    public boolean contains(Object o) {
        Node pred = head;
        pred.lock.lock();
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr != null) {
                    if (o.equals(curr.value)) {
                        printThreadInfo("contains", Thread.currentThread().getId(), o);
                        return true;
                    }
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    if (curr != null) {
                        curr.lock.lock();
                    }
                }
                return false;
            } finally {
                if (curr != null) {
                    curr.lock.unlock();
                }
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean remove(Object o) {
        Node pred = head;
        pred.lock.lock();
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr != null) {
                    if (o.equals(curr.value)) {
                        pred.next = curr.next;
                        printThreadInfo("remove", Thread.currentThread().getId(), o);
                        return true;
                    }
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    if (curr != null) {
                        curr.lock.lock();
                    }
                }
                return false;
            } finally {
                if (curr != null) {
                    curr.lock.unlock();
                }
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean add(Object o) {
        Node newNode = new Node(o);
        Node pred = head;
        pred.lock.lock();
        try {
            Node curr = pred.next;
            if (curr != null) {
                curr.lock.lock();
            }
            try {
                while (curr != null) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    if (curr != null) {
                        curr.lock.lock();
                    }
                }
                pred.next = newNode;
                printThreadInfo("add", Thread.currentThread().getId(), o);
                return true;
            } finally {
                if (curr != null) {
                    curr.lock.unlock();
                }
            }
        } finally {
            pred.lock.unlock();
        }
    }

    private void printThreadInfo(String operation, long threadId, Object value) {
        System.out.println("Thread " + threadId + " " + operation + ": " + value);
    }
}

public class FineGrainedLockedListTest {
    public static void main(String[] args) {
        int numThreads = 100; // You can adjust the number of threads
        int costPerOperation = 10000; // You can adjust the cost per operation

        FineGrainedLockedList fineGrainedLockedList = new FineGrainedLockedList();

        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < costPerOperation; j++) {
                    fineGrainedLockedList.add(j);
                    fineGrainedLockedList.contains(j);
                    fineGrainedLockedList.remove(j);
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time for Fine-Grained Locks: " + (endTime - startTime) + " ms");
    }
}
