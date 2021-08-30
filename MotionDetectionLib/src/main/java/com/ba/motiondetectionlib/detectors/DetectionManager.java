package com.ba.motiondetectionlib.detectors;

import android.hardware.SensorEvent;

public class DetectionManager implements IDetectionManager {
    @Override
    public void onSensorChanged(SensorEvent event) {
        // send to all IDetectors

    }
}
