package org.citysim.devices;

import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.strategies.traffic.TrafficStrategy;
import org.citysim.util.ConfigLoader;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TrafficLight extends CityDevice{
    private static final int INTERVAL_SECONDS = ConfigLoader.getInt("traffic.int.sec");
    private volatile TrafficLightState state = TrafficLightState.RED;
    private TrafficStrategy strategy;

    public TrafficLight(String id) {
        super(id,INTERVAL_SECONDS, DeviceType.TRAFFIC_LIGHT);
    }

    /**
     * Assigns the traffic strategy
     * @param strategy strategy implementation
     * @throws NullPointerException if strategy is null
     */
    public void setStrategy(TrafficStrategy strategy){
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
    }

    /**
     * Advances the traffic light state, schedules next change
     */
    @Override
    public void performAction() {
        if(strategy == null) return;

        switch (state) {
            case RED -> state = TrafficLightState.GREEN;
            case GREEN -> state = TrafficLightState.YELLOW;
            case YELLOW -> state = TrafficLightState.RED;
        }

        int nextInterval = strategy.computeGreenTime(state);

        future = getCity().getThreadPool().getScheduler().schedule(
                this::performAction,
                nextInterval,
                TimeUnit.SECONDS
        );

        getCity().notifyListeners(this, CityEventType.TRAFFIC_LIGHT_CHANGE,
                "Light changed to: " + state + " (next change in " + nextInterval + "s)");

    }
}
