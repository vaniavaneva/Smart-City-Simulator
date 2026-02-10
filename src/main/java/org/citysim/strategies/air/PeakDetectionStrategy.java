package org.citysim.strategies.air;

import java.util.Deque;

public class PeakDetectionStrategy implements AirAnalysisStrategy{
    /**
     * Calculates max air quality score from measurements
     * @param measurements - air quality readings from oldest to newest
     * @return max measurement
     */
    @Override
    public double analyzeQuality(Deque<Double> measurements) {
        if(measurements.isEmpty()) return 0;
        double max = 0;

        for(double value : measurements){
            if(value > max) max = value;

        } return max;
    }
}
