package devices;

import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.city.City;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CityDevice test")
public class CityDeviceTest {
    @Test
    @DisplayName("Constructor rejects invalid values")
    void constructor_test(){
        assertThrows(IllegalArgumentException.class, () -> new TestDevice(0));
    }

    @Test
    @DisplayName("Get methods return values")
    void get_returnValue(){
        TestDevice device = new TestDevice(5);

        assertEquals("test", device.getId());
        assertEquals(5, device.getIntervalSeconds());
        assertEquals(DeviceType.AIR_SENSOR, device.getType());
    }

    @Test
    @DisplayName("Update status notifies city")
    void updateStatus_notifiesCity(){
        TestCity city = new TestCity();
        TestDevice device = new TestDevice(1);

        device.setCity(city);
        device.updateStatus("test");

        assertEquals(CityEventType.STATUS, city.type);
        assertEquals("test", city.message);
    }

    @Test
    @DisplayName("Schedule runs performAction")
    void schedule_performAction() throws InterruptedException {
        CityThreadPool pool = new CityThreadPool(1);
        TestDevice device = new TestDevice(1);

        device.schedule(pool);
        Thread.sleep(1500);

        assertTrue(device.executed.get());
        pool.safeShutdown(1, TimeUnit.SECONDS);
    }

    static class TestDevice extends CityDevice{
        AtomicBoolean executed = new AtomicBoolean(false);

        TestDevice(int interval){
            super("test", interval, DeviceType.AIR_SENSOR);
        }

        @Override
        public void performAction(){
            executed.set(true);
        }
    }

    static class TestCity extends City{
        CityEventType type;
        String message;

        @Override
        public void notifyListeners(CityDevice device, CityEventType type, String message){
            this.type = type;
            this.message = message;
        }
    }
}
