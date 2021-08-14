package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.*;

public class DropMotionDetector implements Detector {

    private MotionDetectionState dropMotion;
    private MotionDetectionState cameraDownPosition;
    private MotionDetectionState cameraUpPosition;
    private DetectionSuccessCallback callback;

    public DropMotionDetector(DetectionSuccessCallback callback) {
        cameraDownPosition = new MotionDetectionState(false, 0);
        cameraUpPosition = new MotionDetectionState(false, 0);
        dropMotion = new MotionDetectionState(false, 0);
        this.callback = callback;
    }

    @Override
    public void detect() {
        long timeNow = timestamp();
        long cameraUpTimeDiff = timeNow - cameraUpPosition.timestamp;
        long cameraDownTimeDiff = timeNow - cameraDownPosition.timestamp;
        long liftTimeDiff = timeNow - dropMotion.timestamp;

        if (cameraDownPosition.detected && cameraUpPosition.detected && dropMotion.detected) {
            if (cameraDownTimeDiff < MAX_GENERAL_TIME_DIFF && liftTimeDiff < MAX_GENERAL_TIME_DIFF && cameraUpTimeDiff < cameraDownTimeDiff) {
                callback.onMotionDetected(MotionType.SCOOP);
                cameraUpPosition.detected = false;
                cameraDownPosition.detected = false;
                dropMotion.detected = false;
            }
        }
    }

    public void processAccelerationData(float zValue) {
        if (zValue > MIN_DROP_ACCELERATION_VALUE) {
            dropMotion.detected = true;
            dropMotion.timestamp = timestamp();
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
