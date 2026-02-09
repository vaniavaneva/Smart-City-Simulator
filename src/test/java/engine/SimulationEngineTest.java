package engine;

import org.citysim.engine.SimulationEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("SimulationEngine Test")
public class SimulationEngineTest {
    @Test
    @DisplayName("Simulation engine runs without crashing")
    void engine_runs() {
        SimulationEngine engine = new SimulationEngine();

        assertDoesNotThrow(engine::start);
    }
}
