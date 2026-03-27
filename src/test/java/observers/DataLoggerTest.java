package observers;

import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.observers.DataLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DataLogger test")
public class DataLoggerTest {

    @TempDir
    Path tempDir;

    @Test @DisplayName("Triggers events, writes in file")
    void trigger_write() throws IOException {

        Path file = tempDir.resolve("log.txt");
        DataLogger logger = new DataLogger(file.toString());
        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR){
            @Override public void performAction() {}
        };

        logger.onEvent(device, CityEventType.TRAFFIC_LIGHT_CHANGE, "");
        logger.onEvent(device, CityEventType.BIKE_RENTED, "");
        logger.onEvent(device, CityEventType.BIKE_RETURNED, "");
        logger.onEvent(device, CityEventType.BIKE_CHARGING, "");
        logger.onEvent(device, CityEventType.ALERT, "");
        logger.onEvent(device, CityEventType.STREET_LIGHT_CHANGE, "ON");
        logger.onEvent(device, CityEventType.STREET_LIGHT_CHANGE, "OFF");

        logger.saveInfo();

        String output = Files.readString(file);

        assertTrue(output.contains("changed 1"));
        assertTrue(output.contains("ON for"));
        assertTrue(output.contains("rented 1"));
        assertTrue(output.contains("returned 1"));
        assertTrue(output.contains("charged 1"));
        assertTrue(output.contains("occurred: 1"));
    }
}
