package com.ba.motiondetectionlib.detection;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.ba.motiondetectionlib.detection.detectors.DropMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.ReceiveMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.ScoopMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.SendMotionDetector;
import com.ba.motiondetectionlib.detection.detectors.SensorDataListener;

import java.util.ArrayList;
import java.util.List;

public class DetectionManager implements IDetectionManager, MotionSensorSource {

    private List<SensorDataListener> sensorDataListenerList;
    private static DetectionManager instance = null;

    private DetectionManager() {
    }

    public static DetectionManager getInstance() {
        if (instance == null) {
            return new DetectionManager();
        }
        return instance;
    }

    @Override
    public void startDetectors(Context context) {
        sensorDataListenerList = new ArrayList<>();
        new SendMotionDetector(context, new Intent(), this);
        new ReceiveMotionDetector(context, new Intent(), this);
        new DropMotionDetector(context, new Intent(), this);
        new ScoopMotionDetector(context, new Intent(), this);
    }

    @Override
    public void forwardSensorData(SensorEvent event) {
        for (SensorDataListener listener : sensorDataListenerList) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    listener.processAccelerationData(event.values);
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
