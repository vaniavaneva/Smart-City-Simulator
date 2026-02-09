package factory;

import org.citysim.factory.DeviceFactory;
import org.citysim.factory.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@DisplayName("DeviceFactory Test")
public class DeviceFactoryTest {
    private final DeviceFactory factory = new DeviceFactory();

    @Test
    @DisplayName("Factory supports all device types")
    void supportsAllTypes() {
        for (DeviceType type : DeviceType.values()) {
            assertDoesNotThrow(() -> factory.create(type, "test"));
        }
    }
}
