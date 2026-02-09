package org.citysim.devices;

import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.util.ConfigLoader;

public class StreetLight extends CityDevice implements LightSensor{
    private static final int INTERVAL_SECONDS = ConfigLoader.getInt("street.int.sec");
    private static final int DARK_START = ConfigLoader.getInt("street.dark.start");
    private static final int DARK_END = ConfigLoader.getInt("street.dark.end");
    private int hour = 0;

    public StreetLight(String id){
        super(id, INTERVAL_SECONDS, DeviceType.STREET_LIGHT);
    }

    @Override
    public void performAction(){
        hour = (hour + 1) % 24;

        boolean on = isDark(hour);

        getCity().notifyListeners(this, CityEventType.STREET_LIGHT_CHANGE,
                "Hour: " + hour + " | Light " + (on ? "ON" : "OFF"));
    }

    @Override
    public boolean isDark(int hour) {
        return hour >= DARK_START || hour < DARK_END;
    }
}
