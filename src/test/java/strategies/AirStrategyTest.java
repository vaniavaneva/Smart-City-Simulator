package strategies;

import org.citysim.strategies.air.AverageStrategy;
import org.citysim.strategies.air.PeakDetectionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AirStrategies tests")
public class AirStrategyTest {

    private final AverageStrategy avgStrategy = new AverageStrategy();
    private final PeakDetectionStrategy peakStrategy = new PeakDetectionStrategy();

    @Test @DisplayName("Empty measurements return 0")
    void empty_return(){

        assertEquals(0, avgStrategy.analyzeQuality(new ArrayDeque<>()));
        assertEquals(0, peakStrategy.analyzeQuality(new ArrayDeque<>()));
    }

    @Test @DisplayName("Null measurements return 0")
    void null_return(){

        assertEquals(0, avgStrategy.analyzeQuality(null));
    }

    @Test @DisplayName("Weighted average ignores negative")
    void weighted_negative(){

        Deque<Double> data = new ArrayDeque<>();
        data.add(10.0);
        data.add(20.0);
        data.add(30.0);
        data.add(-30.0);

        double result = avgStrategy.analyzeQuality(data);

        //(10*1 + 20*2 + 30*3) / 6 = 140/6 = 23.33
        assertEquals(23.33, result, 0.01);
    }

    @Test @DisplayName("PeakStrategy calculates max value")
    void max_value(){

        Deque<Double> data = new ArrayDeque<>();
        data.add(10.0);
        data.add(50.0);
        data.add(20.0);

        assertEquals(50.0, peakStrategy.analyzeQuality(data));
    }

    @Test @DisplayName("Strategy handles single value")
    void single_value(){

        Deque<Double> data = new ArrayDeque<>();
        data.add(10.0);

        assertEquals(10.0, peakStrategy.analyzeQuality(data));
    }
}