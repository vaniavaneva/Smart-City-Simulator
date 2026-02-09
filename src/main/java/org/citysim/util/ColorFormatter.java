package org.citysim.util;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ColorFormatter extends Formatter {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";

    @Override
    public String format(LogRecord record) {
        String color = WHITE;

        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            color = RED;
        }

        return color + record.getMessage() + RESET + "\n";
    }
}
