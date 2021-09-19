package com.ba.motiondetectionlib.detectors;

import android.content.Context;
import android.hardware.SensorEvent;

public interface IDetectionManager {
    void startDetectors(Context context, MotionSensorSource sensorSource);
    void forwardSensorData(SensorEvent event);
}
