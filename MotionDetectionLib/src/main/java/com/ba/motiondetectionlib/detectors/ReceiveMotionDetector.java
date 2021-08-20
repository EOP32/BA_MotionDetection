package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.MAX_TIME_DIFF_RECEIVE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_ACCELERATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_ROTATION_VALUE;

public class ReceiveMotionDetector implements Detector {

    private final DetectionSuccessCallback callback;
    private final MotionDetectionState rotationGesture;
    private final MotionDetectionState upMotionGesture;
    private boolean portraitMode;
    private float before;

    public ReceiveMotionDetector(DetectionSuccessCallback callback) {
        this.callback = callback;
        rotationGesture = new MotionDetectionState(false, 0);
        upMotionGesture = new MotionDetectionState(false, 0);
        before = 0;
    }

    public void detect() {
        long timeNow = timestamp();
        long diffR = timeNow - rotationGesture.timestamp;
        long diffU = timeNow - upMotionGesture.timestamp;
        long maxTimeDiff = MAX_TIME_DIFF_RECEIVE;

        if (rotationGesture.detected &&
                upMotionGesture.detected &&
                portraitMode &&
                diffR < maxTimeDiff &&
                diffU < maxTimeDiff) {

            callback.onMotionDetected(MotionType.RECEIVE);
            reset();
        }
    }

    public void processAccelerationData(float yValue) {
        if (yValue > MIN_GENERAL_ACCELERATION_VALUE) {
            upMotionGesture.detected = true;
            upMotionGesture.timestamp = timestamp();
            detect();
        }
    }

    public void processGyroData(float yValue) {
        float gyroDiff = before - yValue;

        if (Math.abs(gyroDiff) > MIN_ROTATION_VALUE) {
            rotationGesture.detected = true;
            rotationGesture.timestamp = timestamp();
            detect();
        }
        before = yValue;
    }

    public void processGravityData(float yValue) {
        portraitMode = yValue > MIN_GENERAL_GRAVITY_VALUE;
    }

    private void reset() {
        rotationGesture.detected = false;
        upMotionGesture.detected = false;
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }
}
