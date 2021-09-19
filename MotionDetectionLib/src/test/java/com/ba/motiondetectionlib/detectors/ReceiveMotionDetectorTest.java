package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveMotionDetectorTest {
    private ReceiveMotionDetector detector;
    private MotionDetectionListener listener;

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
        listener = mock(MotionDetectionListener.class);
        detector = new ReceiveMotionDetector(mock(Context.class), mock(MotionSensorSource.class));
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

        verify(listener, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void validValuesForCounterClockwiseTurnSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_NEG);
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void validValuesBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(120);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verify(listener, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void multipleValidValuesShouldBeDetectedOnlyOnceGyroDifference() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);

        detector.processGyroData(INVALID_GYRO_ARR_POSITIVE);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);

        detector.processGyroData(VALID_GYRO_ARR_POS);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void validValuesShouldBeDetectedTwice() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(10);

        detector.processGyroData(INVALID_GYRO_ARR_POSITIVE);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);

        detector.processGyroData(VALID_GYRO_ARR_POS);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);

        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);

        detector.processGyroData(VALID_GYRO_ARR_POS);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(listener, times(2)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void validValuesDiffOrderSuccess() throws InterruptedException {
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR);

        verify(listener, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void validValuesDifferentOrderSuccess() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verify(listener, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void validValuesBorderlineTimeDifferenceFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(150);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(listener);
    }

    @Test
    public void validValuesDifferentOrderFailGravityDataTooLate() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR);

        verifyZeroInteractions(listener);
    }

    @Test
    public void validValuesExceptGyroShouldFail() throws InterruptedException {
        detector.processGravityData(INVALID_GRAVITY_ARR);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(listener);
    }

    @Test
    public void validValuesExceptGravityShouldFail() throws InterruptedException {
        detector.processGravityData(INVALID_GYRO_ARR_POSITIVE);
        detector.processAccelerationData(VALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(listener);
    }

    @Test
    public void validValuesExceptAccelShouldFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR);
        detector.processAccelerationData(INVALID_ACCEL_ARR);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_ARR_POS);

        verifyZeroInteractions(listener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the left hand                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////
}