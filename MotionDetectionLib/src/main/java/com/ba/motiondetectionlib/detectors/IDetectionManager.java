package com.ba.motiondetectionlib.detectors;

import android.hardware.SensorEvent;

public interface IDetectionManager {
    void onSensorChanged(SensorEvent event);
}
