package city;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.CityDevice;
import org.citysim.devices.TrafficLight;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.observers.CityEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("City tests")
public class CityTest {

    @Test @DisplayName("City adds device and stores to list")
    void addDevice_setCityStoreDevice(){

        City city = new City();
        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR) {
            @Override public void performAction() {}
        };

        city.addDevice(device);
        List<CityDevice> devices = city.getAllDevices();

        assertEquals(1, devices.size());
        assertSame(city, device.getCity());
    }

    @Test @DisplayName("GetAllDevices() returns copy of list")
    void getAllDevices_returnsCopy(){

        City city = new City();
        city.addDevice(new CityDevice("test", 1, DeviceType.AIR_SENSOR) {
            @Override public void performAction() {}
        });

        List<CityDevice> list = city.getAllDevices();

        assertThrows(UnsupportedOperationException.class, list::clear);
        assertEquals(1, city.getAllDevices().size());
    }

    @Test @DisplayName("NotifyListeners() calls listeners in list")
    void notifyListeners_callsAllListeners(){

        City city = new City();
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicReference<String> capturedMessage = new AtomicReference<>();

        CityEventListener listener = (device, type, message) -> {
            called.set(true);
            capturedMessage.set(message);
        };

        city.addListener(listener);

        CityDevice device = new CityDevice("device", 1, DeviceType.AIR_SENSOR) {
            @Override public void performAction() {}
        };

        city.notifyListeners(device, CityEventType.STATUS, "test");

        assertTrue(called.get());
        assertEquals("test", capturedMessage.get());
    }

    @Test @DisplayName("StartSimulation() routes devices")
    void startSimulation_routesDevices(){

        City city = new City();
        CityThreadPool pool = new CityThreadPool(2);

        city.setThreadPool(pool);

        AtomicBoolean performed = new AtomicBoolean(false);
        AtomicBoolean scheduled = new AtomicBoolean(false);

        TrafficLight light = new TrafficLight("test"){
            @Override public void performAction() {
                performed.set(true);
            }
        };
        CityDevice device = new CityDevice("device", 1, DeviceType.AIR_SENSOR) {
            @Override public void performAction() {
                scheduled.set(true);
            }
        };

        city.addDevice(light);
        city.addDevice(device);
        city.startSimulation();

        assertTrue(performed.get());
        assertTrue(scheduled.get());

        pool.safeShutdown(1, TimeUnit.SECONDS);
    }
}