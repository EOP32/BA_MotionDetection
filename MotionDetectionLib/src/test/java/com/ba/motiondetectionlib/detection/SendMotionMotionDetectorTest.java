package com.ba.motiondetectionlib.detection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.detection.detectors.SendDetector;
import com.ba.motiondetectionlib.model.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SendMotionMotionDetectorTest {
    private SendDetector detector;
    private Intent intent;
    private Context context;

    private final float MOTION_RIGHT_VALID = 16f;
    private final float MOTION_LEFT_VALID = -16f;
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
        intent = mock(Intent.class);
        context = mock(Context.class);
        detector = new SendDetector(context, intent, mock(MotionSensorSource.class));
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

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validBorderlineValuesRightHandSuccess() throws InterruptedException {
        detector.processAccelerationData(new float[]{15.1f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, -5.1f, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{-7.1f, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{7.1f, 0, 0});

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validRightHandBorderlineTimeSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(150);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(100);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(100);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void rightHandShouldBeDetectedTwiceSuccess() throws InterruptedException {
        // valid order for right handed send motion
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        Thread.sleep(500);

        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verify(intent, times(2)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(2)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(2)).sendBroadcast(intent);
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

        verifyZeroInteractions(intent);
    }

    @Test
    public void rightHandWrongOrderGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
    }

    @Test
    public void validValuesRightHandOneMissingFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_RIGHT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);

        verifyZeroInteractions(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the left hand                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

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

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validBorderlineValuesLeftHandSuccess() throws InterruptedException {
        detector.processAccelerationData(new float[]{-15.1f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 5.1f, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{7.1f, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(new float[]{-7.1f, 0, 0});

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validLeftHandBorderlineTimeSuccess() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(100);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(150);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(150);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
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

        verifyZeroInteractions(intent);
    }

    @Test
    public void leftHandWrongOrderGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);

        verifyZeroInteractions(intent);
    }

    @Test
    public void leftHandInvalidAccelShouldFail() throws InterruptedException {
        detector.processAccelerationData(new float[]{-6f, 0, 0});
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verifyZeroInteractions(intent);
    }

    @Test
    public void leftHandInvalidGyroShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 0, 0});
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verifyZeroInteractions(intent);
    }

    @Test
    public void leftHandInvalidGravityShouldFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(new float[]{-6f, 0, 0});

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
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

        verifyZeroInteractions(intent);
    }

    @Test
    public void validValuesLeftHandOneMissingFail() throws InterruptedException {
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);
        Thread.sleep(10);

        verifyZeroInteractions(intent);
    }

    @Test
    public void leftHandShouldBeDetectedTwiceSuccess() throws InterruptedException {
        // valid order for right handed send motion
        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        Thread.sleep(500);

        detector.processAccelerationData(MOTION_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGyroData(ROTATE_LEFT_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_FORTH_VALID_ARR);
        Thread.sleep(10);
        detector.processGravityData(GRAVITY_BACK_VALID_ARR);

        verify(intent, times(2)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(2)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SEND_MOTION);
        verify(context, times(2)).sendBroadcast(intent);
    }
}