package concurrent;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.CityDevice;
import org.citysim.factory.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CityThreadPool tests")
public class CityThreadPoolTest {

    @Test @DisplayName("Scheduling executes tasks")
    void task_executes() throws InterruptedException {

        CityThreadPool pool = new CityThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);

        pool.scheduleAtFixedRate(
                latch::countDown,
                0,
                1,
                TimeUnit.MILLISECONDS
        );

        assertTrue(latch.await(1, TimeUnit.SECONDS));

        pool.safeShutdown(1, TimeUnit.SECONDS);
    }

    @Test @DisplayName("Shutdown() shuts down pool")
    void shutdown_terminatesPool() throws InterruptedException {

        CityThreadPool pool = new CityThreadPool(1);

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);

        assertTrue(pool.isTerminated());
    }

    @Test @DisplayName("SafeShutdown() shuts down pool")
    void safeShutdown_completes() throws InterruptedException {

        CityThreadPool pool = new CityThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);

        pool.scheduleAtFixedRate(
                latch::countDown,
                0,
                1,
                TimeUnit.MILLISECONDS
        );

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        pool.safeShutdown(1, TimeUnit.SECONDS);

        assertTrue(pool.isTerminated());
    }

    @Test @DisplayName("Devices execute asyncly")
    void devicesExecuteAsync_ViaThreadPool() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        City city = new City();
        CityThreadPool pool = new CityThreadPool(1);
        city.setThreadPool(pool);

        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR) {
            @Override
            public void performAction() {
                latch.countDown();
            }
        };

        city.addDevice(device);
        city.startSimulation();

        assertTrue(latch.await(1, TimeUnit.SECONDS));

        pool.safeShutdown(1, TimeUnit.SECONDS);
    }
}
