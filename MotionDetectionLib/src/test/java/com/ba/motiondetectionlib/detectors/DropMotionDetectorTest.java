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

    @Before
    public void setup() {
        callback = mock(DetectionSuccessCallback.class);
        detector = new DropMotionDetector(callback);
    }

    @Test
    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(10);
        detector.processGravityData(0);
        detector.processGravityData(3);
        Thread.sleep(50);

        detector.processAccelerationData(0);
        detector.processAccelerationData(10);
        detector.processAccelerationData(-20);
        Thread.sleep(50);

        detector.processGravityData(-10);
        detector.processGravityData(-2);
        detector.processGravityData(-1);

        verify(callback, times(1)).onMotionDetected(MotionType.DROP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
        detector.processGravityData(7.1f);
        detector.processGravityData(1f);
        detector.processGravityData(-7.1f);
        Thread.sleep(50);

        detector.processAccelerationData(-1.1f);
        detector.processAccelerationData(13.1f);
        detector.processAccelerationData(-15.1f);
        Thread.sleep(50);

        detector.processGravityData(-7.1f);
        detector.processGravityData(7.1f);
        detector.processGravityData(17.1f);

        verify(callback, times(1)).onMotionDetected(MotionType.DROP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(7.1f);
        detector.processGravityData(1f);
        Thread.sleep(50);

        detector.processAccelerationData(13.1f);
        detector.processAccelerationData(-15.1f);
        Thread.sleep(400);

        detector.processGravityData(-7.1f);
        detector.processGravityData(7.1f);

        verify(callback, times(1)).onMotionDetected(MotionType.DROP);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
        detector.processAccelerationData(13.1f);
        detector.processAccelerationData(-15.1f);
        Thread.sleep(450);

        detector.processGravityData(7.1f);
        detector.processGravityData(1f);
        Thread.sleep(50);

        detector.processGravityData(-7.1f);
        detector.processGravityData(7.1f);

        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
        detector.processAccelerationData(-15f);
        Thread.sleep(50);

        detector.processGravityData(7.1f);
        Thread.sleep(50);

        detector.processGravityData(-7.1f);
        verifyZeroInteractions(callback);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
        detector.processAccelerationData(-16f);
        Thread.sleep(50);

        detector.processGravityData(7.1f);
        Thread.sleep(50);

        detector.processGravityData(-5f);
        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail() throws InterruptedException {
        detector.processAccelerationData(-15f);
        Thread.sleep(50);

        detector.processGravityData(-10f);
        Thread.sleep(50);

        detector.processGravityData(17f);
        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail2() throws InterruptedException {
        detector.processGravityData(-10f);
        Thread.sleep(50);

        detector.processAccelerationData(-15f);
        Thread.sleep(50);

        detector.processGravityData(17f);
        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButWrongOrderFail3() throws InterruptedException {
        detector.processGravityData(10f);
        Thread.sleep(50);

        detector.processAccelerationData(-15f);
        Thread.sleep(50);

        detector.processGravityData(-17f);
        verifyZeroInteractions(callback);
    }

    @Test
    public void correctValuesButOneMissingFail() throws InterruptedException {
        detector.processAccelerationData(-15f);
        Thread.sleep(50);

        detector.processGravityData(-10f);
        Thread.sleep(50);
    }
}