package com.example.sensor_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.sensor_app.R;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;

    private static final String CHANNEL_ID = "sensor_service_notifications";
    private static final int NOTIFICATION_ID = 2;

    private Handler handler;
    private Runnable runnable;
    private static final long INTERVAL = 5000; // 5 sec

    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize sensor manager and sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Set up notification channel
        createNotificationChannel();

        // Set up handler and runnable for periodic sensor reading
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                readSensorValues();
                handler.postDelayed(this, INTERVAL);
            }
        };

        // Create instance of DatabaseHelper
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start reading sensor values periodically
        handler.postDelayed(runnable, INTERVAL);

        // Show a notification to let the user know the service is running
        showNotification();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop reading sensor values
        handler.removeCallbacks(runnable);

        // Remove the notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Check sensor type and record values in SQLite DB
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightValue = event.values[0];
            long timestamp = System.currentTimeMillis();
            dbHelper.insertLightSensorValue(timestamp, lightValue);
        } else if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // Record proximity sensor values
        } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Record accelerometer sensor values
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Record gyroscope sensor values
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void readSensorValues() {
        // Register sensor listeners for all sensors
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Unregister sensor listeners after a short delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sensorManager.unregisterListener(SensorService.this);
            }
        }, 1000); // 1 second delay
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sensor Service Notifications";
            String description = "Shows sensor values in the background";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sensor Service")
                .setContentText("App is running in the background")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
