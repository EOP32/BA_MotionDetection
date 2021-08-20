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
public class ScoopMotionDetectorTest {
    private ScoopMotionDetector detector;
    private DetectionSuccessCallback callback;

    private final float VALID_GRAVITY_POSITIVE = 8f;
    private final float VALID_GRAVITY_NEGATIVE = -8f;
    private final float VALID_ACCEL = 20f;

    @Before
    public void setup() {
        callback = mock(DetectionSuccessCallback.class);
        detector = new ScoopMotionDetector(callback);
    }

    @Test
    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(30);

        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(30);

        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(30);

        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(30);

        verify(callback, times(1)).onMotionDetected(MotionType.SCOOP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
        detector.processGravityData(-7.1f);
        detector.processGravityData(1f);
        Thread.sleep(50);

        detector.processAccelerationData(-1.1f);
        detector.processAccelerationData(-12.1f);
        Thread.sleep(50);

        detector.processAccelerationData(16.1f);
        Thread.sleep(50);

        detector.processGravityData(7.1f);
        detector.processGravityData(1f);

        verify(callback, times(1)).onMotionDetected(MotionType.SCOOP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);

        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(350);

        detector.processGravityData(VALID_GRAVITY_POSITIVE);

        verify(callback, times(1)).onMotionDetected(MotionType.SCOOP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(450);

        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-12);
        Thread.sleep(50);
        detector.processAccelerationData(12);
        Thread.sleep(50);

        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(5f);

        detector.processGravityData(-5f);
        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail2() throws InterruptedException {
        detector.processGravityData(-5f);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_POSITIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail() throws InterruptedException {
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_POSITIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail3() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButOneMissingFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);

        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
    }
}