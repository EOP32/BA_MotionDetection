package com.ba.motiondetectionlib.detection;

public interface MotionSensorSource {
    void addSensorDataListener(SensorDataListener newListener);
    void removeSensorDataListeners();
}
