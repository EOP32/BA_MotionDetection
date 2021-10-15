package com.ba.motiondetectionlib.detection.detectors;

/**
 * this interface represents the "observers" in the observer/listener pattern
 */
public interface SensorDataListener {
    /**
     * call this method to check if all conditions
     * for a successful execution of a motion are met
     */
    void detect();

    /**
     * this method is called if the sensormanager detected a new linear acceleration sensor event
     * the observable triggers this method and sends it to all its observers
     *
     * @param values float array with sensor data
     */
    void processAccelerationData(float[] values);

    /**
     * this method is called if the sensormanager detected a new gravty sensor event
     * the observable triggers this method and sends it to all its observers
     *
     * @param values float array with sensor data
     */
    void processGravityData(float[] values);

    /**
     * this method is called if the sensormanager detected a new gyroscope sensor event
     * the observable triggers this method and sends it to all its observers
     *
     * @param values float array with sensor data
     */
    void processGyroData(float[] values);
}
