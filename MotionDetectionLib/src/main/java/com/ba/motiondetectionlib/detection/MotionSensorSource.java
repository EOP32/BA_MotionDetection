package com.ba.motiondetectionlib.detection;

import android.hardware.SensorEvent;

import com.ba.motiondetectionlib.detection.detectors.SensorDataListener;

/**
 * this interface represents the observable in the observer pattern
 */
public interface MotionSensorSource {
    /**
     * call this method to register new listeners/observers
     *
     * @param newListener object that implements the interface SensorDataListener
     */
    void addSensorDataListener(SensorDataListener newListener);

    /**
     * call this method to remove all listeners/observers
     */
    void removeSensorDataListeners();

    /**
     * if service has received new sensor data, forward those data to all listeners
     *
     * @param event SensorEvent with relevant information like sensor values, sensor name and type
     */
    void forwardSensorData(SensorEvent event);

}
