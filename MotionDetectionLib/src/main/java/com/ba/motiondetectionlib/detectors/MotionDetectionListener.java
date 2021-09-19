package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

public interface MotionDetectionListener {
    void onMotionDetected(MotionType motionType);
}
