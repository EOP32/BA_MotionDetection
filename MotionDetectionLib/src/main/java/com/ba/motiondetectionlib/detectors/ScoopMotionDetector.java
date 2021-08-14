package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_ACCELERATION_VALUE;

public class ScoopMotionDetector implements Detector {

    private MotionDetectionState liftMotion;
    private MotionDetectionState cameraDownPosition;
    private MotionDetectionState cameraUpPosition;
    private DetectionSuccessCallback callback;

    public ScoopMotionDetector(DetectionSuccessCallback callback) {
        cameraDownPosition = new MotionDetectionState(false, 0);
        cameraUpPosition = new MotionDetectionState(false, 0);
        liftMotion = new MotionDetectionState(false, 0);
        this.callback = callback;
    }

    @Override
    public void detect() {
        long timeNow = timestamp();
        long cameraUpTimeDiff = timeNow - cameraUpPosition.timestamp;
        long cameraDownTimeDiff = timeNow - cameraDownPosition.timestamp;
        long liftTimeDiff = timeNow - liftMotion.timestamp;

        if (cameraDownPosition.detected && cameraUpPosition.detected && liftMotion.detected) {
            if (cameraUpTimeDiff < MAX_GENERAL_TIME_DIFF && liftTimeDiff < MAX_GENERAL_TIME_DIFF && cameraDownTimeDiff < cameraUpTimeDiff) {
                callback.onMotionDetected(MotionType.SCOOP);
                cameraUpPosition.detected = false;
                cameraDownPosition.detected = false;
                liftMotion.detected = false;
            }
        }
    }

    public void processAccelerationData(float zValue) {
        if (zValue > MIN_GENERAL_ACCELERATION_VALUE) {
            liftMotion.detected = true;
            liftMotion.timestamp = timestamp();
            detect();
        }
    }

    public void processGravityData(float zValue) {
        if (zValue < -MIN_GENERAL_GRAVITY_VALUE) {
            cameraUpPosition.detected = true;
            cameraUpPosition.timestamp = timestamp();
            detect();
        }
        if (zValue > MIN_GENERAL_GRAVITY_VALUE) {
            cameraDownPosition.detected = true;
            cameraDownPosition.timestamp = timestamp();
            detect();
        }
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }
}
