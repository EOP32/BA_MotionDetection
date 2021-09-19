package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.*;
import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_ACCELERATION_VALUE;

import android.content.Context;

public class ScoopMotionDetector extends MotionDetector implements SensorDataListener {

    private MotionDetectionState liftMotion;
    private MotionDetectionState dropMotion;
    private MotionDetectionState cameraDownPosition;
    private MotionDetectionState cameraUpPosition;
    private float before;

    public ScoopMotionDetector(Context context, MotionSensorSource motionSensorSource) {
        super(context);
        motionSensorSource.addSensorDataListener(this);
        cameraDownPosition = new MotionDetectionState(false, 0);
        cameraUpPosition = new MotionDetectionState(false, 0);
        liftMotion = new MotionDetectionState(false, 0);
        dropMotion = new MotionDetectionState(false, 0);
        before = 0;
    }

    @Override
    public void detect() {
        long timeNow = timestamp();
        long cameraUpTimeDiff = timeNow - cameraUpPosition.timestamp;
        long cameraDownTimeDiff = timeNow - cameraDownPosition.timestamp;
        long liftTimeDiff = timeNow - liftMotion.timestamp;
        long dropTimeDiff = timeNow - dropMotion.timestamp;

        if (cameraDownPosition.detected &&
                cameraUpPosition.detected &&
                liftMotion.detected &&
                dropMotion.detected &&
                cameraUpTimeDiff < MAX_GENERAL_TIME_DIFF &&
                dropTimeDiff < cameraUpTimeDiff &&
                cameraDownTimeDiff < dropTimeDiff &&
                liftTimeDiff < dropTimeDiff) {

            onMotionDetected(MotionType.SCOOP);
            reset();
        }
    }

    @Override
    public void processAccelerationData(float[] values) {
        float zValue = values[2];

        if (zValue > MIN_SCOOP_ACCELERATION_VALUE) {
            liftMotion.detected = true;
            liftMotion.timestamp = timestamp();
            detect();
        }
        if (zValue < -MIN_GENERAL_ACCELERATION_VALUE) {
            dropMotion.detected = true;
            dropMotion.timestamp = timestamp();
            detect();
        }
    }

    @Override
    public void processGravityData(float[] values) {
        float zValue = values[2];

        if (zValue < -MIN_GENERAL_GRAVITY_VALUE && significantGravityChange(zValue)) {
            cameraUpPosition.detected = true;
            cameraUpPosition.timestamp = timestamp();
            detect();
            before = zValue;
        }
        if (zValue > MIN_GENERAL_GRAVITY_VALUE && significantGravityChange(zValue)) {
            cameraDownPosition.detected = true;
            cameraDownPosition.timestamp = timestamp();
            detect();
            before = zValue;
        }
    }

    private boolean significantGravityChange(float value) {
        if (value < 0) {
            return !(before > -MAX_GRAVITY) || !(before < -MIN_GENERAL_GRAVITY_VALUE);
        } else {
            return !(before < MAX_GRAVITY) || !(before > MIN_GENERAL_GRAVITY_VALUE);
        }
    }

    private void reset() {
        cameraUpPosition.detected = false;
        cameraDownPosition.detected = false;
        liftMotion.detected = false;
        dropMotion.detected = false;
    }

    @Override
    public void processGyroData(float[] values) {

    }

    @Override
    public void processLinearAccelerationData(float[] values) {

    }
}
