package com.ba.motiondetectionlib.detection;

public interface SensorDataListener {
    void detect();
    void processAccelerationData(float[] values);
    void processGravityData(float[] values);
    void processGyroData(float[] values);
}
