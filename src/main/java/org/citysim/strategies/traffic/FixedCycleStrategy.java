package org.citysim.strategies.traffic;

import org.citysim.devices.TrafficLightState;
import org.citysim.util.ConfigLoader;

import java.util.Objects;

public class FixedCycleStrategy implements TrafficStrategy{

    private static final int FIXED_GREEN = ConfigLoader.getInt("traffic.fixed.green");

    /**
     * Returns fixed green time regardless of traffic conditions
     * @param state - current traffic light color
     * @return green duration in seconds, 1 sec for yellow
     */
    @Override
    public int computeGreenTime(TrafficLightState state){
        if(state == TrafficLightState.YELLOW){
            return 1;
        } else {
            return FIXED_GREEN;
        }
    }
}
