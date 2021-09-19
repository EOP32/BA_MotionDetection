package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionDetectionState;
import com.ba.motiondetectionlib.model.MotionType;

import static com.ba.motiondetectionlib.model.Constants.MAX_GENERAL_TIME_DIFF;
import static com.ba.motiondetectionlib.model.Constants.MIN_GENERAL_GRAVITY_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_SEND_ACCELERATION_VALUE;
import static com.ba.motiondetectionlib.model.Constants.MIN_SEND_ROTATION_VALUE;

import android.content.Context;

public class SendMotionDetector extends MotionDetector implements SensorDataListener {

    private final MotionDetectionState motionRight;
    private final MotionDetectionState motionLeft;
    private final MotionDetectionState rotationClockWise;
    private final MotionDetectionState rotationCounterClockWise;
    private final MotionDetectionState backPosition;
    private final MotionDetectionState forthPosition;

    public SendMotionDetector(Context context, MotionSensorSource motionSensorSource) {
        super(context);
        motionSensorSource.addSensorDataListener(this);
        backPosition = new MotionDetectionState(false, 0);
        forthPosition = new MotionDetectionState(false, 0);
        motionRight = new MotionDetectionState(false, 0);
        motionLeft = new MotionDetectionState(false, 0);
        rotationClockWise = new MotionDetectionState(false, 0);
        rotationCounterClockWise = new MotionDetectionState(false, 0);
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

            onMotionDetected(MotionType.SEND);
            reset();
        }

        if (backPosition.detected &&
                forthPosition.detected &&
                motionLeft.detected &&
                rotationClockWise.detected &&
                forthPositionTimeDiff < maxTimeDiff &&
                throwTimeDiffLeft < maxTimeDiff &&
                backPositionTimeDiff < forthPositionTimeDiff) {

            onMotionDetected(MotionType.SEND);
            reset();
        }
    }

    @Override
    public void processLinearAccelerationData(float[] values) {
        float xValue = values[0];

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

    @Override
    public void processGravityData(float[] values) {
        float xValue = values[0];

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

    @Override
    public void processGyroData(float[] values) {
        float yValue = values[1];

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

    private void reset() {
        backPosition.detected = false;
        forthPosition.detected = false;
        motionRight.detected = false;
        motionLeft.detected = false;
        rotationClockWise.detected = false;
        rotationCounterClockWise.detected = false;
    }


    @Override
    public void processAccelerationData(float[] values) {

    }
}
