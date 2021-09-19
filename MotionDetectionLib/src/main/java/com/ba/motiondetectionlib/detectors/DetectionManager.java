package com.ba.motiondetectionlib.detectors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

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
        new SendMotionDetector(context, sensorSource);
        new ReceiveMotionDetector(context, sensorSource);
        new DropMotionDetector(context, sensorSource);
        new ScoopMotionDetector(context, sensorSource);
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
