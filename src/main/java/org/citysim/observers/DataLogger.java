package org.citysim.observers;

import org.citysim.devices.*;
import org.citysim.events.CityEventType;
import org.citysim.util.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DataLogger implements CityEventListener {
    private static final Logger logger =
            LoggerFactory.getLogger("DATALOGGER");
    private final File logFile;
    private boolean loggingEnabled = true;
    private List<String> alerts = new ArrayList<>();

    private int trafficLightChanges = 0;

    private int streetLightOnCount = 0;
    private int streetLightTotalEvents = 0;

    private int timesBikesRented = 0;
    private int timesBikesReturned = 0;
    private int timesBikesCharged = 0;

    /**
     * Creates a new data logger
     * @param filePath - destination file path
     */
    public DataLogger(String filePath) {
        this.logFile = ensureFileExists(filePath);
    }

    /**
     * Processes incoming simulation events
     * @param device - the device generating event
     * @param type - event type
     * @param message - message from device
     */
    @Override
    public void onEvent(CityDevice device, CityEventType type, String message) {
        switch(type) {
            case ALERT -> alerts.add(device.getId() + " " + message);
            case TRAFFIC_LIGHT_CHANGE -> trafficLightChanges++;
            case STREET_LIGHT_CHANGE -> {
                if(message.contains("ON")) streetLightOnCount++;
                else streetLightTotalEvents++;}
            case BIKE_RENTED -> timesBikesRented++;
            case BIKE_RETURNED -> timesBikesReturned++;
            case BIKE_CHARGING -> timesBikesCharged++;
        }
    }

    /**
     * Writes statistics to log file
     */
    public void saveInfo(){
        clearFile();
        double percent = streetLightTotalEvents == 0 ? 0 : (streetLightOnCount / (double) streetLightTotalEvents) * 100.0;
        write("All traffic lights changed " + trafficLightChanges + " times.\n\n");
        write("Street lights were ON for " + String.format("%.2f", percent) + "% of the simulation.\n\n");
        write("Bikes were rented " + timesBikesRented + " times.\n\n");
        write("Bikes were returned " + timesBikesReturned + " times.\n\n");
        write("Bikes were charged " + timesBikesCharged + " times.\n\n");
        write("Alerts occurred: " + alerts.size() + "\n\n");
        for(String a : alerts){
            write("\t" + a + "\n");
        }
    }

    /**
     * Confirms file exists
     * @param path - path to file
     * @return file
     */
    private File ensureFileExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                logger.warning("File doesn't exist: " + ex.getMessage());
            }
        }
        return f;
    }

    /**
     * Clears file before writing
     */
    private void clearFile() {
        try (FileWriter fw = new FileWriter(logFile, false)) { // false = overwrite
            fw.write("");
        } catch (IOException ex) {
            logger.warning("Failed to clear file: " + ex.getMessage());
        }
    }


    /**
     * Writes in file if logging enabled
     * @param text - text to write in file
     */
    private void write(String text) {
        if(!loggingEnabled) return;
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(text);
        } catch (IOException e) {
            logger.warning("Logging disabled due to error: " + e.getMessage());
            loggingEnabled = false;
        }
    }
}
