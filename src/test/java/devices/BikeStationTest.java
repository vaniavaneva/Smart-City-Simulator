package devices;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.BikeStation;
import org.citysim.events.CityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

@DisplayName("BikeStation tests")
public class BikeStationTest {

    @Test @DisplayName("PerformAction() rents bikes [0.0 - 0.4) 40%")
    void rent_bike(){

        BikeStation bike = new BikeStation("BS") {
            @Override protected double random() {
                return 0.1; // RENT
            }
        };
        City city = mock(City.class);

        bike.setCity(city);
        bike.setCapacity(5,5);
        bike.setBikesAvailable(3,3);
        bike.setChargers(2,2);

        bike.performAction();

        assertEquals(2, bike.getBikesAvailable());
        verify(city).notifyListeners(
                eq(bike),
                eq(CityEventType.BIKE_RENTED),
                contains("Bike rented")
        );
    }

    @Test @DisplayName("PerformAction() returns bikes [0.4 - 0.8) 40%")
    void return_bike(){

        BikeStation bike = new BikeStation("BS") {
            @Override protected double random() {
                return 0.6; // RETURN
            }
        };
        City city = mock(City.class);

        bike.setCity(city);
        bike.setCapacity(5,5);
        bike.setBikesAvailable(2,2);
        bike.setChargers(2,2);

        bike.performAction();

        assertEquals(3, bike.getBikesAvailable());
        verify(city).notifyListeners(
                eq(bike),
                eq(CityEventType.BIKE_RETURNED),
                contains("Bike returned")
        );
    }

    @Test @DisplayName("PerformAction() charges bikes [0.8 - 1.0) 20%")
    void charge_bike(){

        BikeStation bike = new BikeStation("BS") {
            @Override protected double random() {
                return 0.95; // CHARGE
            }
        };
        City city = new City();
        CityThreadPool pool = new CityThreadPool(1);

        city.setThreadPool(pool);
        bike.setCity(city);
        bike.setChargers(1,1);

        List<CityEventType> events = new ArrayList<>();
        city.addListener((device, type, message) -> events.add(type));

        bike.performAction();

        assertTrue(events.contains(CityEventType.BIKE_CHARGING));
        pool.safeShutdown(1, TimeUnit.SECONDS);
    }

    @Test @DisplayName("NotifyListeners() sends alert for no bikes")
    void noBikes_alert() {

        BikeStation bike = new BikeStation("BS") {
            @Override
            protected double random() {
                return 0.1;
            }
        };
            City city = mock(City.class);
        CityThreadPool pool = new CityThreadPool(1);
        city.setThreadPool(pool);
        bike.setCity(city);

        bike.setCapacity(1, 1);
        bike.setBikesAvailable(0, 0);
        bike.setChargers(1, 1);

        bike.performAction();

        verify(city, atLeastOnce()).notifyListeners(
                eq(bike),
                eq(CityEventType.ALERT),
                contains("No bikes available")
        );
        pool.safeShutdown(1, TimeUnit.SECONDS);
    }
}
