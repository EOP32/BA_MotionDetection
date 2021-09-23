package com.ba.motiondetectionlib.service;

import static com.ba.motiondetectionlib.model.Constants.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import com.ba.motiondetectionlib.detection.DetectionManager;

public class MotionDetectionService extends Service implements SensorEventListener {

    private final String CHANNEL_ID = "Motion_Detection_Service";

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;
    private Sensor accelerationSensor;
    private Sensor gravitySensor;
    private Sensor gyroscopeSensor;

    private DetectionManager detectionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDetectors();
        initializeAndRegisterSensors();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());
        Log.d(TAG, "Service started.");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterSensors();
        detectionManager.removeSensorDataListeners();
        stopSelf();
        stopForeground(true);

        Log.d(TAG, "Service stopped.");
    }

    private void initializeAndRegisterSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (linearAccelerationSensor != null && gravitySensor != null && gyroscopeSensor != null && accelerationSensor != null) {
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d(TAG, "All sensors successfully registered.");
        } else {
            Log.d(TAG, "One or multiple sensors are not available on this device. No sensors delivered to sensor manager.");
        }
    }

    private void unregisterSensors() {
        sensorManager.unregisterListener(this, linearAccelerationSensor);
        sensorManager.unregisterListener(this, accelerationSensor);
        sensorManager.unregisterListener(this, gravitySensor);
        sensorManager.unregisterListener(this, gyroscopeSensor);
        Log.d(TAG, "All sensors shut down.");
    }

    private void initializeDetectors() {
        detectionManager = new DetectionManager(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        detectionManager.forwardSensorData(event);
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
        String packageName = this.getPackageName();
        Intent intentActivity = this.getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intentActivity, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
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
