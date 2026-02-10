package org.citysim.factory;

import org.citysim.devices.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class DeviceFactory {
    /**
     * Maps device types to constructors.
     */
    private final Map<DeviceType, Function<String, CityDevice>> registry = new EnumMap<>(DeviceType.class);

    /**
     * Registers all supported device types.
     */
    public DeviceFactory() {
        registry.put(DeviceType.TRAFFIC_LIGHT, TrafficLight::new);
        registry.put(DeviceType.BIKE_STATION, BikeStation::new);
        registry.put(DeviceType.AIR_SENSOR, AirSensor::new);
        registry.put(DeviceType.STREET_LIGHT, StreetLight::new);
    }

    /**
     * Creates a new device instance
     * @param type - device type
     * @param id - device identifier
     * @return new device instance
     * @throws IllegalArgumentException if type is unknown
     */
    public CityDevice create(DeviceType type, String id) {
        Function<String, CityDevice> supplier = registry.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown device type: " + type);
        }
        return supplier.apply(id);
    }
}

/*public static CityDevice createDevice(DeviceType type, String id) {
    return switch(type){
        case TRAFFIC_LIGHT -> new TrafficLight(id);
        case AIR_SENSOR -> new AirSensor(id);
        case STREET_LIGHT -> new StreetLight(id);
        case BIKE_STATION -> new BikeStation(id);
    };
}*/
