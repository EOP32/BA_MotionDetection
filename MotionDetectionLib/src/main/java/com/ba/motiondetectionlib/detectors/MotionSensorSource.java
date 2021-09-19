package com.ba.motiondetectionlib.detectors;

public interface MotionSensorSource {
    void addSensorDataListener(SensorDataListener newListener);
    void removeSensorDataListeners();
}
