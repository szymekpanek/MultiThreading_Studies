package LockMechanism;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



class CoarseGrainedLockedList {
    static class Node {
        Object value;
        Node next;

        Node(Object value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private final Lock lock;

    CoarseGrainedLockedList() {
        this.head = new Node(null); // Dummy head node
        this.lock = new ReentrantLock();
    }

    // Jednokończeniowe blokowanie
    public boolean contains(Object o, long threadId) {
        lock.lock();
        try {
            Node curr = head.next;
            while (curr != null) {
                if (o.equals(curr.value)) {
                    printThreadInfo("contains", threadId, o);
                    return true;
                }
                curr = curr.next;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Object o, long threadId) {
        lock.lock();
        try {
            Node pred = head;
            Node curr = head.next;
            while (curr != null) {
                if (o.equals(curr.value)) {
                    pred.next = curr.next;
                    printThreadInfo("remove", threadId, o);
                    return true;
                }
                pred = curr;
                curr = curr.next;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean add(Object o, long threadId) {
        Node newNode = new Node(o);
        lock.lock();
        try {
            Node curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newNode;
            printThreadInfo("add", threadId, o);
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void printThreadInfo(String operation, long threadId, Object value) {
        System.out.println("Thread " + threadId + " " + operation + ": " + value);
    }
}

public class CoarseGrainedLockedListTest {
    public static void main(String[] args) {
        int numThreads = 100; // You can adjust the number of threads
        int costPerOperation = 10000; // You can adjust the cost per operation

        CoarseGrainedLockedList coarseGrainedLockedList = new CoarseGrainedLockedList();

        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadNumber = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < costPerOperation; j++) {
                    coarseGrainedLockedList.add(j, threadNumber);
                    coarseGrainedLockedList.contains(j, threadNumber);
                    coarseGrainedLockedList.remove(j, threadNumber);
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
        System.out.println("Execution time for Coarse-Grained Locks: " + (endTime - startTime) + " ms");
    }
}

