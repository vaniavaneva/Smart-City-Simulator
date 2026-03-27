package util;

import org.citysim.util.MessageOnlyFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MessageOnlyFormatter Test")
public class MessageOnlyFormatterTest {

    @Test @DisplayName("Displays only message")
    void message_test(){
        MessageOnlyFormatter f = new MessageOnlyFormatter();
        LogRecord r = new LogRecord(Level.INFO, "Test");
        String result = f.format(r);
        assertEquals("Test\n", result);
    }
}
