package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

public interface DetectionSuccessCallback {
    void onMotionDetected(MotionType type);
}
