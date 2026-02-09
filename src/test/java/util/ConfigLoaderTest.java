package util;

import org.citysim.util.ConfigLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ConfigLoader Test")
public class ConfigLoaderTest {
    @Test
    @DisplayName("Reads int")
    void read_int(){
        int value = ConfigLoader.getInt("simulation.duration");
        assertTrue(value > 0);
    }

    @Test
    @DisplayName("Reads double")
    void read_double(){
        double value = ConfigLoader.getDouble("traffic.scale.factor");
        assertTrue(value > 0);
    }

    @Test
    @DisplayName("Missing key throws ex")
    void missing_key(){
        assertThrows(RuntimeException.class, () -> ConfigLoader.getInt("asd"));
    }
}
