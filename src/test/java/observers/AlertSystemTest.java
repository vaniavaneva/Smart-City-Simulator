package observers;

import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.observers.AlertSystem;
import org.citysim.util.LoggerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AlertSystem Test")
public class AlertSystemTest {
    @Test
    @DisplayName("Logs only ALERT events")
    void logsOnlyAlertEvents() {
        AlertSystem system = new AlertSystem();

        Logger logger = LoggerFactory.getLogger("ALERTSYSTEM");

        List<String> logs = new ArrayList<>();

        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logs.add(record.getMessage());
            }
            @Override public void flush() {}
            @Override public void close() {}
        };

        logger.addHandler(handler);

        CityDevice device = new TestDevice();

        system.onEvent(device, CityEventType.STATUS, "status");
        system.onEvent(device, CityEventType.ALERT, "alert");

        logger.removeHandler(handler);

        assertEquals(1, logs.size());
        assertTrue(logs.getFirst().contains("alert"));
    }
    static class TestDevice extends CityDevice {
        TestDevice() {
            super("test", 1, null);
        }

        @Override
        public void performAction() {}
    }
}
