package com.ba.motiondetectionlib.detection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.detection.detectors.ScoopMotionDetector;
import com.ba.motiondetectionlib.model.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScoopMotionDetectorTest {
    private ScoopMotionDetector detector;
    private Intent intent;
    private Context context;

    private final float[] VALID_GRAVITY_VALUES_POSITIVE = new float[]{0, 0, 8f};
    private final float[] VALID_GRAVITY_VALUES_NEGATIVE = new float[]{0, 0, -8f};
    private final float[] VALID_ACCEL_VALUES = new float[]{0, 0, 10f};

    @Before
    public void setup() {
        intent = mock(Intent.class);
        context = mock(Context.class);
        detector = new ScoopMotionDetector(context, intent, mock(MotionSensorSource.class));
    }

    @Test
    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(30);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_VALUES);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validSensorDataShouldBeDetectedTwiceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(30);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_VALUES);

        Thread.sleep(500);

        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(30);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_VALUES);

        verify(intent, times(2)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(2)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(2)).sendBroadcast(intent);
    }

    @Test
    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
        detector.processGravityData(new float[]{0, 0, -6.1f});
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, 6.1f});
        Thread.sleep(50);
        detector.processGravityData(new float[]{0, 0, 6.1f});
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, 6.1f});

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(350);

        detector.processAccelerationData(VALID_ACCEL_VALUES);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(450);

        detector.processAccelerationData(VALID_ACCEL_VALUES);
        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, 5.9f});
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processGravityData(new float[]{0, 0, 5.9f});
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail2() throws InterruptedException {
        detector.processGravityData(new float[]{0, 0, -5.9f});
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail3() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_POSITIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButOneMissingFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_VALUES_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_VALUES);
        Thread.sleep(50);

        verifyZeroInteractions(intent);
    }
}