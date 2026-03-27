package engine;

import org.citysim.engine.SimulationEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("SimulationEngine test")
public class SimulationEngineTest {

    @Test @DisplayName("Engine simulates correctly, doesn't crash")
    void engine_runs() throws InterruptedException {

        SimulationEngine engine = new SimulationEngine();

        Thread thread = new Thread(engine::start);

        thread.start();
        thread.join();

        assertFalse(thread.isAlive());
    }
}
