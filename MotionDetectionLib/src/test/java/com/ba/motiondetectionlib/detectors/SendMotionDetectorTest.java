package com.ba.motiondetectionlib.detectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;

import com.ba.motiondetectionlib.model.MotionType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SendMotionDetectorTest {

    private SendMotionDetector detector;
    private MotionDetectionListener listener;

    private final float MOTION_RIGHT_VALID = 8f;
    private final float MOTION_LEFT_VALID = -8f;
    private final float ROTATE_RIGHT_VALID = -6f;
    private final float ROTATE_LEFT_VALID = 6f;
    private final float GRAVITY_BACK_VALID = -8f;
    private final float GRAVITY_FORTH_VALID = 8f;

    private final float[] MOTION_RIGHT_VALID_ARR = new float[]{MOTION_RIGHT_VALID, 0, 0};
    private final float[] MOTION_LEFT_VALID_ARR = new float[]{MOTION_LEFT_VALID, 0, 0};
    private final float[] ROTATE_RIGHT_VALID_ARR = new float[]{0, ROTATE_RIGHT_VALID, 0};
    private final float[] ROTATE_LEFT_VALID_ARR = new float[]{0, ROTATE_LEFT_VALID, 0};
    private final float[] GRAVITY_BACK_VALID_ARR = new float[]{GRAVITY_BACK_VALID, 0, 0};
    private final float[] GRAVITY_FORTH_VALID_ARR = new float[]{GRAVITY_FORTH_VALID, 0, 0};

    @Before
    public void setup() {
        listener = mock(MotionDetectionListener.class);
        detector = new SendMotionDetector(mock(Context.class), mock(MotionSensorSource.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the right hand                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void validValuesRightHandSuccess() throws InterruptedException {
        // valid order for right handed send motion
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validValuesLeftHandSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        // for the left hand gravity value order changes which means forth_gravity should be detected first
        // then back_gravity
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validBorderlineValuesRightHandSuccess() throws InterruptedException {
        detector.processAccelerationData(new float[]{7.1f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, -5.1f, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{-7.1f, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{7.1f, 0, 0});

        verify(listener, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validBorderlineValuesLeftHandSuccess() throws InterruptedException {
        detector.processAccelerationData(new float[]{-7.1f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 5.1f, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{7.1f, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{-7.1f, 0, 0});

        verify(listener, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validRightHandBorderlineTimeSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(200);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(100);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(100);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void validLeftHandBorderlineTimeSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(150);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(150);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(150);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.SEND);
    }

    @Test
    public void rightHandInvalidRotationShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void leftHandInvalidRotationShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandWrongOrderGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void leftHandGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandInvalidAccelShouldFail() throws InterruptedException {
        detector.processAccelerationData(new float[]{6f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandInvalidGyroShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandInvalidGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(new float[]{-6f, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandInvalidAccelShouldFail4() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(new float[]{6f, 0, 0});

        verifyZeroInteractions(listener);
    }

    @Test
    public void leftHandInvalidAccelShouldFail() throws InterruptedException {
        detector.processAccelerationData(new float[]{-6f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandValidValuesWrongOrderShouldFail() throws InterruptedException {
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);

        verifyZeroInteractions(listener);
    }

    @Test
    public void leftHandValidValuesWrongOrderShouldFail() throws InterruptedException {
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightHandValidValuesWrongOrderShouldFail2() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);

        verifyZeroInteractions(listener);
    }

    @Test
    public void leftHandValidValuesWrongOrderShouldFail2() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void rightValuesWrongOrderShouldFail2() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);

        verifyZeroInteractions(listener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the left hand                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////
}