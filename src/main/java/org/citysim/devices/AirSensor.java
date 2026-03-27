package org.citysim.devices;

import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.util.ConfigLoader;
import org.citysim.strategies.air.AirAnalysisStrategy;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

public class AirSensor extends CityDevice{
    private final Deque<Double> history = new LinkedList<>();
    private final int MAX_HISTORY = ConfigLoader.getInt("air.max.history");
    private static final int INTERVAL_SECONDS = ConfigLoader.getInt("air.int.sec");
    private static final double AIR_QUALITY_THRESHOLD = ConfigLoader.getDouble("air.quality.threshold");
    private static final int AIR_PM_BASE = ConfigLoader.getInt("air.pm.base");
    private static final int AIR_PM_RANGE = ConfigLoader.getInt("air.pm.range");
    private AirAnalysisStrategy strategy;

    public AirSensor(String id) {
        super(id, INTERVAL_SECONDS, DeviceType.AIR_SENSOR);
    }

    /**
     * Sets the air quality analysis strategy
     * @param strategy - analysis algorithm
     * @throws NullPointerException if strategy is null
     */
    public void setStrategy(AirAnalysisStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
    }

    public Deque<Double> getHistory() {
        return history;
    }

    /**
     * Generates new air measurement and evaluates air quality
     */
    @Override
    public synchronized void performAction() {
        double pm25 = AIR_PM_BASE + Math.random() * AIR_PM_RANGE;

        history.addLast(pm25);
        if (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }

        double quality = strategy.analyzeQuality(new LinkedList<>(history));

        if (quality > AIR_QUALITY_THRESHOLD) {
            getCity().notifyListeners(this, CityEventType.ALERT,
                    "Poor air quality PM2.5=" + String.format("%.2f", pm25));
        } else {
            getCity().notifyListeners(this, CityEventType.STATUS,
                    "Air OK: " + String.format("%.2f", pm25));
        }
    }
}
