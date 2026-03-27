package devices;

import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.city.City;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("CityDevice tests")
public class CityDeviceTest {

    @Test @DisplayName("Constructor rejects invalid values")
    void constructor_test(){

        assertThrows(IllegalArgumentException.class,
                () -> new CityDevice("test", 0, DeviceType.AIR_SENSOR) {
                    @Override public void performAction(){}
                });

        assertThrows(NullPointerException.class,
                () -> new CityDevice("test", 1, null) {
                    @Override public void performAction(){}
                });
    }

    @Test @DisplayName("Get methods return values")
    void get_returnValue(){

        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR){
            @Override public void performAction(){}
        };

        assertAll(
                () -> assertEquals("test", device.getId()),
                () -> assertEquals(1, device.getIntervalSeconds()),
                () -> assertEquals(DeviceType.AIR_SENSOR, device.getType())
        );
    }

    @Test @DisplayName("UpdateStatus() notifies listeners")
    void updateStatus_notifiesCity(){

        City city = mock(City.class);
        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR){
            @Override public void performAction(){}
        };

        device.setCity(city);
        device.updateStatus("test");

        verify(city).notifyListeners(
                eq(device),
                eq(CityEventType.STATUS),
                contains("test")
        );
    }

    @Test @DisplayName("Schedule() runs performAction()")
    void schedule_performAction() throws InterruptedException {

        CityThreadPool pool = new CityThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);
        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR){
            @Override public void performAction(){
                latch.countDown();
            }
        };

        device.schedule(pool);
        boolean executed = latch.await(1, TimeUnit.SECONDS);
        assertTrue(executed);

        pool.safeShutdown(1, TimeUnit.SECONDS);
    }
}
