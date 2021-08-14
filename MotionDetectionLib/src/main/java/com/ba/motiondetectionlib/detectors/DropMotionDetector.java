package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.*;

public class DropMotionDetector implements Detector {

    private DetectionSuccessCallback callback;
    private boolean cameraPositionFacingDown;

    public DropMotionDetector(DetectionSuccessCallback callback) {
        this.callback = callback;
        cameraPositionFacingDown = false;
    }

    @Override
    public void detect() {
        if (cameraPositionFacingDown) {
            callback.onMotionDetected(MotionType.DROP);
        }
    }

    public void processAccelerationData(float zValue) {
        if (zValue < MIN_DROP_ACCELERATION_VALUE) {
            detect();
        }
    }

    public void processGravityData(float zValue) {
        cameraPositionFacingDown = zValue > MIN_GENERAL_GRAVITY_VALUE;
    }
}
