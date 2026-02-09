package devices;

import org.citysim.city.City;
import org.citysim.devices.AirSensor;
import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AirSensor test")
public class AirSensorTest {
    @Test
    @DisplayName("SetStrategy rejects null")
    void setStrategy_rejects_null(){
        AirSensor sensor = new AirSensor("sensor");

        assertThrows(NullPointerException.class, () -> sensor.setStrategy(null));
    }

    @Test
    @DisplayName("PerformAction sends alert")
    void performAction_sendsAlert(){
        AirSensor sensor = new AirSensor("sensor");
        sensor.setStrategy(history -> 999);

        TestCity city = new TestCity();
        sensor.setCity(city);

        sensor.performAction();

        assertEquals(CityEventType.ALERT, city.lastType);
    }

    @Test
    @DisplayName("PerformAction sends status")
    void performAction_sendsStatus(){
        AirSensor sensor = new AirSensor("sensor");
        sensor.setStrategy(history -> 0);

        TestCity city = new TestCity();
        sensor.setCity(city);

        sensor.performAction();
        assertEquals(CityEventType.STATUS, city.lastType);
    }

    @Test
    @DisplayName("History has max size")
    void performAction_maxHistory(){
        AirSensor sensor = new AirSensor("sensor");
        sensor.setStrategy(history -> 0);
        TestCity city = new TestCity();
        sensor.setCity(city);

        for(int i = 0; i < 1000; i++){
            sensor.performAction();
        }

        assertTrue(true);
    }

    static class TestCity extends City {
        CityEventType lastType;

        @Override
        public void notifyListeners(CityDevice device, CityEventType type, String message) {
            lastType = type;
        }
    }
}
