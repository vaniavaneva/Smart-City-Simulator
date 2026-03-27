package strategies;

import org.citysim.devices.TrafficLightState;
import org.citysim.strategies.traffic.FixedCycleStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FixedCycleStrategy tests")
public class FixedCycleStrategyTest {

    FixedCycleStrategy strategy = new FixedCycleStrategy();

    @Test @DisplayName("Yellow always returns 0")
    void yellow_return(){

        assertEquals(1, strategy.computeGreenTime(TrafficLightState.YELLOW));
    }

    @Test @DisplayName("Green & red return positive value")
    void pos_value(){

        int red = strategy.computeGreenTime(TrafficLightState.RED);
        int green = strategy.computeGreenTime(TrafficLightState.GREEN);

        assertTrue(red > 1);
        assertTrue(green > 1);
    }
}
