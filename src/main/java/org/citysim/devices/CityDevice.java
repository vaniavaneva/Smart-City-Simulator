package org.citysim.devices;

import org.citysim.city.City;
import org.citysim.concurrent.CityThreadPool;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class CityDevice {
    private final String id;
    protected DeviceType type;
    private City city;
    protected volatile int intervalSeconds;
    protected ScheduledFuture<?> future;

    public CityDevice(String id, int intervalSeconds, DeviceType type) {
        this.id = Objects.requireNonNull(id, "Device id cannot be null");
        this.type = Objects.requireNonNull(type, "Device type cannot be null");
        if(intervalSeconds <= 0) throw new IllegalArgumentException("Seconds can't be <= 0");
        this.intervalSeconds = intervalSeconds;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public String getId(){
        return id;
    }

    public void setCity(City city) {
        this.city = Objects.requireNonNull(city, "City cannot be null");
    }

    public City getCity(){
        return city;
    }

    public DeviceType getType() {
        return type;
    }

    public void updateStatus(String message) {
        if (city != null) {
            city.notifyListeners(this, CityEventType.STATUS, message);
        }
    }

    /*public void stop() {
        if(future != null && !future.isCancelled()) {
            future.cancel(false);
        }
    }*/

    public void schedule(CityThreadPool pool) {
        int initialDelay = (int)(Math.random() * intervalSeconds);

        future = pool.getScheduler().scheduleAtFixedRate(
                this::performAction,
                initialDelay,
                intervalSeconds,
                TimeUnit.SECONDS
        );
    }

    public abstract void performAction();
}
