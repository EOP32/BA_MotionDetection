package com.ba.motiondetectionlib.detection.detectors;

import com.ba.motiondetectionlib.detection.MotionSensorSource;
import com.ba.motiondetectionlib.detection.SensorDataListener;
import com.ba.motiondetectionlib.detection.detectors.MotionDetector;
import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.MAX_TIME_DIFF_RECEIVE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_ACCELERATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_ROTATION_VALUE;

import android.content.Context;
import android.content.Intent;

public class ReceiveMotionDetector extends MotionDetector implements SensorDataListener {

    private final MotionDetectionState rotationGesture;
    private final MotionDetectionState upMotionGesture;
    private boolean portraitMode;
    private float before;

    public ReceiveMotionDetector(Context context, Intent intent, MotionSensorSource motionSensorSource) {
        super(context, intent);
        motionSensorSource.addSensorDataListener(this);
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

            onMotionDetected(MotionType.RECEIVE);
            reset();
        }
    }

    @Override
    public void processAccelerationData(float[] values) {
        float yValue = values[1];

        if (yValue > MIN_GENERAL_ACCELERATION_VALUE) {
            upMotionGesture.detected = true;
            upMotionGesture.timestamp = timestamp();
            detect();
        }
    }

    @Override
    public void processGravityData(float[] values) {
        float yValue = values[1];
        portraitMode = yValue > MIN_GENERAL_GRAVITY_VALUE;
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
