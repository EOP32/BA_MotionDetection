package com.ba.motiondetectionlib.detection;

import com.ba.motiondetectionlib.model.MotionType;

public interface DetectionBroadcaster {
    void sendBroadcast(MotionType motionType);
}
