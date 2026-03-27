package observers;

import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.observers.Dashboard;
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

@DisplayName("Dashboard test")
public class DashboardTest {

    @Test @DisplayName("Logs only STATUS events")
    void logsOnlyAlertEvents() {

        Dashboard system = new Dashboard();
        Logger logger = LoggerFactory.getLogger("DASHBOARD");
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

        CityDevice device = new CityDevice("test", 1, DeviceType.AIR_SENSOR) {
            @Override public void performAction() {}
        };

        system.onEvent(device, CityEventType.STATUS, "status");
        system.onEvent(device, CityEventType.ALERT, "alert");

        logger.removeHandler(handler);

        assertEquals(1, logs.size());
        assertTrue(logs.getFirst().contains("status"));
    }
}
