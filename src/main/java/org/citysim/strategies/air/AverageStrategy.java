package org.citysim.strategies.air;

import java.util.Deque;

public class AverageStrategy implements AirAnalysisStrategy{
    /**
     * Calculates weighted air quality score from measurements
     * @param measurements - air quality readings from oldest to newest
     * @return weighted average of measurements or 0 if no valid data
     */
    @Override
    public double analyzeQuality(Deque<Double> measurements) {
        if(measurements == null || measurements.isEmpty()) return 0;

        double weightedSum = 0;
        double totalWeight = 0;
        int weight = 1;

        int n = measurements.size();

        for(Double value : measurements) {
            if(value > 0) {
                weightedSum += value * weight;
                totalWeight += weight;
            }
            weight++;
        }

        return totalWeight == 0 ? 0 : weightedSum / totalWeight;
    }
}
