package com.ba.motiondetectionlib.detection.detectors;

import static com.ba.motiondetectionlib.model.Constants.MAX_TIME_DIFF_RECEIVE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_ROTATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_VERTICAL_ACCELERATION_VALUE;

import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.detection.MotionSensorSource;
import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

public class ReceiveMotionDetector extends MotionDetector {

    private final MotionDetectionState rotationGesture;
    private final MotionDetectionState upMotionGesture;
    private boolean portraitMode;
    private float before;

    public ReceiveMotionDetector(Context context, Intent intent, MotionSensorSource motionSensorSource) {
        super(context, intent, motionSensorSource);
        rotationGesture = new MotionDetectionState(false, 0);
        upMotionGesture = new MotionDetectionState(false, 0);
        before = 0;
    }

    @Override
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

            sendBroadcast(MotionType.RECEIVE);
            reset();
        }
    }

    @Override
    public void processAccelerationData(float[] values) {
        float yValue = values[1];

        if (yValue > MIN_VERTICAL_ACCELERATION_VALUE) {
            upMotionGesture.detected = true;
            upMotionGesture.timestamp = timestamp();
            detect();
        }
    }

    @Override
    public void processGravityData(float[] values) {
        float yValue = values[1];
        portraitMode = yValue > MIN_GRAVITY_VALUE;
    }

    @Override
    public void processGyroData(float[] values) {
        float yValue = values[1];
        float gyroDiff = before - yValue;

        if (Math.abs(gyroDiff) > MIN_ROTATION_VALUE) {
            rotationGesture.detected = true;
            rotationGesture.timestamp = timestamp();
            detect();
        }
        before = yValue;
    }

    private void reset() {
        rotationGesture.detected = false;
        upMotionGesture.detected = false;
    }
}
