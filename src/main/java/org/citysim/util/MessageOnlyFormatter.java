package org.citysim.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MessageOnlyFormatter extends Formatter {
    /**
     * Formats log record as plain text
     * @param record - log record
     * @return message with newline
     */
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\n";
    }
}