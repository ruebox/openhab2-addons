package rocks.xmpp.util;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.util.concurrent.QueuedScheduledExecutorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QueuedScheduledExecutorServiceTest {

    private static final String ID1 = "ID1_";
    private static final String ID2 = "ID2_";
    private static final String BEG = "_BEG";
    private static final String END = "_END";

    @Test
    public void scheduleSingleExecutor() throws Exception {

        List<String> execution = new ArrayList<>();
        AtomicInteger failures = new AtomicInteger(0);
        Tasker tasker = new Tasker(execution, failures);

        Runnable task_1_1 = () -> tasker.run(ID1 + "D_1", 400);
        Runnable task_1_2 = () -> tasker.run(ID1 + "D_2", 300);

        ThreadPoolExecutor shared;
        shared = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        shared.prestartAllCoreThreads();

        QueuedScheduledExecutorService executor1;
        executor1 = new QueuedScheduledExecutorService(shared);
        executor1.schedule(task_1_2, 200, TimeUnit.MILLISECONDS);
        executor1.schedule(task_1_1, 0, TimeUnit.MILLISECONDS);
        executor1.shutdown();
        executor1.awaitTermination(2000, TimeUnit.MILLISECONDS);

        List<String> expected = Arrays.asList(  //  Task ID      | Configured   | Actual Delay
                ID1 + "D_1" + BEG,  //              ID1_D_1_BEG  | 000          | 000
                ID1 + "D_1" + END,  //              ID1_D_1_END  | 400          | 400 = ID1_D_1_BEG + 400
                ID1 + "D_2" + BEG,  //              ID1_D_2_BEG  | 300          | 400 = ID1_D_1_END
                ID1 + "D_2" + END); //              ID1_D_2_END  | 300          | 700 = ID1_D_2_BEG = 300

        Assert.assertEquals(failures.get(), 0);
        Assert.assertEquals(execution, expected);
        Assert.assertNotSame(execution, expected);

    }

    @Test
    public void scheduleMultipleExecutor() throws Exception {

        List<String> execution = new ArrayList<>();
        AtomicInteger failures = new AtomicInteger(0);
        Tasker tasker = new Tasker(execution, failures);

        Runnable task_1_1 = () -> tasker.run(ID1 + "D_1", 400);
        Runnable task_1_2 = () -> tasker.run(ID1 + "D_2", 300);
        Runnable task_2_1 = () -> tasker.run(ID2 + "D_1", 0);

        ThreadPoolExecutor shared;
        shared = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        shared.prestartAllCoreThreads();

        QueuedScheduledExecutorService executor1, executor2;
        executor1 = new QueuedScheduledExecutorService(shared);
        executor2 = new QueuedScheduledExecutorService(shared);

        executor1.schedule(task_1_2, 200, TimeUnit.MILLISECONDS);
        executor1.schedule(task_1_1, 0, TimeUnit.MILLISECONDS);
        executor2.schedule(task_2_1, 100, TimeUnit.MILLISECONDS);

        executor1.shutdown();
        executor2.shutdown();

        executor1.awaitTermination(2000, TimeUnit.MILLISECONDS);
        executor2.awaitTermination(2000, TimeUnit.MILLISECONDS);

        List<String> expected = Arrays.asList(  //  Task ID      | Configured   | Actual Delay
                ID1 + "D_1" + BEG,  //              ID1_D_1_BEG  | 000          | 000
                ID2 + "D_1" + BEG,  //              ID2_D_1_BEG  | 100          | 100
                ID2 + "D_1" + END,  //              ID2_D_1_END  | 100          | 100 = ID2_D_1_BEG + 100
                ID1 + "D_1" + END,  //              ID1_D_1_END  | 400          | 400 = ID1_D_1_BEG + 400
                ID1 + "D_2" + BEG,  //              ID1_D_2_BEG  | 300          | 400 = ID1_D_1_END
                ID1 + "D_2" + END); //              ID1_D_2_END  | 300          | 700 = ID1_D_2_BEG = 300

        Assert.assertEquals(failures.get(), 0);
        Assert.assertEquals(execution, expected);
        Assert.assertNotSame(execution, expected);

    }

    @Test
    public void scheduleMultipleExecutorAtFixedRate() throws Exception {

        List<String> execution = new ArrayList<>();
        AtomicInteger failures = new AtomicInteger(0);
        Tasker tasker = new Tasker(execution, failures);

        Runnable task_1_1 = () -> tasker.run(ID1 + "D_1", 400);
        Runnable task_1_2 = () -> tasker.run(ID1 + "D_2", 300);
        Runnable task_1_P = () -> tasker.run(ID1 + "PER", 0);
        Runnable task_2_P = () -> tasker.run(ID2 + "PER", 300);

        ThreadPoolExecutor shared;
        shared = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        shared.prestartAllCoreThreads();

        QueuedScheduledExecutorService executor1, executor2;
        executor1 = new QueuedScheduledExecutorService(shared);
        executor2 = new QueuedScheduledExecutorService(shared);

        executor1.schedule(task_1_2, 300, TimeUnit.MILLISECONDS);
        executor1.schedule(task_1_1, 0, TimeUnit.MILLISECONDS);
        executor1.scheduleAtFixedRate(task_1_P, 100, 400, TimeUnit.MILLISECONDS);
        executor2.scheduleAtFixedRate(task_2_P, 200, 400, TimeUnit.MILLISECONDS);

        Thread.sleep(900);

        executor1.shutdown();
        executor2.shutdown();
        executor1.awaitTermination(2000, TimeUnit.MILLISECONDS);
        executor2.awaitTermination(2000, TimeUnit.MILLISECONDS);

        List<String> expected = Arrays.asList(  //  Task ID      | Configured   | Actual Delay
                ID1 + "D_1" + BEG,  //              ID1_D_1_BEG  | 000          | 000
                ID2 + "PER" + BEG,  //              ID2_P_1_BEG  | 200          | 200
                ID1 + "D_1" + END,  //              ID1_D_1_END  | 400          | 400 = ID1_D_1_BEG  + 400
                ID1 + "PER" + BEG,  //              ID1_P_1_BEG  | 100          | 400 = ID1_D_1_END
                ID1 + "PER" + END,  //              ID1_P_1_END  | 100          | 400 = ID1_P_1_BEG  + 0
                ID1 + "D_2" + BEG,  //              ID1_D_2_BEG  | 300          | 400 = ID1_P_1_END
                ID2 + "PER" + END,  //              ID2_P_1_END  | 500          | 500 = ID2_P_1_BEG + 300
                ID2 + "PER" + BEG,  //              ID2_P_2_BEG  | 600          | 600 = ID2_P_1_BEG + 400
                ID1 + "D_2" + END,  //              ID1_D_2_END  | 600          | 700 = ID1_D_2_BEG + 300
                ID1 + "PER" + BEG,  //              ID1_P_2_BEG  | 600          | 700 = ID1_D_2_END
                ID1 + "PER" + END,  //              ID1_P_2_END  | 600          | 700 = ID1_P_2_BEG + 0
                ID2 + "PER" + END); //              ID2_P_2_END  | 900          | 900 = ID2_P_2_BEG + 300

        Assert.assertEquals(failures.get(), 0);
        Assert.assertEquals(execution, expected);
        Assert.assertNotSame(execution, expected);

    }

    @Test
    public void scheduleMultipleExecutorWithFixedDelay() throws Exception {

        List<String> execution = new ArrayList<>();
        AtomicInteger failures = new AtomicInteger(0);
        Tasker tasker = new Tasker(execution, failures);

        Runnable task_1_1 = () -> tasker.run(ID1 + "D_1", 400);
        Runnable task_1_2 = () -> tasker.run(ID1 + "D_2", 300);
        Runnable task_1_P = () -> tasker.run(ID1 + "PER", 0);
        Runnable task_2_P = () -> tasker.run(ID2 + "PER", 300);

        ThreadPoolExecutor shared;
        shared = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        shared.prestartAllCoreThreads();

        QueuedScheduledExecutorService executor1, executor2;
        executor1 = new QueuedScheduledExecutorService(shared);
        executor2 = new QueuedScheduledExecutorService(shared);

        executor1.schedule(task_1_2, 300, TimeUnit.MILLISECONDS);
        executor1.schedule(task_1_1, 0, TimeUnit.MILLISECONDS);
        executor1.scheduleWithFixedDelay(task_1_P, 100, 400, TimeUnit.MILLISECONDS);
        executor2.scheduleWithFixedDelay(task_2_P, 200, 400, TimeUnit.MILLISECONDS);

        Thread.sleep(900);

        executor1.shutdown();
        executor2.shutdown();
        executor1.awaitTermination(2000, TimeUnit.MILLISECONDS);
        executor2.awaitTermination(2000, TimeUnit.MILLISECONDS);

        List<String> expected = Arrays.asList(  //  Task ID      | Configured   | Actual Delay
                ID1 + "D_1" + BEG,  //              ID1_D_1_BEG  | 000          | 000
                ID2 + "PER" + BEG,  //              ID2_P_1_BEG  | 200          | 200
                ID1 + "D_1" + END,  //              ID1_D_1_END  | 400          | 400 = ID1_D_1_BEG  + 400
                ID1 + "PER" + BEG,  //              ID1_P_1_BEG  | 100          | 400 = ID1_D_1_END
                ID1 + "PER" + END,  //              ID1_P_1_END  | 100          | 400 = ID1_P_1_BEG  + 0
                ID1 + "D_2" + BEG,  //              ID1_D_2_BEG  | 300          | 400 = ID1_P_1_END
                ID2 + "PER" + END,  //              ID2_P_1_END  | 500          | 500 = ID2_P_1_BEG + 300
                ID1 + "D_2" + END,  //              ID1_D_2_END  | 600          | 700 = ID1_D_2_BEG + 300
                ID1 + "PER" + BEG,  //              ID1_P_2_BEG  | 600          | 700 = ID1_D_2_END
                ID1 + "PER" + END); //              ID1_P_2_END  | 600          | 700 = ID1_P_2_BEG + 0

        Assert.assertEquals(failures.get(), 0);
        Assert.assertEquals(execution, expected);
        Assert.assertNotSame(execution, expected);

    }

    private static class Tasker {

        private final List<String> output;

        private final AtomicInteger failures;

        private Tasker(List<String> output, AtomicInteger failures) {
            this.output = output;
            this.failures = failures;
        }

        private void run(String prefix, int sleep) {

            try {

                output.add(prefix + BEG);
                Thread.sleep(sleep);
                output.add(prefix + END);

            } catch (InterruptedException e) {
                failures.incrementAndGet();
            }

        }

    }

}
