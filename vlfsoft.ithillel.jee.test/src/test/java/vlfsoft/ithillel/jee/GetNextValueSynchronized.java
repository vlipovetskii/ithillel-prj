package vlfsoft.ithillel.jee;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Given the following class;
 * public class IncrementSynchronize {
 * private int value = 0;
 * //getNextValue()
 * }
 * Write three different method options for getNextValue() that will return 'value++', each method needs to be synchronized in a different way.
 *
 * Assumption: public int getNextValue() { return value++;  }
 * If the Task require public int getNextValue() { return ++value;  }, then replace return value++ with return ++value and leverage incrementAndGet() instead of getAndIncrement()
 */
class GetNextValueSynchronized {

    private final static int maxCount = 10;

    static class GetNextValueRunnable implements Runnable {

        CountDownLatch finishTestLatch;
        IncrementSynchronizeA incrementSynchronize;

        GetNextValueRunnable(CountDownLatch finishTestLatch, IncrementSynchronizeA incrementSynchronize) {
            this.finishTestLatch = finishTestLatch;
            this.incrementSynchronize = incrementSynchronize;
        }

        int value = 0;

        @Override
        public void run() {
            while (value < maxCount) {
                try {
                    // Leverage sleep, to run threads more parallel.
                    TimeUnit.MILLISECONDS.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%s: getNextValue = %d\n", Thread.currentThread().getName(), incrementSynchronize.getNextValue());
                value++;
            }
            finishTestLatch.countDown();
        }
    }

    interface IncrementSynchronizeA {
        int getValue();

        int getNextValue();
    }

    private void getNextValueSynchronizedTest(IncrementSynchronizeA incrementSynchronize) {
        CountDownLatch finishTestLatch = new CountDownLatch(2);

        GetNextValueRunnable t1 = new GetNextValueRunnable(finishTestLatch, incrementSynchronize);
        GetNextValueRunnable t2 = new GetNextValueRunnable(finishTestLatch, incrementSynchronize);

        // new Thread(t1).start();
        // new Thread(t2).start();

        // Or
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(t1);
        executorService.submit(t2);

        try {
            finishTestLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("t1.value = %d\n", t1.value);
        System.out.printf("t2.value = %d\n", t1.value);
        System.out.printf("incrementSynchronize.getValue = %d\n", incrementSynchronize.getValue());

        assertEquals(maxCount, t1.value);
        assertEquals(maxCount, t2.value);

        assertEquals(incrementSynchronize.getValue(), t1.value + t2.value);
    }

    static class IncrementSynchronize1 implements IncrementSynchronizeA {
        private int value = 0;

        @Override
        synchronized public int getValue() {
            return value;
        }

        @Override
        synchronized public int getNextValue() {
            return value++;
        }
    }

    @Test
    void getNextValueSynchronizedTest1() {
        getNextValueSynchronizedTest(new IncrementSynchronize1());
    }

    static class IncrementSynchronize2 implements IncrementSynchronizeA {
        private int value = 0;

        private ReadWriteLock readWriteLock;

        IncrementSynchronize2(ReadWriteLock readWriteLock) {
            this.readWriteLock = readWriteLock;
        }

        @Override
        public int getValue() {
            Lock lock = readWriteLock.readLock();
            lock.lock();
            try {
                return value;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getNextValue() {
            Lock lock = readWriteLock.writeLock();
            lock.lock();
            try {
                return value++;
            } finally {
                lock.unlock();
            }
        }
    }

    @Test
    void getNextValueSynchronizedTest2() {
        getNextValueSynchronizedTest(new IncrementSynchronize2(new ReentrantReadWriteLock()));
    }

    static class IncrementSynchronize3 implements IncrementSynchronizeA {
        private int value = 0;

        private StampedLock readWriteLock;

        IncrementSynchronize3(StampedLock readWriteLock) {
            this.readWriteLock = readWriteLock;
        }

        @Override
        public int getValue() {
            long stamp = readWriteLock.tryOptimisticRead();
            int retValue = value;
            if (!readWriteLock.validate(stamp)) {
                try {
                    retValue =value;
                } finally {
                    readWriteLock.unlock(stamp);
                }
            }
            return retValue;
        }

        @Override
        public int getNextValue() {
            long stamp = readWriteLock.writeLock();
            try {
                return value++;
            } finally {
                readWriteLock.unlock(stamp);
            }
        }
    }

    @Test
    void getNextValueSynchronizedTest3() {
        getNextValueSynchronizedTest(new IncrementSynchronize3(new StampedLock()));
    }

    static class IncrementSynchronize4 implements IncrementSynchronizeA {
        private AtomicInteger value = new AtomicInteger(0);

        @Override
        public int getValue() {
            return value.get();
        }

        @Override
        public int getNextValue() {
            return value.getAndIncrement();
        }
    }

    @Test
    void getNextValueSynchronizedTest4() {
        getNextValueSynchronizedTest(new IncrementSynchronize4());
    }

}
