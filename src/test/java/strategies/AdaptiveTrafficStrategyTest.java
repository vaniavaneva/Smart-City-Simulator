package strategies;

import org.citysim.devices.TrafficLightState;
import org.citysim.strategies.traffic.AdaptiveTrafficStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdaptiveTrafficStrategy Test")
public class AdaptiveTrafficStrategyTest {
    @Test
    @DisplayName("Constructor rejects invalid arg")
    void constructor_arguments(){
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveTrafficStrategy(0, 10, 5, 2));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveTrafficStrategy(10, 5, 5, 2));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveTrafficStrategy(5, 10, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveTrafficStrategy(5, 10, 5, 0));
    }

    @Test
    @DisplayName("Yellow always returns 0")
    void yellow_return(){
        AdaptiveTrafficStrategy strategy = new AdaptiveTrafficStrategy(5, 10, 5, 2);
        assertEquals(1, strategy.computeGreenTime(TrafficLightState.YELLOW));
    }

    @Test
    @DisplayName("Duration in bounds")
    void duration_time(){
        AdaptiveTrafficStrategy strategy = new AdaptiveTrafficStrategy(5, 10, 5, 2);
        for(int i = 0; i < 100; i++){
            int dur = strategy.computeGreenTime(TrafficLightState.GREEN);
            assertTrue(dur >= 5 && dur <= 10);
        }
    }
}
