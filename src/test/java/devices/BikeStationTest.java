package devices;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.BikeStation;
import org.citysim.devices.CityDevice;
import org.citysim.events.CityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BikeStation test")
public class BikeStationTest {
    @Test
    @DisplayName("Rent bike works")
    void rent_bike(){
        TestBikeStation bike = new TestBikeStation(0.1);
        TestCity city = new TestCity();

        bike.setCity(city);
        bike.setCapacity(5,5);
        bike.setBikesAvailable(3,3);
        bike.setChargers(2,2);

        bike.performAction();

        assertEquals(2, bike.getBikesAvailable());
        assertTrue(city.events.contains(CityEventType.BIKE_RENTED));
    }

    @Test
    @DisplayName("Return bike works")
    void return_bike(){
        TestBikeStation bike = new TestBikeStation(0.6);
        TestCity city = new TestCity();

        bike.setCity(city);
        bike.setCapacity(5,5);
        bike.setBikesAvailable(2,2);
        bike.setChargers(2,2);

        bike.performAction();

        assertEquals(3, bike.getBikesAvailable());
        assertTrue(city.events.contains(CityEventType.BIKE_RETURNED));
    }

    @Test
    @DisplayName("Charging bike works")
    void charge_bike(){
        TestBikeStation s = new TestBikeStation(0.95);
        TestCity city = new TestCity();
        CityThreadPool pool = new CityThreadPool(1);

        s.setCity(city);
        city.setThreadPool(pool);
        s.setChargers(1,1);

        s.performAction();

        assertTrue(city.events.contains(CityEventType.BIKE_CHARGING));
        pool.safeShutdown(1, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("No bikes sends alert")
    void noBikes_alert(){
        TestBikeStation s = new TestBikeStation(0.1);
        TestCity city = new TestCity();
        s.setCity(city);

        s.setCapacity(5,5);
        s.setBikesAvailable(0,0);
        s.setChargers(2,2);

        s.performAction();

        assertTrue(city.events.contains(CityEventType.ALERT));
    }

    static class TestBikeStation extends BikeStation{
        private double random;

        TestBikeStation(double random){
            super("station");
            this.random=random;
        }

        void setRandom(double r){
            random = r;
        }

        @Override
        protected double random(){
            return random;
        }
    }

    static class TestCity extends City {
        List<CityEventType> events = new ArrayList<>();

        @Override
        public void notifyListeners(CityDevice device, CityEventType type, String message){
            events.add(type);
        }
    }
}
