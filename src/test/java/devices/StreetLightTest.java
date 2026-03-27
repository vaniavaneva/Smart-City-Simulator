package devices;

import org.citysim.city.City;
import org.citysim.devices.StreetLight;
import org.citysim.events.CityEventType;
import org.citysim.util.ConfigLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("StreetLight tests")
public class StreetLightTest {

    int start = ConfigLoader.getInt("street.dark.start");
    int end = ConfigLoader.getInt("street.dark.end");

    @Test @DisplayName("IsDark() returns correct boolean")
    void isDark_test(){

        StreetLight light = new StreetLight("light");

        for(int i = 0; i < 24; i++){
            if(i >= start || i < end) assertTrue(light.isDark(i));
            else assertFalse(light.isDark(i));
        }
    }

    @Test @DisplayName("PerformAction() sends event")
    void performAction_sendEvent(){

        StreetLight light = new StreetLight("light");
        City city = mock(City.class);

        light.setCity(city);
        light.performAction();

        verify(city).notifyListeners(
                eq(light),
                eq(CityEventType.STREET_LIGHT_CHANGE),
                contains("Light")
        );
    }

    @Test @DisplayName("performAction() resets after 23")
    void performAction_resets(){
        StreetLight light = new StreetLight("light");
        City city = mock(City.class);
        light.setCity(city);

        for(int i = 0; i < 24; i++){
            light.performAction();
        }

        verify(city).notifyListeners(
                eq(light),
                eq(CityEventType.STREET_LIGHT_CHANGE),
                contains("Hour: 0")
        );
    }

    @Test @DisplayName("PerformAction() turns on when dark")
    void performAction_turnsOn(){
        StreetLight light = new StreetLight("light");
        City city = new City();
        light.setCity(city);

        AtomicBoolean turnedOn = new AtomicBoolean(false);

        city.addListener((device, type, message) -> {
            if (device == light && message.contains("ON")) {
                turnedOn.set(true);
            }
        });

        for(int i = 0; i < 24; i++){
            light.performAction();
            if (turnedOn.get()) break;
        }

        assertTrue(turnedOn.get());
    }
}
