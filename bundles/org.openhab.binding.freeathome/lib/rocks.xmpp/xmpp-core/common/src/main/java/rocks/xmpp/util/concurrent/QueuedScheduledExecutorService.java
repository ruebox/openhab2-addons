package rocks.xmpp.util.concurrent;

import rocks.xmpp.util.XmppUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class QueuedScheduledExecutorService extends QueuedExecutorService implements ScheduledExecutorService {

    /**
     * Executor that will schedule the tasks.
     */
    private static final ScheduledThreadPoolExecutor SCHEDULER = new ScheduledThreadPoolExecutor(1, XmppUtils.createNamedThreadFactory("Scheduler Thread"));

    /**
     * Sequence number to break scheduling ties.
     */
    private static final AtomicLong SEQUENCER = new AtomicLong();

    /**
     * List of all the tasks currently running.
     */
    private final Set<RunnableScheduledFuture<?>> futures;

    /**
     * False if should cancel/suppress periodic tasks on shutdown.
     */
    private volatile boolean keepPeriodic;

    /**
     * False if should cancel non-periodic tasks on shutdown.
     */
    private volatile boolean keepDelayed;

    /**
     * True if ScheduledFutureTask.cancel should remove from queue
     */
    private volatile boolean removeOnCancel;

    /**
     * @param delegate the executor that will handle the tasks
     */
    public QueuedScheduledExecutorService(ExecutorService delegate) {

        super(delegate);

        this.futures = new HashSet<>();
        this.keepPeriodic = false;
        this.keepDelayed = true;
        this.removeOnCancel = false;

    }

    @Override
    public boolean isTerminated() {
        return super.isTerminated() && futures.isEmpty();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return new ScheduledFutureTask<Void>(Executors.callable(command, null), getInitialDelay(delay, unit), unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return new ScheduledFutureTask<>(callable, getInitialDelay(delay, unit), unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return new ScheduledFutureTask<Void>(Executors.callable(command, null), getInitialDelay(initialDelay, unit), period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return new ScheduledFutureTask<Void>(Executors.callable(command, null), getInitialDelay(initialDelay, unit), -delay, unit);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        onShutdown();
    }

    public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value) {

        keepPeriodic = value;

        if (!value && isShutdown()) {
            onShutdown();
        }

    }

    public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) {

        keepDelayed = value;

        if (!value && isShutdown()) {
            onShutdown();
        }

    }

    public void setRemoveOnCancelPolicy(boolean removeOnCancel) {
        this.removeOnCancel = removeOnCancel;
    }

    private long getInitialDelay(long delay, TimeUnit unit) {
        return System.nanoTime() + unit.toNanos(delay);
    }

    private void onShutdown() {
        for (RunnableScheduledFuture<?> future : new HashSet<>(this.futures)) {
            if ((future.isPeriodic() ? !keepPeriodic : !keepDelayed) || future.isCancelled()) {
                future.cancel(false);
            }
        }
    }

    private class ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {

        private final Callable<V> callable;

        private final long period;

        private final long sequence;

        private long time;

        private ScheduledFutureTask(Callable<V> callable, long initial, TimeUnit unit) {
            this(callable, initial, 0, unit);
        }

        private ScheduledFutureTask(Callable<V> callable, long time, long period, TimeUnit unit) {

            super(callable);

            this.callable = callable;
            this.period = unit.toNanos(period);
            this.sequence = SEQUENCER.getAndIncrement();
            this.time = time;

            QueuedScheduledExecutorService.this.futures.add(this);

            queueNextRun();

        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {

            boolean cancelled = super.cancel(mayInterruptIfRunning);

            if (cancelled && QueuedScheduledExecutorService.this.removeOnCancel) {
                SCHEDULER.remove(this);
            }

            return cancelled;

        }

        @Override
        public boolean isCancelled() {
            return super.isCancelled();
        }

        @Override
        public boolean isDone() {
            return super.isDone();
        }

        @Override
        public boolean isPeriodic() {
            return period != 0;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return super.get();
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(timeout, unit);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {

            if (other == this) {
                return 0;
            }

            if (other instanceof QueuedScheduledExecutorService.ScheduledFutureTask) {

                QueuedScheduledExecutorService.ScheduledFutureTask<?> otherTask;
                otherTask = (QueuedScheduledExecutorService.ScheduledFutureTask<?>) other;

                long deltaTime = time - otherTask.time;

                if (deltaTime < 0) {
                    return -1;
                } else if (deltaTime > 0) {
                    return 1;
                } else if (sequence < otherTask.sequence) {
                    return -1;
                } else {
                    return 1;
                }

            }

            long deltaDelay = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);

            return (deltaDelay < 0) ? -1 : (deltaDelay > 0) ? 1 : 0;

        }

        @Override
        public void run() {
            QueuedScheduledExecutorService.this.execute(this::doRun, canRun());
        }

        @Override
        protected void done() {
            synchronized (QueuedScheduledExecutorService.this.lock) {
                QueuedScheduledExecutorService.this.futures.remove(this);
                QueuedScheduledExecutorService.this.lock.notifyAll();
            }
        }

        private boolean canRun() {
            return !QueuedScheduledExecutorService.this.isShutdown() || (isPeriodic() ? keepPeriodic : keepDelayed);
        }

        private void doRun() {

            boolean isPeriodic = isPeriodic();

            try {

                if (!canRun()) {
                    cancel(false);
                } else if (!isPeriodic) {
                    set(callable.call());
                } else {
                    callable.call();
                }

            } catch (Exception e) {

                setException(e);

            } finally {

                if (isPeriodic) {

                    if (period > 0) {
                        time += period;
                    } else {
                        time = System.nanoTime() - period;
                    }

                    queueNextRun();

                }

            }

        }

        private void queueNextRun() {

            SCHEDULER.getQueue().add(this);

            if (QueuedScheduledExecutorService.this.isShutdown() && !canRun() && SCHEDULER.remove(this)) {
                cancel(false);
            } else {
                SCHEDULER.prestartCoreThread();
            }

        }

    }

}
