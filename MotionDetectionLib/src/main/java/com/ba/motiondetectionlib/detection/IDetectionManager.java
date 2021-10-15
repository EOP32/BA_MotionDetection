package com.ba.motiondetectionlib.detection;

import android.content.Context;
import android.hardware.SensorEvent;

/**
 * responsibilities a detectionManager should have
 */
public interface IDetectionManager {
    /**
     * call this method to instantiate and start detectors
     * @param context needed in the detectors to send broadcast
     */
    void startDetectors(Context context);
}
