package factory;

import org.citysim.devices.AirSensor;
import org.citysim.devices.BikeStation;
import org.citysim.devices.StreetLight;
import org.citysim.devices.TrafficLight;
import org.citysim.factory.DeviceFactory;
import org.citysim.factory.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("DeviceFactory test")
public class DeviceFactoryTest {

    private final DeviceFactory factory = new DeviceFactory();

    @Test @DisplayName("Factory supports all device types")
    void supportsAllTypes() {

        for (DeviceType type : DeviceType.values()) {
            assertDoesNotThrow(() -> factory.create(type, "test"));
        }

        assertInstanceOf(TrafficLight.class, factory.create(DeviceType.TRAFFIC_LIGHT, "TL"));
        assertInstanceOf(BikeStation.class, factory.create(DeviceType.BIKE_STATION, "BS"));
        assertInstanceOf(AirSensor.class, factory.create(DeviceType.AIR_SENSOR, "AS"));
        assertInstanceOf(StreetLight.class, factory.create(DeviceType.STREET_LIGHT, "SL"));
    }
}
