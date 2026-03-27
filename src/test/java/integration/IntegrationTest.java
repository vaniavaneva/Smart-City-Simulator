package integration;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.AirSensor;
import org.citysim.devices.BikeStation;
import org.citysim.devices.TrafficLight;
import org.citysim.events.CityEventType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Integration tests")
public class IntegrationTest {

    @Test @DisplayName("AirSensor flow")
    void airSensor_alertFlow(){

        City city = new City();

        AirSensor sensor = new AirSensor("AS");
        sensor.setStrategy(m -> 999.0); // force alert

        AtomicBoolean alertReceived = new AtomicBoolean(false);

        city.addListener((d, t, m) -> {
            if (t == CityEventType.ALERT) {
                alertReceived.set(true);
            }
        });

        city.addDevice(sensor);
        sensor.performAction();

        assertTrue(alertReceived.get());
    }

    @Test @DisplayName("BikeStation flow")
    void bikeStation_flow(){

        City city = new City();
        CityThreadPool pool = new CityThreadPool(1);
        city.setThreadPool(pool);

        BikeStation station = new BikeStation("BS") {
            @Override protected double random() {
                return 0.95;
            }
        };

        station.setChargers(1, 1);

        AtomicBoolean chargingTriggered = new AtomicBoolean(false);

        city.addListener((d, t, m) -> {
            if (t == CityEventType.BIKE_CHARGING) {
                chargingTriggered.set(true);
            }
        });

        city.addDevice(station);
        station.performAction();

        assertTrue(chargingTriggered.get());

        pool.safeShutdown(1, TimeUnit.SECONDS);
    }

    @Test @DisplayName("TrafficLight flow")
    void trafficLight_flow() throws InterruptedException {

        City city = new City();
        CityThreadPool pool = new CityThreadPool(1);
        city.setThreadPool(pool);

        TrafficLight tl = new TrafficLight("TL");
        tl.setStrategy(state -> 1);

        CountDownLatch latch = new CountDownLatch(1);

        city.addListener((d,t,m) -> {
            if(t == CityEventType.TRAFFIC_LIGHT_CHANGE) {
                latch.countDown();
            }
        });

        city.addDevice(tl);
        city.startSimulation();

        assertTrue(latch.await(2, TimeUnit.SECONDS));

        pool.safeShutdown(1, TimeUnit.SECONDS);
    }
}
