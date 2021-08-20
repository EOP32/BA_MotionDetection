package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveMotionDetectorTest {
    private ReceiveMotionDetector detector;
    private DetectionSuccessCallback callback;

    private final float VALID_GRAVITY = 8f;
    private final float VALID_ACCEL = 15f;
    private final float VALID_GYRO_POSITIVE = 8f;
    private final float VALID_GYRO_NEGATIVE = -8f;

    @Before
    public void setup() {
        callback = mock(DetectionSuccessCallback.class);
        detector = new ReceiveMotionDetector(callback);
    }

    @Test
    public void validValuesForClockwiseTurnSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_POSITIVE);

        verify(callback, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validValuesForCounterClockwiseTurnSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_NEGATIVE);
        Thread.sleep(10);
        detector.processAccelerationData(VALID_ACCEL);

        verify(callback, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validValuesBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(120);
        detector.processGyroData(VALID_GYRO_POSITIVE);

        verify(callback, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void multipleValidValuesShouldBeDetectedOnlyOnceGyroDifference() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        Thread.sleep(10);

        detector.processGyroData(6f);
        detector.processGyroData(7f);
        detector.processGyroData(VALID_GYRO_POSITIVE);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(10);

        detector.processGyroData(20f);
        detector.processAccelerationData(VALID_ACCEL);

        verify(callback, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validValuesShouldBeDetectedTwice() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        Thread.sleep(10);

        detector.processGyroData(6f);
        detector.processGyroData(7f);
        detector.processGyroData(VALID_GYRO_POSITIVE);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(10);

        detector.processGyroData(20f);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(10);

        detector.processGravityData(VALID_GRAVITY);
        detector.processGyroData(20f);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(10);

        detector.processGyroData(30f);
        detector.processAccelerationData(VALID_ACCEL);

        verify(callback, times(2)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validValuesDiffOrderSuccess() throws InterruptedException {
        detector.processGyroData(VALID_GYRO_POSITIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);

        verify(callback, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validValuesDifferentOrderSuccess() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_POSITIVE);

        verify(callback, times(1)).onMotionDetected(MotionType.RECEIVE);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validValuesBorderlineTimeDifferenceFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(150);
        detector.processGyroData(VALID_GYRO_POSITIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void validValuesDifferentOrderFailGravityDataTooLate() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGyroData(VALID_GYRO_POSITIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY);

        verifyZeroInteractions(callback);
    }

    @Test
    public void validValuesExceptGyroShouldFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(10);
        detector.processGyroData(6f);

        verifyZeroInteractions(callback);
    }

    @Test
    public void validValuesExceptGravityShouldFail() throws InterruptedException {
        detector.processGravityData(0f);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_NEGATIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void validValuesExceptAccelShouldFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY);
        detector.processAccelerationData(4f);
        Thread.sleep(10);
        detector.processGyroData(VALID_GYRO_NEGATIVE);

        verifyZeroInteractions(callback);
    }
}