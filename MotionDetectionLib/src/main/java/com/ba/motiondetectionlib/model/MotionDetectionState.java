package com.ba.motiondetectionlib.model;

public class MotionDetectionState {
    public boolean detected;
    public long timestamp;

    public MotionDetectionState(boolean detected, long timestamp) {
        this.detected = detected;
        this.timestamp = timestamp;
    }
}
