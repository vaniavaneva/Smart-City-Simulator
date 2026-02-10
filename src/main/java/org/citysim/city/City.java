package org.citysim.city;

import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.*;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.observers.CityEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class City {
    private final List<CityDevice> devices = new CopyOnWriteArrayList<>();
    private final List<CityEventListener> listeners = new CopyOnWriteArrayList<>();
    private CityThreadPool pool;


    /**
     * @param pool - thread pool instance
     */
    public void setThreadPool(CityThreadPool pool){
        this.pool = pool;
    }


    /**
     * @return thread pool
     * @throws IllegalStateException if pool not set
     */
    public CityThreadPool getThreadPool() {
        if (pool == null)
            throw new IllegalStateException("Thread pool not set");
        return pool;
    }

    /**
     * @param device - city device to add
     */
    public void addDevice(CityDevice device) {
        device.setCity(this);
        devices.add(device);
    }

    /**
     * @return copy of all devices
     */
    public List<CityDevice> getAllDevices() {
        return List.copyOf(devices);
    }

    /**
     * @param listener - listener to add
     */
    public void addListener(CityEventListener listener) {
        listeners.add(listener);
    }

    /*public void notifyListeners(CityDevice device, String message) {
        for (CityEventListener listener : listeners) {
            listener.onStatus(device, message);
        }
    }*/

    /**
     * Notifies listeners about device event
     * @param device - the device generating event
     * @param type - event type
     * @param message - message from device
     */
    public void notifyListeners(CityDevice device, CityEventType type, String message) {
        for (CityEventListener listener : listeners) {
            listener.onEvent(device, type, message);
        }
    }

    /**
     * Starts the simulation
     * Traffic light self-reschedules based of color
     * Other devices use the thread pool
     */
    public void startSimulation(){
        for(CityDevice device : devices){
            if (device.getType() == DeviceType.TRAFFIC_LIGHT) {
                device.performAction();
            } else {
                device.schedule(pool);
            }
        }
    }
}
