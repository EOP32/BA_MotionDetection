//package com.ba.motiondetectionlib.detection;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.verifyZeroInteractions;
//
//import android.content.Context;
//
//import com.ba.motiondetectionlib.detection.detectors.ScoopMotionDetector;
//import com.ba.motiondetectionlib.model.MotionType;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.junit.MockitoJUnitRunner;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ScoopMotionDetectorTest {
//    private ScoopMotionDetector detector;
//    private MotionDetectionListener listener;
//
//    private final float VALID_GRAVITY_POSITIVE = 8f;
//    private final float VALID_ACCEL = 20f;
//
//    private final float[] VALID_GRAVITY_ARR_POSITIVE = new float[]{0, 0, VALID_GRAVITY_POSITIVE};
//    private final float[] VALID_GRAVITY_ARR_NEGATIVE = new float[]{0, 0, -VALID_GRAVITY_POSITIVE};
//    private final float[] VALID_ACCEL_ARR_POS = new float[]{0, 0, VALID_ACCEL};
//    private final float[] VALID_ACCEL_ARR_NEG = new float[]{0, 0, -VALID_ACCEL};
//
//    @Before
//    public void setup() {
//        listener = mock(MotionDetectionListener.class);
//        detector = new ScoopMotionDetector(mock(Context.class), mock(MotionSensorSource.class));
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    //                   tests for motions executed with the right hand                           //
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    @Test
//    public void validSensorDataAndValidTimeDifferenceSuccess() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(30);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(30);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(30);
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//        Thread.sleep(30);
//
//        verify(listener, times(1)).onMotionDetected(MotionType.SCOOP);
//        verifyNoMoreInteractions(listener);
//    }
//
//    @Test
//    public void validButBorderlineSensorValuesSuccess() throws InterruptedException {
//        detector.processGravityData(new float[]{0, 0, -7.1f});
//        Thread.sleep(50);
//        detector.processAccelerationData(new float[]{0, 0, -12.1f});
//        Thread.sleep(50);
//        detector.processAccelerationData(new float[]{0, 0, 16.1f});
//        Thread.sleep(50);
//        detector.processGravityData(new float[]{0, 0, 7.1f});
//
//        verify(listener, times(1)).onMotionDetected(MotionType.SCOOP);
//        verifyNoMoreInteractions(listener);
//    }
//
//    @Test
//    public void validButBorderlineTimeDifferenceSuccess() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(350);
//
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//
//        verify(listener, times(1)).onMotionDetected(MotionType.SCOOP);
//        verifyNoMoreInteractions(listener);
//    }
//
//    @Test
//    public void borderlineButInvalidTimeValuesFail() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(450);
//
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void borderlineButInvalidAccelerationSensorValuesFail() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(new float[]{0, 0, -12f});
//        Thread.sleep(50);
//        detector.processAccelerationData(new float[]{0, 0, 12f});
//        Thread.sleep(50);
//
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void borderlineButInvalidGravitySensorValuesFail() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processGravityData(new float[]{0, 0, 5f});
//
//        detector.processGravityData(new float[]{0, 0, -5f});
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void borderlineButInvalidGravitySensorValuesFail2() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(50);
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void correctValuesButWrongOrderFail() throws InterruptedException {
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(50);
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void correctValuesButWrongOrderFail2() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(50);
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void correctValuesButWrongOrderFail3() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_POSITIVE);
//        Thread.sleep(50);
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(50);
//
//        verifyZeroInteractions(listener);
//    }
//
//    @Test
//    public void correctValuesButOneMissingFail() throws InterruptedException {
//        detector.processGravityData(VALID_GRAVITY_ARR_NEGATIVE);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_NEG);
//        Thread.sleep(50);
//        detector.processAccelerationData(VALID_ACCEL_ARR_POS);
//        Thread.sleep(50);
//
//        verifyZeroInteractions(listener);
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    //                   tests for motions executed with the left hand                            //
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//}