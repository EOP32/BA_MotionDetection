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

    private final float VALID_GRAVITY_POSITIVE = 8f;
    private final float VALID_ACCEL = 20f;

    private final float[] VALID_GRAVITY_ARR_POSITIVE = new float[]{0, 0, VALID_GRAVITY_POSITIVE};
    private final float[] VALID_GRAVITY_ARR_NEGATIVE = new float[]{0, 0, -VALID_GRAVITY_POSITIVE};
    private final float[] VALID_ACCEL_ARR_POS = new float[]{0, 0, VALID_ACCEL};
    private final float[] VALID_ACCEL_ARR_NEG = new float[]{0, 0, -VALID_ACCEL};

    @Before
    public void setup() {
        intent = mock(Intent.class);
        context = mock(Context.class);
        detector = new ScoopMotionDetector(context, intent, mock(MotionSensorSource.class));
    }

    @Test
    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(30);
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
        Thread.sleep(30);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validSensorDataShouldBeDetectedTwiceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(30);
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);

        Thread.sleep(500);

        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(30);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(30);
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);

        verify(intent, times(2)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(2)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(2)).sendBroadcast(intent);
    }

    @Test
    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
        detector.processGravityData(new float[]{0, 0, -7.1f});
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, -12.1f});
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, 16.1f});
        Thread.sleep(50);
        detector.processGravityData(new float[]{0, 0, 7.1f});

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(350);

        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.SCOOP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(450);

        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, -12f});
        Thread.sleep(50);
        detector.processAccelerationData(new float[]{0, 0, 12f});
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processGravityData(new float[]{0, 0, 5f});

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail2() throws InterruptedException {
        detector.processGravityData(new float[]{0, 0, -5f});
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail3() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButOneMissingFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);

        verifyZeroInteractions(intent);
    }
}