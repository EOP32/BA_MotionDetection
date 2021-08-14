package com.ba.motiondetectionlib.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ba.motiondetectionlib.R;
import com.ba.motiondetectionlib.detectors.DetectionSuccessCallback;
import com.ba.motiondetectionlib.detectors.DropMotionDetector;
import com.ba.motiondetectionlib.detectors.ReceiveMotionDetector;
import com.ba.motiondetectionlib.detectors.ScoopMotionDetector;
import com.ba.motiondetectionlib.detectors.SendMotionDetector;
import com.ba.motiondetectionlib.model.Constants;
import com.ba.motiondetectionlib.model.MotionType;

import java.util.List;

public class MotionDetectionService extends Service implements DetectionSuccessCallback, SensorEventListener {

    private final String CHANNEL_ID = "Motion_Detection_Service";
    private Context context;

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;
    private Sensor accelerationSensor;
    private Sensor gravitySensor;
    private Sensor gyroscopeSensor;

    private SendMotionDetector sendDetector;
    private ReceiveMotionDetector receiveDetector;
    private DropMotionDetector dropDetector;
    private ScoopMotionDetector scoopDetector;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = ServiceController.context;
        initializeAndRegisterSensors();
        initializeDetectors();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());
        Log.d(Constants.TAG, "Service started.");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterSensors();
        stopSelf();
        stopForeground(true);
        Log.d(Constants.TAG, "Service stopped.");
    }

    private void initializeAndRegisterSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (linearAccelerationSensor != null && gravitySensor != null && gyroscopeSensor != null && accelerationSensor != null) {
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(Constants.TAG, "All sensors successfully registered.");
        } else {
            Log.d(Constants.TAG, "One or multiple sensors are not available on this device. No sensors delivered to sensor manager.");
        }
    }

    private void unregisterSensors() {
        sensorManager.unregisterListener(this, linearAccelerationSensor);
        sensorManager.unregisterListener(this, accelerationSensor);
        sensorManager.unregisterListener(this, gravitySensor);
        sensorManager.unregisterListener(this, gyroscopeSensor);
        Log.d(Constants.TAG, "Shut down sensors.");
    }

    private void initializeDetectors() {
        sendDetector = new SendMotionDetector(this);
        receiveDetector = new ReceiveMotionDetector(this);
        dropDetector = new DropMotionDetector(this);
        scoopDetector = new ScoopMotionDetector(this);
    }

    private void sendBroadcast(String motion) {
        Intent intent = new Intent(Constants.INTENT_IDENTIFIER);
        intent.putExtra(Constants.STRING_EXTRA_IDENTIFIER, motion);
        sendBroadcast(intent);
        Log.d(Constants.TAG, "Sending motion detection broadcast finished.");
    }

    @Override
    public void onMotionDetected(MotionType type) {
        switch (type) {
            case SEND:
                sendBroadcast(Constants.SEND_MOTION);
                break;
            case RECEIVE:
                sendBroadcast(Constants.RECEIVE_MOTION);
                break;
            case DROP:
                sendBroadcast(Constants.DROP_MOTION);
                break;
            case SCOOP:
                sendBroadcast(Constants.SCOOP_MOTION);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                receiveDetector.processAccelerationData(event.values[1]);
                dropDetector.processAccelerationData(event.values[2]);
                scoopDetector.processAccelerationData(event.values[2]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sendDetector.processAccelerationData(event.values[0]);
                break;
            case Sensor.TYPE_GRAVITY:
                dropDetector.processGravityData(event.values[2]);
                receiveDetector.processGravityData(event.values[1]);
                scoopDetector.processGravityData(event.values[2]);
                sendDetector.processGravityData(event.values[0]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                receiveDetector.processGyroData(event.values[1]);
                break;
            default:
                break;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Motion Detection",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            getSystemService(NotificationManager.class).createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        String packageName = context.getPackageName();
        Intent intentActivity = context.getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intentActivity, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Motion Detection")
                .setContentText("Started detecting motions.")
                .setSmallIcon(R.drawable.ic_loop)
                .setContentIntent(contentIntent)
                .build();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
