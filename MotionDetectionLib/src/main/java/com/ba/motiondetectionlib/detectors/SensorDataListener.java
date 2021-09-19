package com.ba.motiondetectionlib.detectors;

public interface SensorDataListener {
    void detect();
    void processAccelerationData(float[] values);
    void processLinearAccelerationData(float[] values);
    void processGravityData(float[] values);
    void processGyroData(float[] values);
}
