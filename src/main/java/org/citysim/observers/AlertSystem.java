package org.citysim.observers;

import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.citysim.util.LoggerFactory;

import java.util.logging.Logger;

public class AlertSystem implements CityEventListener{
    private static final Logger logger =
            LoggerFactory.getLogger("ALERTSYSTEM");
    @Override
    public void onEvent(CityDevice device, CityEventType type, String message) {
        if (type == CityEventType.ALERT) {
            logger.warning("[ALERT] {" + device.getId() + "} " + message);
        }
    }
}
