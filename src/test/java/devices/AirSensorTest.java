package devices;

import org.citysim.city.City;
import org.citysim.devices.AirSensor;
import org.citysim.events.CityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("AirSensor tests")
public class AirSensorTest {

    @Test @DisplayName("SetStrategy() rejects null")
    void setStrategy_rejects_null(){

        AirSensor sensor = new AirSensor("sensor");

        assertThrows(NullPointerException.class, () -> sensor.setStrategy(null));
    }

    @Test @DisplayName("High pm25 sends alert")
    void performAction_sendsAlert(){

        City city = mock(City.class);
        AirSensor sensor = new AirSensor("AS");

        sensor.setCity(city);
        sensor.setStrategy(history -> 999.0);
        sensor.performAction();

        verify(city).notifyListeners(
                eq(sensor),
                eq(CityEventType.ALERT),
                contains("Poor air quality")
        );
    }

    @Test @DisplayName("Normal pm25 sends status")
    void performAction_sendsStatus(){

        City city = mock(City.class);
        AirSensor sensor = new AirSensor("AS");

        sensor.setCity(city);
        sensor.setStrategy(history -> 1.0);
        sensor.performAction();

        verify(city).notifyListeners(
                eq(sensor),
                eq(CityEventType.STATUS),
                contains("Air OK")
        );
    }

    @Test @DisplayName("History has max size")
    void performAction_maxHistory(){

        City city = mock(City.class);
        AirSensor sensor = new AirSensor("AS");

        sensor.setStrategy(history -> 0);
        sensor.setCity(city);

        for(int i = 0; i < 1000; i++){
            sensor.performAction();
        }

        assertTrue(sensor.getHistory().size() <= 100);
    }
}
