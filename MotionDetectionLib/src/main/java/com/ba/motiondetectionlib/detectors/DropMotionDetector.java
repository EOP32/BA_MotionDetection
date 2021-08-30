package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MAX_GRAVITY;
import static com.ba.motiondetectionlib.model.Constants.MIN_DROP_ACCELERATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;

public class DropMotionDetector implements IDetector {

    private final MotionDetectionState dropMotion;
    private final MotionDetectionState liftMotion;
    private final MotionDetectionState cameraDownPosition;
    private final MotionDetectionState cameraUpPosition;
    private final DetectionSuccessCallback callback;
    private float before;
    private float gravityCache;

    public DropMotionDetector(DetectionSuccessCallback callback) {
        cameraDownPosition = new MotionDetectionState(false, 0);
        cameraUpPosition = new MotionDetectionState(false, 0);
        dropMotion = new MotionDetectionState(false, 0);
        liftMotion = new MotionDetectionState(false, 0);
        before = 0;
        gravityCache = 0;
        this.callback = callback;
    }

    @Override
    public void detect() {
        long timeNow = timestamp();
        long cameraUpTimeDiff = timeNow - cameraUpPosition.timestamp;
        long cameraDownTimeDiff = timeNow - cameraDownPosition.timestamp;
        long dropTimeDiff = timeNow - dropMotion.timestamp;
        long liftTimeDiff = timeNow - liftMotion.timestamp;

        if (cameraDownPosition.detected &&
                cameraUpPosition.detected &&
                dropMotion.detected &&
                liftMotion.detected &&
                cameraDownTimeDiff < MAX_GENERAL_TIME_DIFF &&
                dropTimeDiff < MAX_GENERAL_TIME_DIFF &&
                liftTimeDiff < cameraDownTimeDiff &&
                cameraUpTimeDiff < liftTimeDiff &&
                dropTimeDiff < cameraUpTimeDiff) {

            callback.onMotionDetected(MotionType.DROP);
            reset();
        }
    }

    public void processAccelerationData(float zValue) {
        if (zValue < MIN_DROP_ACCELERATION_VALUE && gravityCache < -MIN_GENERAL_GRAVITY_VALUE) {
            dropMotion.detected = true;
            dropMotion.timestamp = timestamp();
            detect();
        }
        if (zValue > -MIN_DROP_ACCELERATION_VALUE) {
            liftMotion.detected = true;
            liftMotion.timestamp = timestamp();
            detect();
        }
    }

    public void processGravityData(float zValue) {
        if (zValue < -MIN_GENERAL_GRAVITY_VALUE && significantGravityChange()) {
            cameraUpPosition.detected = true;
            cameraUpPosition.timestamp = timestamp();
            detect();
            before = zValue;
        }
        if (zValue > MIN_GENERAL_GRAVITY_VALUE) {
            cameraDownPosition.detected = true;
            cameraDownPosition.timestamp = timestamp();
            detect();
            before = zValue;
        }
        gravityCache = zValue;
    }

    private boolean significantGravityChange() {
        return !(before > -MAX_GRAVITY) || !(before < -MIN_GENERAL_GRAVITY_VALUE);
    }

    private void reset() {
        cameraUpPosition.detected = false;
        cameraDownPosition.detected = false;
        dropMotion.detected = false;
        liftMotion.detected = false;
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }
}
