package com.ba.motiondetectionlib.detection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.detection.detectors.ReceiveMotionDetector;
import com.ba.motiondetectionlib.model.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveMotionDetectorTest {
    private ReceiveMotionDetector detector;
    private Intent intent;
    private Context context;

    private final float VALID_GRAVITY = 8f;
    private final float VALID_ACCEL = 15f;
    private final float VALID_GYRO_POSITIVE = 8f;
    private final float VALID_GYRO_NEGATIVE = -8f;

    private final float[] VALID_ACCEL_ARR = new float[]{0, VALID_ACCEL, 0};
    private final float[] VALID_GRAVITY_ARR = new float[]{0, VALID_GRAVITY, 0};
    private final float[] VALID_GYRO_ARR_POS = new float[]{0, VALID_GYRO_POSITIVE, 0};
    private final float[] VALID_GYRO_ARR_NEG = new float[]{0, VALID_GYRO_NEGATIVE, 0};

    private final float[] INVALID_GYRO_ARR_POSITIVE = new float[]{0, 6f, 0};
    private final float[] INVALID_GRAVITY_ARR = new float[]{0, 5f, 0};
    private final float[] INVALID_ACCEL_ARR = new float[]{0, 5f, 0};

    @Before
    public void setup() {
        intent = mock(Intent.class);
        context = mock(Context.class);
        detector = new ReceiveMotionDetector(context, intent, mock(MotionSensorSource.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the right hand                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void validValuesForClockwiseTurnSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validValuesForCounterClockwiseTurnSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_NEG);
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validValuesBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(120);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void multipleValidValuesShouldBeDetectedOnlyOnceGyroDifference() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validValuesShouldBeDetectedTwice() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 6f, 0});
        detector.processGyroData(new float[]{0, 7f, 0});
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 16f, 0});
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 26f, 0});
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(new float[]{0, 26f, 0});
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(intent, times(2)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(2)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(2)).sendBroadcast(intent);
    }

    @Test
    public void validValuesDiffOrderSuccess() throws InterruptedException {
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validValuesDifferentOrderSuccess() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.RECEIVE_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validValuesBorderlineTimeDifferenceFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(150);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(intent);
    }

    @Test
    public void validValuesDifferentOrderFailGravityDataTooLate() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR);

        verifyZeroInteractions(intent);
    }

    @Test
    public void validValuesExceptGyroShouldFail() throws InterruptedException {
        detector.processGravityData(INVALID_GRAVITY_ARR);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(intent);
    }

    @Test
    public void validValuesExceptGravityShouldFail() throws InterruptedException {
        detector.processGravityData(INVALID_GYRO_ARR_POSITIVE);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(intent);
    }

    @Test
    public void validValuesExceptAccelShouldFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processAccelerationData(INVALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the left hand                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////
}