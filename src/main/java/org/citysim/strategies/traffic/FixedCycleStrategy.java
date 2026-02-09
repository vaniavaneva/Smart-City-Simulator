package org.citysim.strategies.traffic;

import org.citysim.devices.TrafficLightState;
import org.citysim.util.ConfigLoader;

import java.util.Objects;

public class FixedCycleStrategy implements TrafficStrategy{

    private static final int FIXED_GREEN = ConfigLoader.getInt("traffic.fixed.green");

    @Override
    public int computeGreenTime(TrafficLightState state){
        if(state == TrafficLightState.YELLOW){
            return 1;
        } else {
            return FIXED_GREEN;
        }
    }
}
