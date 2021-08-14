package com.ba.motiondetectionlib.broadcast;

import com.ba.motiondetectionlib.model.MotionType;

public interface MotionDetectionListener {
    void motionDetected(MotionType type);
}
