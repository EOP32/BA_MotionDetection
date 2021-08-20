package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_SEND_ACCELERATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_SEND_ROTATION_VALUE;

public class SendMotionDetector implements Detector {

    private final MotionDetectionState motionRight;
    private final MotionDetectionState motionLeft;
    private final MotionDetectionState rotationClockWise;
    private final MotionDetectionState rotationCounterClockWise;
    private final MotionDetectionState backPosition;
    private final MotionDetectionState forthPosition;
    private final DetectionSuccessCallback callback;

    public SendMotionDetector(DetectionSuccessCallback callback) {
        backPosition = new MotionDetectionState(false, 0);
        forthPosition = new MotionDetectionState(false, 0);
        motionRight = new MotionDetectionState(false, 0);
        motionLeft = new MotionDetectionState(false, 0);
        rotationClockWise = new MotionDetectionState(false, 0);
        rotationCounterClockWise = new MotionDetectionState(false, 0);
        this.callback = callback;
    }

    @Override
    public void detect() {
        long timeNow = timestamp();
        long backPositionTimeDiff = timeNow - backPosition.timestamp;
        long forthPositionTimeDiff = timeNow - forthPosition.timestamp;
        long throwTimeDiffRight = timeNow - motionRight.timestamp;
        long throwTimeDiffLeft = timeNow - motionLeft.timestamp;
        long maxTimeDiff = MAX_GENERAL_TIME_DIFF;

        if (backPosition.detected &&
                forthPosition.detected &&
                motionRight.detected &&
                rotationCounterClockWise.detected &&
                backPositionTimeDiff < maxTimeDiff &&
                throwTimeDiffRight < maxTimeDiff &&
                forthPositionTimeDiff < backPositionTimeDiff) {

            callback.onMotionDetected(MotionType.SEND);
            reset();
        }

        if (backPosition.detected &&
                forthPosition.detected &&
                motionLeft.detected &&
                rotationClockWise.detected &&
                forthPositionTimeDiff < maxTimeDiff &&
                throwTimeDiffLeft < maxTimeDiff &&
                backPositionTimeDiff < forthPositionTimeDiff) {

            callback.onMotionDetected(MotionType.SEND);
            reset();
        }
    }

    public void processAccelerationData(float xValue) {
        if (xValue > MIN_SEND_ACCELERATION_VALUE) {
            motionRight.detected = true;
            motionRight.timestamp = timestamp();
            detect();
        }
        if (xValue < -MIN_SEND_ACCELERATION_VALUE) {
            motionLeft.detected = true;
            motionLeft.timestamp = timestamp();
            detect();
        }
    }

    public void processGravityData(float xValue) {
        if (xValue < -MIN_GENERAL_GRAVITY_VALUE) {
            backPosition.detected = true;
            backPosition.timestamp = timestamp();
            detect();
        }
        if (xValue > MIN_GENERAL_GRAVITY_VALUE) {
            forthPosition.detected = true;
            forthPosition.timestamp = timestamp();
            detect();
        }
    }

    public void processGyroData(float yValue) {
        if (yValue > MIN_SEND_ROTATION_VALUE) {
            rotationClockWise.detected = true;
            rotationClockWise.timestamp = timestamp();
            detect();
        }

        if (yValue < -MIN_SEND_ROTATION_VALUE) {
            rotationCounterClockWise.detected = true;
            rotationCounterClockWise.timestamp = timestamp();
            detect();
        }
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }

    private void reset() {
        backPosition.detected = false;
        forthPosition.detected = false;
        motionRight.detected = false;
        motionLeft.detected = false;
        rotationClockWise.detected = false;
        rotationCounterClockWise.detected = false;
    }
}
