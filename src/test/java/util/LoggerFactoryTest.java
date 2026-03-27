package util;

import org.citysim.util.LoggerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LoggerFactory test")
public class LoggerFactoryTest {

    @Test @DisplayName("Logs info")
    void logger_test(){

        Logger logger = LoggerFactory.getLogger("TEST");
        assertNotNull(logger);
        assertTrue(logger.getHandlers().length > 0);
    }
}
