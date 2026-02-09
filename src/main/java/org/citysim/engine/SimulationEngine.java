package org.citysim.engine;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.AirSensor;
import org.citysim.devices.BikeStation;
import org.citysim.devices.StreetLight;
import org.citysim.devices.TrafficLight;
import org.citysim.factory.DeviceFactory;
import org.citysim.factory.DeviceType;
import org.citysim.observers.AlertSystem;
import org.citysim.observers.Dashboard;
import org.citysim.observers.DataLogger;
import org.citysim.strategies.air.AverageStrategy;
import org.citysim.strategies.air.PeakDetectionStrategy;
import org.citysim.strategies.traffic.AdaptiveTrafficStrategy;
import org.citysim.strategies.traffic.FixedCycleStrategy;
import org.citysim.util.ConfigLoader;
import org.citysim.util.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SimulationEngine {

    private static final Logger logger = LoggerFactory.getLogger("SIMULATIONENGINE");

    private static int simulationDuration = ConfigLoader.getInt("simulation.duration");
    private static int simulationTimeout = ConfigLoader.getInt("simulation.timeout");
    private int threadPoolSize;

    private City city;
    private CityThreadPool pool;
    private DataLogger dataLogger;
    private DeviceFactory factory;

    private int minGreenDuration, maxGreenDuration, baseVehicleThreshold, scalingFactor;
    private int minCapacity, maxCapacity;
    private int minBikes, maxBikes;
    private int minChargers, maxChargers;

    private TrafficLight tl1, tl2, tl3, tl4;
    private AirSensor as1, as2, as3, as4;
    private StreetLight sl1, sl2;
    private BikeStation bs1, bs2;

    public void start(){
        threadPoolSize = ConfigLoader.getInt("thread.pool.size");

        city = new City();
        pool = new CityThreadPool(threadPoolSize);
        dataLogger = new DataLogger("city_log.txt");
        factory = new DeviceFactory();

        city.setThreadPool(pool);
        city.addListener(new Dashboard());
        city.addListener(new AlertSystem());
        city.addListener(dataLogger);

        minGreenDuration = ConfigLoader.getInt("traffic.adapt.min");
        maxGreenDuration = ConfigLoader.getInt("traffic.adapt.max");
        baseVehicleThreshold = ConfigLoader.getInt("traffic.vehicle.threshold");
        scalingFactor = ConfigLoader.getInt("traffic.scale.factor");

        minCapacity = ConfigLoader.getInt("bike.min.capacity");
        maxCapacity = ConfigLoader.getInt("bike.max.capacity");
        minBikes = ConfigLoader.getInt("bike.min.bikes");
        maxBikes = ConfigLoader.getInt("bike.max.bikes");
        minChargers = ConfigLoader.getInt("bike.min.chargers");
        maxChargers = ConfigLoader.getInt("bike.max.chargers");

        tl1 = (TrafficLight) factory.create(DeviceType.TRAFFIC_LIGHT, "TL-01");
        tl2 = (TrafficLight) factory.create(DeviceType.TRAFFIC_LIGHT, "TL-02");
        tl3 = (TrafficLight) factory.create(DeviceType.TRAFFIC_LIGHT, "TL-03");
        tl4 = (TrafficLight) factory.create(DeviceType.TRAFFIC_LIGHT, "TL-04");

        as1 = (AirSensor) factory.create(DeviceType.AIR_SENSOR, "AS-01");
        as2 = (AirSensor) factory.create(DeviceType.AIR_SENSOR, "AS-02");
        as3 = (AirSensor) factory.create(DeviceType.AIR_SENSOR, "AS-03");
        as4 = (AirSensor) factory.create(DeviceType.AIR_SENSOR, "AS-04");

        sl1 = (StreetLight) factory.create(DeviceType.STREET_LIGHT, "SL-01");
        sl2 = (StreetLight) factory.create(DeviceType.STREET_LIGHT, "SL-02");

        bs1 = (BikeStation) factory.create(DeviceType.BIKE_STATION, "BS-01");
        bs2 = (BikeStation) factory.create(DeviceType.BIKE_STATION, "BS-02");

        tl1.setStrategy(new AdaptiveTrafficStrategy(minGreenDuration, maxGreenDuration, baseVehicleThreshold, scalingFactor));
        tl2.setStrategy(new FixedCycleStrategy());
        tl3.setStrategy(new AdaptiveTrafficStrategy(minGreenDuration, maxGreenDuration, baseVehicleThreshold, scalingFactor));
        tl4.setStrategy(new FixedCycleStrategy());

        as1.setStrategy(new AverageStrategy());
        as2.setStrategy(new PeakDetectionStrategy());
        as3.setStrategy(new AverageStrategy());
        as4.setStrategy(new PeakDetectionStrategy());

        bs1.setCapacity(minCapacity, maxCapacity);
        bs1.setBikesAvailable(minBikes, maxBikes);
        bs1.setChargers(minChargers, maxChargers);

        bs2.setCapacity(minCapacity, maxCapacity);
        bs2.setBikesAvailable(minBikes, maxBikes);
        bs2.setChargers(minChargers, maxChargers);

        city.addDevice(tl1);
        city.addDevice(tl2);
        city.addDevice(tl3);
        city.addDevice(tl4);
        city.addDevice(as1);
        city.addDevice(as2);
        city.addDevice(as3);
        city.addDevice(as4);
        city.addDevice(sl1);
        city.addDevice(sl2);
        city.addDevice(bs1);
        city.addDevice(bs2);

        city.startSimulation();

        try {
            Thread.sleep(
                    SimulationEngine.simulationDuration * 1000L
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        stop();
    }

    private void stop(){
        pool.safeShutdown(simulationTimeout, TimeUnit.SECONDS);
        logger.info("Simulation stopped.");
        dataLogger.saveInfo();
    }
}