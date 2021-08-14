package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.*;

public class SendMotionDetector implements Detector {

    private MotionDetectionState sidewaysGesture;
    private MotionDetectionState backPosition;
    private MotionDetectionState forthPosition;
    private DetectionSuccessCallback callback;

    public SendMotionDetector(DetectionSuccessCallback callback) {
        backPosition = new MotionDetectionState(false, 0);
        forthPosition = new MotionDetectionState(false, 0);
        sidewaysGesture = new MotionDetectionState(false, 0);
        this.callback = callback;
    }

    @Override
    public void detect() {
        long timeNow = timestamp();
        long backPositionTimeDiff = timeNow - backPosition.timestamp;
        long forthPositionTimeDiff = timeNow - forthPosition.timestamp;
        long liftTimeDiff = timeNow - sidewaysGesture.timestamp;
        long maxTimeDiff = MAX_GENERAL_TIME_DIFF;

        if (backPosition.detected && forthPosition.detected && sidewaysGesture.detected) {
            if (backPositionTimeDiff < maxTimeDiff && liftTimeDiff < maxTimeDiff && forthPositionTimeDiff < backPositionTimeDiff) {
                callback.onMotionDetected(MotionType.SEND);
                backPosition.detected = false;
                forthPosition.detected = false;
                sidewaysGesture.detected = false;
            }
        }
    }

    public void processAccelerationData(float xValue) {
        float x = Math.abs(xValue);

        if (x > MIN_SEND_ACCELERATION_VALUE) {
            sidewaysGesture.detected = true;
            sidewaysGesture.timestamp = timestamp();
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

    private long timestamp() {
        return System.currentTimeMillis();
    }
}
