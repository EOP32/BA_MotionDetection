package com.ba.motiondetectionlib.detection;

import com.ba.motiondetectionlib.model.MotionType;

public interface MotionDetectionListener {
    void onMotionDetected(MotionType motionType);

    void sendBroadcast(String motionType);
}
