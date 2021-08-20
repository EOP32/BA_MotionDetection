package com.ba.motiondetectionlib.detectors;

import com.ba.motiondetectionlib.model.MotionType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DropMotionDetectorTest {
    private DropMotionDetector detector;
    private DetectionSuccessCallback callback;

    private final float VALID_GRAVITY_POSITIVE = 8f;
    private final float VALID_GRAVITY_NEGATIVE = -8f;
    private final float VALID_ACCEL = 20f;

    @Before
    public void setup() {
        callback = mock(DetectionSuccessCallback.class);
        detector = new DropMotionDetector(callback);
    }

    @Test
    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);

        verify(callback, times(1)).onMotionDetected(MotionType.DROP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
        detector.processGravityData(7.1f);
        detector.processGravityData(1f);
        Thread.sleep(50);

        detector.processAccelerationData(-1.1f);
        detector.processAccelerationData(16.1f);
        detector.processAccelerationData(-12.1f);
        Thread.sleep(50);

        detector.processGravityData(6.1f);
        detector.processGravityData(2.1f);
        detector.processGravityData(-7.1f);
        Thread.sleep(50);

        detector.processAccelerationData(-1.1f);
        detector.processAccelerationData(-16.1f);
        detector.processAccelerationData(-12.1f);
        Thread.sleep(50);

        verify(callback, times(1)).onMotionDetected(MotionType.DROP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(200);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(200);
        detector.processAccelerationData(-VALID_ACCEL);

        verify(callback, times(1)).onMotionDetected(MotionType.DROP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(20);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(450);
        detector.processAccelerationData(-VALID_ACCEL);

        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-10f);

        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(10f);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);

        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(-5f);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_POSITIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(-VALID_ACCEL);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_POSITIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail3() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(-VALID_GRAVITY_NEGATIVE);

        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButOneMissingFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_NEGATIVE);
        Thread.sleep(50);

        verifyZeroInteractions(callback);
    }
}