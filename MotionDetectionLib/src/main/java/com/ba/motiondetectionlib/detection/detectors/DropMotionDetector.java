package com.ba.motiondetectionlib.detection.detectors;

import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MAX_GRAVITY;
import static com.ba.motiondetectionlib.model.Constants.MIN_DROP_ACCELERATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;

import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.detection.MotionSensorSource;
import com.ba.motiondetectionlib.detection.SensorDataListener;
import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

public class DropMotionDetector extends MotionDetector implements SensorDataListener {

    private final MotionDetectionState dropMotion;
    private final MotionDetectionState liftMotion;
    private final MotionDetectionState cameraDownPosition;
    private final MotionDetectionState cameraUpPosition;
    private float before;
    private float gravityCache;

    public DropMotionDetector(Context ctx, Intent intent, MotionSensorSource motionSensorSource) {
        super(ctx, intent);
        motionSensorSource.addSensorDataListener(this);
        cameraDownPosition = new MotionDetectionState(false, 0);
        cameraUpPosition = new MotionDetectionState(false, 0);
        dropMotion = new MotionDetectionState(false, 0);
        liftMotion = new MotionDetectionState(false, 0);
        before = 0;
        gravityCache = 0;
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

            onMotionDetected(MotionType.DROP);
            reset();
        }
    }

    @Override
    public void processAccelerationData(float[] values) {
        float zValue = values[2];

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

    @Override
    public void processGravityData(float[] values) {
        float zValue = values[2];

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

    @Override
    public void processGyroData(float[] values) {
    }
}
