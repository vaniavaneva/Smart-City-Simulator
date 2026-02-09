package city;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.observers.CityEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("City test")
public class CityTest {
    @Test @DisplayName("Adds device and stores to city")
    void addDevice_setCityStoreDevice(){
        City city = new City();
        TestDevice device = new TestDevice();

        city.addDevice(device);
        List<CityDevice> devices = city.getAllDevices();

        assertEquals(1, devices.size());
        assertSame(city, device.getCity());
    }

    @Test @DisplayName("GetAllDevices returns copy")
    void getAllDevices_returnsCopy(){
        City city = new City();
        city.addDevice(new TestDevice());

        List<CityDevice> list = city.getAllDevices();

        assertThrows(UnsupportedOperationException.class, list::clear);
        assertEquals(1, city.getAllDevices().size());
    }

    @Test @DisplayName("All listeners are called")
    void notifyListeners_callsAllListeners(){
        City city = new City();
        TestListener listener = new TestListener();

        city.addListener(listener);

        TestDevice device = new TestDevice();

        city.notifyListeners(device, CityEventType.STATUS, "test");

        assertTrue(listener.called);
        assertEquals("test", listener.message);
    }

    @Test @DisplayName("StartSimulation routes devices")
    void startSimulation_routesDevices(){
        City city = new City();
        CityThreadPool testPool = new CityThreadPool(2);

        city.setThreadPool(testPool);

        TestTrafficLight light = new TestTrafficLight();
        TestDevice device = new TestDevice();

        city.addDevice(light);
        city.addDevice(device);

        city.startSimulation();

        assertTrue(light.performed);
        assertTrue(device.scheduled);
    }

    static class TestDevice extends CityDevice{
        boolean scheduled = false;

        TestDevice() {
            super("testDevice", 1, DeviceType.AIR_SENSOR);
        }

        @Override public void performAction() {}

        @Override public void schedule(CityThreadPool pool){
            scheduled = true;
        }
    }
    static class TestListener implements CityEventListener{
        boolean called = false;
        String message;

        @Override public void onEvent(CityDevice device, CityEventType type, String message){
            called = true;
            this.message = message;
        }
    }
    static class TestTrafficLight extends CityDevice {
        boolean performed = false;

        TestTrafficLight(){
            super("testTrafficLight", 2, DeviceType.TRAFFIC_LIGHT);
        }

        @Override public void performAction(){
            performed = true;
        }

        @Override public void schedule(CityThreadPool pool) {}
    }
}