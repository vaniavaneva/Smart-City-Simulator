package org.citysim.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {

    /**
     * Creates or retrieves configured logger instance
     * @param name - logger name
     * @return configured logger with color output
     */
    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        if (logger.getHandlers().length == 0) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            handler.setFormatter(new ColorFormatter());
            logger.addHandler(handler);
        }

        return logger;
    }
}


//-Dlog.level=FINE || -Dlog.level=WARNING