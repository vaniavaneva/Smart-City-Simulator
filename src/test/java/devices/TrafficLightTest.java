package devices;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.TrafficLight;
import org.citysim.events.CityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TrafficLight tests")
public class TrafficLightTest {

    @Test @DisplayName("SetStrategy() rejects null")
    void setStrategy_rejects_null(){

        TrafficLight light = new TrafficLight("light");

        assertThrows(NullPointerException.class, () -> light.setStrategy(null));
    }

    @Test @DisplayName("PerformAction() without strategy does nothing")
    void performAction_noStrategy(){

        TrafficLight light = new TrafficLight("light");
        City city = new City();
        light.setCity(city);

        AtomicBoolean eventSent = new AtomicBoolean(false);
        city.addListener((device, type, message) -> eventSent.set(true));

        light.performAction();

        assertFalse(eventSent.get());
    }

    @Test @DisplayName("PerformAction() changes state, sends event")
    void performAction_works(){

        City city = mock(City.class);
        CityThreadPool pool = mock(CityThreadPool.class);
        ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);

        when(pool.getScheduler()).thenReturn(scheduler);
        when(city.getThreadPool()).thenReturn(pool);

        TrafficLight light = new TrafficLight("TL");
        light.setCity(city);
        light.setStrategy(state -> 5);

        light.performAction();

        verify(city).notifyListeners(
                eq(light),
                eq(CityEventType.TRAFFIC_LIGHT_CHANGE),
                contains("GREEN")
        );

        verify(scheduler).schedule(any(Runnable.class), eq(5L), any());  //5 int L long
    }

    @Test @DisplayName("PerformAction() changes states R->G->Y")
    void performAction_states(){

        City city = mock(City.class);
        CityThreadPool pool = mock(CityThreadPool.class);
        ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);

        when(pool.getScheduler()).thenReturn(scheduler);
        when(city.getThreadPool()).thenReturn(pool);

        TrafficLight light = new TrafficLight("TL");
        light.setCity(city);
        light.setStrategy(state -> 1);

        light.performAction(); // RED -> GREEN
        light.performAction(); // GREEN -> YELLOW
        light.performAction(); // YELLOW -> RED

        InOrder inOrder = inOrder(city);

        inOrder.verify(city).notifyListeners(eq(light), eq(CityEventType.TRAFFIC_LIGHT_CHANGE), contains("GREEN"));
        inOrder.verify(city).notifyListeners(eq(light), eq(CityEventType.TRAFFIC_LIGHT_CHANGE), contains("YELLOW"));
        inOrder.verify(city).notifyListeners(eq(light), eq(CityEventType.TRAFFIC_LIGHT_CHANGE), contains("RED"));
    }

    @Test @DisplayName("PerformAction() schedules thread")
    void performAction_schedules(){

        City city = mock(City.class);
        CityThreadPool pool = mock(CityThreadPool.class);
        ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);

        when(pool.getScheduler()).thenReturn(scheduler);
        when(city.getThreadPool()).thenReturn(pool);

        TrafficLight light = new TrafficLight("TL");
        light.setCity(city);
        light.setStrategy(state -> 3);

        light.performAction();

        verify(scheduler).schedule(
                any(Runnable.class),
                eq(3L),
                any(TimeUnit.class)
        );
    }
}
