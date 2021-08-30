package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SendMotionDetectorTest {

    private SendMotionDetector detector;
    private DetectionSuccessCallback callback;

    private final float MOTION_RIGHT_VALID = 8f;
    private final float MOTION_LEFT_VALID = -8f;
    private final float ROTATE_RIGHT_VALID = -6f;
    private final float ROTATE_LEFT_VALID = 6f;
    private final float GRAVITY_BACK_VALID = -8f;
    private final float GRAVITY_FORTH_VALID = 8f;

    @Before
    public void setup() {
        callback = mock(DetectionSuccessCallback.class);
        detector = new SendMotionDetector(callback);
    }

    @Test
    public void validValuesRightHandSuccess() throws InterruptedException {
        // valid order for right handed send motion
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);

        verify(callback, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validValuesLeftHandSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(10);
        // for the left hand gravity value order changes which means forth_gravity should be detected first
        // then back_gravity
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);

        verify(callback, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validBorderlineValuesRightHandSuccess() throws InterruptedException {
        detector.processAccelerationData(7.1f);
        Thread.sleep(10);
        detector.processGyroData(-5.1f);
        Thread.sleep(10);
        detector.processGravityData(-7.1f);
        Thread.sleep(10);
        detector.processGravityData(7.1f);

        verify(callback, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validBorderlineValuesLeftHandSuccess() throws InterruptedException {
        detector.processAccelerationData(-7.1f);
        Thread.sleep(10);
        detector.processGyroData(5.1f);
        Thread.sleep(10);
        detector.processGravityData(7.1f);
        Thread.sleep(10);
        detector.processGravityData(-7.1f);

        verify(callback, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validRightHandBorderlineTimeSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(200);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(100);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(100);
        detector.processGravityData(GRAVITY_FORTH_VALID);

        verify(callback, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validLeftHandBorderlineTimeSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID);
        Thread.sleep(150);
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(150);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(150);
        detector.processGravityData(GRAVITY_BACK_VALID);

        verify(callback, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void rightHandInvalidRotationShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void leftHandInvalidRotationShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void rightHandInvalidGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void leftHandInvalidGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void rightHandInvalidAccelShouldFail() throws InterruptedException {
        detector.processAccelerationData(6);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void leftHandInvalidAccelShouldFail() throws InterruptedException {
        detector.processAccelerationData(-6);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void rightHandValidValuesWrongOrderShouldFail() throws InterruptedException {
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);

        verifyZeroInteractions(callback);
    }

    @Test
    public void leftHandValidValuesWrongOrderShouldFail() throws InterruptedException {
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processAccelerationData(MOTION_LEFT_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void rightHandValidValuesWrongOrderShouldFail2() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);

        verifyZeroInteractions(callback);
    }

    @Test
    public void leftHandValidValuesWrongOrderShouldFail2() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);

        verifyZeroInteractions(callback);
    }

    @Test
    public void rightValuesWrongOrderShouldFail2() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID);
        Thread.sleep(10);

        verifyZeroInteractions(callback);
    }
}