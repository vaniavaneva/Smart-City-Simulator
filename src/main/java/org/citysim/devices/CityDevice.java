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

    /**
     * Creates new device with fixed execution interval
     * @param id - unique device identifier
     * @param intervalSeconds - execution interval in seconds (must be > 0)
     * @param type - device type
     * @throws NullPointerException if id or type is null
     * @throws IllegalArgumentException if interval is negative
     */
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

    /**
     * Attaches this device to city
     * @param city - owning city instance
     * @throws NullPointerException if city is null
     */
    public void setCity(City city) {
        this.city = Objects.requireNonNull(city, "City cannot be null");
    }

    /**
     * Returns the city this device belongs to
     * @return attached city
     * @throws IllegalStateException if device is not attached to a city
     */
    public City getCity() {
        City c = city;
        if (c == null)
            throw new IllegalStateException("Device not attached to a city");
        return c;
    }

    public DeviceType getType() {
        return type;
    }

    /**
     * Notifies listeners for event
     * @param message - message from device
     */
    public void updateStatus(String message) {
        getCity().notifyListeners(this, CityEventType.STATUS, message);
    }

    /*public void stop() {
        if(future != null && !future.isCancelled()) {
            future.cancel(false);
        }
    }*/

    /**
     * Schedules periodic execution of this device using thread pool
     * @param pool - thread pool used for scheduling
     * @throws NullPointerException if pool is null
     */
    public void schedule(CityThreadPool pool) {
        Objects.requireNonNull(pool, "Thread pool cannot be null");

        int initialDelay = (int) (Math.random() * intervalSeconds);

        future = pool.getScheduler().scheduleAtFixedRate(
                this::performAction,
                initialDelay,
                intervalSeconds,
                TimeUnit.SECONDS
        );
    }


    /**
     * Performs one simulation step
     */
    public abstract void performAction();
}
