package com.ba.motiondetectionlib.detection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.content.Context;
import android.content.Intent;

import com.ba.motiondetectionlib.detection.detectors.DropMotionDetector;
import com.ba.motiondetectionlib.model.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DropMotionDetectorTest {
    private DropMotionDetector detector;
    private Intent intent;
    private Context context;

    private final float VALID_GRAVITY_POSITIVE = 8f;
    private final float VALID_ACCEL = 20f;

    private final float[] VALID_GRAVITY_ARR_POS = new float[]{0, 0, VALID_GRAVITY_POSITIVE};
    private final float[] VALID_GRAVITY_ARR_NEG = new float[]{0, 0, -VALID_GRAVITY_POSITIVE};
    private final float[] VALID_ACCEL_ARR_POS = new float[]{0, 0, VALID_ACCEL};
    private final float[] VALID_ACCEL_ARR_NEG = new float[]{0, 0, -VALID_ACCEL};

    private final float[] INVALID_GRAVITY_ARR_POS = new float[]{0, 0, 3f};
    private final float[] INVALID_GRAVITY_ARR_NEG = new float[]{0, 0, -3f};
    private final float[] INVALID_ACCEL_ARR_POS = new float[]{0, 0, 3f};
    private final float[] INVALID_ACCEL_ARR_NEG = new float[]{0, 0, -3f};

    @Before
    public void setup() {
        intent = mock(Intent.class);
        context = mock(Context.class);
        detector = new DropMotionDetector(context, intent, mock(MotionSensorSource.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the right hand                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.DROP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
        detector.processGravityData(new float[]{0, 0, 7.1f});
        detector.processGravityData(INVALID_GRAVITY_ARR_POS);
        Thread.sleep(50);

        detector.processAccelerationData(INVALID_ACCEL_ARR_POS);
        detector.processAccelerationData(new float[]{0, 0, 16.1f});
        Thread.sleep(50);

        detector.processGravityData(INVALID_GRAVITY_ARR_POS);
        detector.processGravityData(new float[]{0, 0, -7.1f});
        Thread.sleep(50);

        detector.processAccelerationData(INVALID_ACCEL_ARR_NEG);
        detector.processAccelerationData(new float[]{0, 0, -16.1f});
        Thread.sleep(50);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.DROP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(200);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(200);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);

        verify(intent, times(1)).setAction(Constants.INTENT_IDENTIFIER);
        verify(intent, times(1)).putExtra(Constants.STRING_EXTRA_IDENTIFIER, Constants.DROP_MOTION);
        verify(context, times(1)).sendBroadcast(intent);
    }

    @Test
    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(20);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(450);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(INVALID_ACCEL_ARR_NEG);

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidAccelerationSensorValuesFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(INVALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);

        verifyZeroInteractions(intent);
    }

    @Test
    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(INVALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail() throws InterruptedException {
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_POS);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButWrongOrderFail2() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_POS);

        verifyZeroInteractions(intent);
    }

    @Test
    public void correctValuesButOneMissingFail() throws InterruptedException {
        detector.processGravityData(VALID_GRAVITY_ARR_POS);
        Thread.sleep(50);
        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
        Thread.sleep(50);
        detector.processGravityData(VALID_GRAVITY_ARR_NEG);
        Thread.sleep(50);

        verifyZeroInteractions(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                   tests for motions executed with the left hand                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////
}