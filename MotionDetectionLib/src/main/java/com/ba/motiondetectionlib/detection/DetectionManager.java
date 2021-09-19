package com.ba.motiondetectionlib.detection;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.ba.motiondetectionlib.detection.detectors.DropMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.ReceiveMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.ScoopMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.SendMotionDetector;

import java.util.ArrayList;
import java.util.List;

public class DetectionManager implements IDetectionManager, MotionSensorSource {

    private List<SensorDataListener> sensorDataListenerList;

    public DetectionManager(Context context) {
        sensorDataListenerList = new ArrayList<>();
        startDetectors(context, this);
    }

    @Override
    public void startDetectors(Context context, MotionSensorSource sensorSource) {
        new SendMotionDetector(context, new Intent(), sensorSource);
        new ReceiveMotionDetector(context, new Intent(), sensorSource);
        new DropMotionDetector(context, new Intent(), sensorSource);
        new ScoopMotionDetector(context, new Intent(), sensorSource);
    }

    @Override
    public void forwardSensorData(SensorEvent event) {
        for (SensorDataListener listener : sensorDataListenerList) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    listener.processAccelerationData(event.values);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    listener.processLinearAccelerationData(event.values);
                    break;
                case Sensor.TYPE_GRAVITY:
                    listener.processGravityData(event.values);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    listener.processGyroData(event.values);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void addSensorDataListener(SensorDataListener newListener) {
        this.sensorDataListenerList.add(newListener);
    }

    @Override
    public void removeSensorDataListeners() {
        this.sensorDataListenerList = null;
    }
}
