package concurrent;

import org.citysim.concurrent.CityThreadPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CityThreadPool test")
public class CityThreadPoolTest {

    @Test
    @DisplayName("Scheduling works")
    void task_executes() throws InterruptedException {
        CityThreadPool pool = new CityThreadPool(1);
        AtomicBoolean executed = new AtomicBoolean(false);

        pool.scheduleAtFixedRate(() -> executed.set(true), 0, 1, TimeUnit.MILLISECONDS);
        Thread.sleep(20);
        pool.safeShutdown(1, TimeUnit.SECONDS);

        assertTrue(executed.get());
    }

    @Test
    @DisplayName("Shutdown works")
    void shutdown_terminatesPool() throws InterruptedException {
        CityThreadPool pool = new CityThreadPool(1);

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);

        assertTrue(pool.isTerminated());
    }

    @Test
    @DisplayName("SafeShutdown doesn't throw ex")
    void safeShutdown_completes() {
        CityThreadPool pool = new CityThreadPool(1);

        assertDoesNotThrow(() -> pool.safeShutdown(1, TimeUnit.SECONDS));
    }
}
