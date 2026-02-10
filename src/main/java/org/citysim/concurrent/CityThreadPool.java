package org.citysim.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CityThreadPool {
    private final ScheduledExecutorService scheduler;

    /**
     * Creates a thread pool with number of threads
     * @param threads - number of threads in pool (must be > 0)
     */
    public CityThreadPool(int threads) {
        this.scheduler = Executors.newScheduledThreadPool(threads);
    }

    /**
     * @return scheduled executor service
     */
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    /**
     * Schedules task to run repeatedly at fixed rate
     * @param task - task to execute
     * @param initialDelay - delay before first execution
     * @param period - time between executions
     * @param unit - time unit
     */
    public void scheduleAtFixedRate(Runnable task, long initialDelay,
                                    long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * Stops scheduler
     * No new tasks accepted
     */
    public void shutdown() {
        scheduler.shutdown();
    }

    /**
     * Stops scheduler immediately
     */
    public void shutdownNow() {
        scheduler.shutdownNow();
    }

    /**
     * Blocks until all tasks finish execution or timeout expires
     * @param timeout - maximum wait time
     * @param unit - time unit
     * @return true if terminated successfully
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return scheduler.awaitTermination(timeout, unit);
    }

    /**
     * @return true if all tasks finished
     */
    public boolean isTerminated() {
        return scheduler.isTerminated();
    }

    /**
     * Tries safe shutdown first then forces termination
     * @param timeout - maximum time to wait
     * @param unit - time unit
     */
    public void safeShutdown(long timeout, TimeUnit unit) {
        try {
            scheduler.shutdown();

            if (!scheduler.awaitTermination(timeout, unit)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}