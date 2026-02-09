package org.citysim.city;

import org.citysim.concurrent.CityThreadPool;
import org.citysim.devices.*;
import org.citysim.events.CityEventType;
import org.citysim.factory.DeviceType;
import org.citysim.observers.CityEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class City {
    private final List<CityDevice> devices = new CopyOnWriteArrayList<>();
    private final List<CityEventListener> listeners = new CopyOnWriteArrayList<>();
    private CityThreadPool pool;

    public void setThreadPool(CityThreadPool pool){
        this.pool = pool;
    }

    public CityThreadPool getThreadPool() {
        if (pool == null)
            throw new IllegalStateException("Thread pool not set");
        return pool;
    }

    public void addDevice(CityDevice device) {
        device.setCity(this);
        devices.add(device);
    }

    public List<CityDevice> getAllDevices() {
        return List.copyOf(devices);
    }

    public void addListener(CityEventListener listener) {
        listeners.add(listener);
    }

    /*public void notifyListeners(CityDevice device, String message) {
        for (CityEventListener listener : listeners) {
            listener.onStatus(device, message);
        }
    }*/

    public void notifyListeners(CityDevice device, CityEventType type, String message) {
        for (CityEventListener listener : listeners) {
            listener.onEvent(device, type, message);
        }
    }

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
