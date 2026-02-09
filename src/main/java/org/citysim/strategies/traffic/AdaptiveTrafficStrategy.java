package org.citysim.strategies.traffic;

import org.citysim.devices.TrafficLightState;
import org.citysim.util.ConfigLoader;

import static java.lang.Math.log;

public class AdaptiveTrafficStrategy implements TrafficStrategy{
    private final int minGreenDuration;
    private final int maxGreenDuration;
    private final int baseVehicleThreshold;
    private final double scalingFactor;
    private double historicalAverage = 0;

    public AdaptiveTrafficStrategy(int minGreenDuration, int maxGreenDuration, int baseVehicleThreshold, double scalingFactor) {
        if (minGreenDuration <= 0 || maxGreenDuration <= minGreenDuration) {
            throw new IllegalArgumentException("Invalid duration: min must be > 0 and max > min");
        }
        if (baseVehicleThreshold <= 0) {
            throw new IllegalArgumentException("Base vehicle threshold must be positive");
        }
        if (scalingFactor <= 0) {
            throw new IllegalArgumentException("Scaling factor must be positive");
        }
        this.minGreenDuration = minGreenDuration;
        this.maxGreenDuration = maxGreenDuration;
        this.baseVehicleThreshold = baseVehicleThreshold;
        this.scalingFactor = scalingFactor;
    }

    private int getCurrentVehicleCount(){
        int vehicleBase = ConfigLoader.getInt("traffic.vehicle.base");
        int vehicleRange = ConfigLoader.getInt("traffic.vehicle.range");
        return vehicleBase + (int)(Math.random() * vehicleRange); // 5-45
    }

    private double calculateWeightedVehicleCount(int currentCount){
        historicalAverage = historicalAverage * 0.7 + currentCount * 0.3;
        return currentCount * 0.7 + historicalAverage * 0.3;
    }

    @Override
    public int computeGreenTime(TrafficLightState state) {
        if (state == TrafficLightState.YELLOW) return 1;

        int current = getCurrentVehicleCount();
        double weightedCount = calculateWeightedVehicleCount(current);

        double duration = minGreenDuration + (maxGreenDuration - minGreenDuration) * log(weightedCount + 1) / log(baseVehicleThreshold * scalingFactor);

        if(duration < minGreenDuration) duration = minGreenDuration;
        if(duration > maxGreenDuration) duration = maxGreenDuration;

        return (int) duration;
    }
}
