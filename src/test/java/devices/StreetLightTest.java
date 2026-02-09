package devices;

import org.citysim.city.City;
import org.citysim.devices.CityDevice;
import org.citysim.devices.StreetLight;
import org.citysim.events.CityEventType;
import org.citysim.util.ConfigLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StreetLight test")
public class StreetLightTest {
    int start = ConfigLoader.getInt("street.dark.start");
    int end = ConfigLoader.getInt("street.dark.end");

    @Test
    @DisplayName("isDark works correctly")
    void isDark_test(){
        StreetLight light = new StreetLight("light");

        for(int i = 0; i < 24; i++){
            if(i >= start || i < end) assertTrue(light.isDark(i));
            else assertFalse(light.isDark(i));
        }
    }

    @Test
    @DisplayName("PerformAction sends event")
    void performAction_sendEvent(){
        StreetLight light = new StreetLight("light");
        TestCity city = new TestCity();

        light.setCity(city);
        light.performAction();

        assertEquals(CityEventType.STREET_LIGHT_CHANGE, city.type);
        assertNotNull(city.message);
    }

    @Test
    @DisplayName("performAction resets after 23")
    void performAction_resets(){
        StreetLight light = new StreetLight("light");
        TestCity city = new TestCity();
        light.setCity(city);

        for(int i = 0; i < 25; i++) light.performAction();

        assertTrue(city.message.contains("Hour: 1"));
    }

    @Test
    @DisplayName("PerformAction turns on when dark")
    void performAction_turnsOn(){
        StreetLight light = new StreetLight("light");
        TestCity city = new TestCity();
        light.setCity(city);

        for(int i = 0; i < 24; i++){
            light.performAction();
            if(city.message.contains("ON")) return;
        }

        fail("Light doesn't turn on for whole duration");
    }

    static class TestCity extends City {
        CityEventType type;
        String message;

        @Override
        public void notifyListeners(CityDevice device, CityEventType type, String message){
            this.type = type;
            this.message = message;
        }
    }
}
