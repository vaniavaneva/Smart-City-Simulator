package devices;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.CityDevice;
import org.citysim.devices.TrafficLight;
import org.citysim.events.CityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrafficLight test")
public class TrafficLightTest {
    @Test
    @DisplayName("SetStrategy rejects null")
    void setStrategy_rejects_null(){
        TrafficLight light = new TrafficLight("light");

        assertThrows(NullPointerException.class, () -> light.setStrategy(null));
    }

    @Test
    @DisplayName("PerformAction without strategy does nothing")
    void performAction_noStrategy(){
        TrafficLight light = new TrafficLight("light");
        TestCity city = new TestCity();
        light.setCity(city);

        light.performAction();

        assertNull(city.type);
    }

    @Test
    @DisplayName("PerformAction changes state, sends event")
    void performAction_works(){
        TrafficLight light = new TrafficLight("light");
        TestCity city = new TestCity();
        TestPool pool = new TestPool();

        city.setThreadPool(pool);
        light.setCity(city);
        light.setStrategy(state -> 5);
        light.performAction();

        assertEquals(CityEventType.TRAFFIC_LIGHT_CHANGE, city.type);
        assertTrue(city.message.contains("GREEN"));
    }

    @Test
    @DisplayName("PerformAction changes states")
    void performAction_states(){
        TrafficLight light = new TrafficLight("t");
        TestCity city = new TestCity();
        TestPool pool = new TestPool();

        city.setThreadPool(pool);
        light.setCity(city);

        light.setStrategy(state -> 1);

        light.performAction(); // RED -> GREEN
        assertTrue(city.message.contains("GREEN"));
        light.performAction(); // GREEN -> YELLOW
        assertTrue(city.message.contains("YELLOW"));
        light.performAction(); // YELLOW -> RED
        assertTrue(city.message.contains("RED"));
    }

    @Test
    @DisplayName("PerformAction schedules thread")
    void performAction_schedules(){
        TrafficLight light = new TrafficLight("t");
        TestCity city = new TestCity();
        TestPool pool = new TestPool();

        city.setThreadPool(pool);
        light.setCity(city);

        light.setStrategy(state -> 3);

        light.performAction();

        assertTrue(pool.scheduled.get(), "Should schedule next action");
    }

    static class TestCity extends City {
        CityEventType type;
        String message;

        @Override
        public void notifyListeners(CityDevice device, CityEventType type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    static class TestScheduler extends java.util.concurrent.ScheduledThreadPoolExecutor{
        private final AtomicBoolean flag;

        TestScheduler(AtomicBoolean flag){
            super(1);
            this.flag=flag;
        }

        @Override
        public java.util.concurrent.ScheduledFuture<?> schedule
                (Runnable command, long delay, TimeUnit unit){
            flag.set(true);
            return super.schedule(() -> {}, 0, TimeUnit.SECONDS);
        }
    }

    static class TestPool extends CityThreadPool{
        AtomicBoolean scheduled = new AtomicBoolean(false);

        TestPool(){
            super(1);
        }

        @Override
        public ScheduledExecutorService getScheduler(){
            return new TestScheduler(scheduled);
        }
    }
}
