package com.ba.motiondetectionlib.detection.detectors;

import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MIN_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_VERTICAL_ACCELERATION_VALUE;
import android.content.Context;
import android.content.Intent;
import com.ba.motiondetectionlib.detection.MotionSensorSource;
import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

public class DropMotionDetector extends MotionDetector {

    private final MotionDetectionState dropMotion;
    private final MotionDetectionState liftMotion;
    private final MotionDetectionState cameraDownPosition;
    private final MotionDetectionState cameraUpPosition;
    private boolean gravityValuePositive;

    public DropMotionDetector(Context ctx, Intent intent, MotionSensorSource motionSensorSource) {
        super(ctx, intent, motionSensorSource);
        cameraDownPosition = new MotionDetectionState(false, 0);
        cameraUpPosition = new MotionDetectionState(false, 0);
        dropMotion = new MotionDetectionState(false, 0);
        liftMotion = new MotionDetectionState(false, 0);
        gravityValuePositive = false;
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
                liftTimeDiff < MAX_GENERAL_TIME_DIFF &&
                cameraUpTimeDiff < cameraDownTimeDiff &&
                dropTimeDiff < liftTimeDiff) {

            sendBroadcast(MotionType.DROP);
            reset();
        }
    }

    @Override
    public void processAccelerationData(float[] values) {
        float zValue = values[2];

        if (zValue > MIN_VERTICAL_ACCELERATION_VALUE) {
            if (gravityValuePositive) {
                liftMotion.detected = true;
                liftMotion.timestamp = timestamp();
            } else {
                dropMotion.detected = true;
                dropMotion.timestamp = timestamp();
            }
            detect();
        }
    }

    @Override
    public void processGravityData(float[] values) {
        float zValue = values[2];
        gravityValuePositive = zValue >= 0;

        if (zValue < -MIN_GRAVITY_VALUE) {
            cameraUpPosition.detected = true;
            cameraUpPosition.timestamp = timestamp();
            detect();
        }
        if (zValue > MIN_GRAVITY_VALUE) {
            cameraDownPosition.detected = true;
            cameraDownPosition.timestamp = timestamp();
            detect();
        }
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
