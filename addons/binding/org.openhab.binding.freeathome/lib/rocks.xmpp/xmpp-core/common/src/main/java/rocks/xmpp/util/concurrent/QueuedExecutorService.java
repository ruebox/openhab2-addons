package rocks.xmpp.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueuedExecutorService extends AbstractExecutorService {

    /**
     * Executor that will handle the tasks
     */
    private final ExecutorService delegate;

    /**
     * Lock indicating whether or not a task is pending completion.
     */
    final AtomicBoolean lock;

    /**
     * Lock indicating whether or not the delegate has been shutdown.
     */
    private final AtomicBoolean shutdown;

    /**
     * Queue of all the pending tasks.
     */
    private final BlockingQueue<Runnable> tasks;

    /**
     * @param delegate the executor that will handle the tasks
     */
    public QueuedExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
        this.lock = new AtomicBoolean(false);
        this.shutdown = new AtomicBoolean(false);
        this.tasks = new LinkedBlockingQueue<>();
    }

    @Override
    public void execute(Runnable command) {
        execute(command, false);
    }

    @Override
    public void shutdown() {
        synchronized (shutdown) {
            shutdown.set(true);
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        synchronized (shutdown) {
            shutdown();
            List<Runnable> result = new ArrayList<>();
            tasks.drainTo(result);
            return result;
        }
    }

    @Override
    public boolean isShutdown() {
        synchronized (shutdown) {
            return shutdown.get();
        }
    }

    @Override
    public boolean isTerminated() {
        return isShutdown() && tasks.isEmpty() && !lock.get();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {

        long nanos = unit.toNanos(timeout);

        synchronized (lock) {
            while (true) {
                if (isTerminated()) {
                    return true;
                } else if (nanos <= 0) {
                    return false;
                } else {
                    long now = System.nanoTime();
                    TimeUnit.NANOSECONDS.timedWait(lock, nanos);
                    nanos -= System.nanoTime() - now;
                }
            }
        }

    }

    void execute(Runnable command, boolean ignoreShutdown) {

        if (ignoreShutdown || !isShutdown()) {

            // Adds a task to the queue and call ThreadQueue#poll in order to process it (if the queue
            // allows it, otherwise, wait for a Thread to be available).
            tasks.add(command);
            poll();

        }

    }

    /**
     * Find outs if the queue contains any tasks and handle the next one if necessary.
     */
    private void poll() {

        // If the queue is not empty and the lock is available, then we can continue and attempt to
        // retrieve a task.
        if (!tasks.isEmpty() && !lock.getAndSet(true)) {

            Runnable task = tasks.poll();

            if (task != null) {

                // Queue the task for further processing
                delegate.execute(() -> doExecute(task));

            } else {

                // Probably useless (if the queue wasn't empty previously, then if task should never be
                // null ; yet, this should prevent any unforseen deadlock.
                lock.set(false);
                poll();

            }

        } else if (tasks.isEmpty()) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

    }

    /**
     * Performs a task.
     *
     * @param task the request being task.
     */
    private void doExecute(Runnable task) {

        try {

            task.run();

        } finally {

            // The task is complete, release the lock and queue the next task.
            lock.set(false);
            poll();

        }

    }

}
