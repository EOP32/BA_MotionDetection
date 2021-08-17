package com.ba.motiondetectionlib.detectors;

import android.util.Log;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.*;

public class SendMotionDetector implements Detector {

    private MotionDetectionState motionRight;
    private MotionDetectionState motionLeft;
    private MotionDetectionState rotationClockWise;
    private MotionDetectionState rotationCounterClockWise;
    private MotionDetectionState backPosition;
    private MotionDetectionState forthPosition;
    private DetectionSuccessCallback callback;

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

        if (backPosition.detected && forthPosition.detected && motionRight.detected && rotationCounterClockWise.detected) {
            if (backPositionTimeDiff < maxTimeDiff && throwTimeDiffRight < maxTimeDiff && forthPositionTimeDiff < backPositionTimeDiff) {
                callback.onMotionDetected(MotionType.SEND);
                resetDetection();
            }
        }

        if (backPosition.detected && forthPosition.detected && motionLeft.detected && rotationClockWise.detected) {
            if (forthPositionTimeDiff < maxTimeDiff && throwTimeDiffLeft < maxTimeDiff && backPositionTimeDiff < forthPositionTimeDiff) {
                callback.onMotionDetected(MotionType.SEND);
                resetDetection();
            }
        }
    }

    public void processAccelerationData(float xValue) {
        if (xValue > MIN_SEND_ACCELERATION_VALUE) {
            motionRight.detected = true;
            motionRight.timestamp = timestamp();
            Log.d(TAG, "motion right");
            detect();
        }
        if (xValue < -MIN_SEND_ACCELERATION_VALUE) {
            motionLeft.detected = true;
            motionLeft.timestamp = timestamp();
            Log.d(TAG, "motion left");
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
            Log.d(TAG, "rotation clockwise");
            detect();
        }

        if (yValue < -MIN_SEND_ROTATION_VALUE) {
            rotationCounterClockWise.detected = true;
            rotationCounterClockWise.timestamp = timestamp();
            Log.d(TAG, "rotation counter clockwise");
            detect();
        }
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }

    private void resetDetection() {
        backPosition.detected = false;
        forthPosition.detected = false;
        motionRight.detected = false;
        motionLeft.detected = false;
        rotationClockWise.detected = false;
        rotationCounterClockWise.detected = false;
    }
}
