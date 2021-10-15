package com.ba.motiondetectionlib.detection.detectors;

import com.ba.motiondetectionlib.model.MotionType;

/**
 * this interface shall provide broadcasting ability
 * to detectors, once a motion is successfully detected
 */
public interface DetectionBroadcaster {
    /**
     * call this method if a motion was executed correcty
     * @param motionType represents the detected type of motion
     */
    void sendBroadcast(MotionType motionType);
}
