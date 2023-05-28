package com.example.sensor_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private TextView lightSensorValue;
    private TextView proximitySensorValue;
    private TextView accelerometerSensorValue;
    private TextView gyroscopeSensorValue;

    private static final String CHANNEL_ID = "sensor_notifications";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize sensor manager and sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Initialize UI elements
        lightSensorValue = findViewById(R.id.lightSensorValue);
        proximitySensorValue = findViewById(R.id.proximitySensorValue);
        accelerometerSensorValue = findViewById(R.id.accelerometerSensorValue);
        gyroscopeSensorValue = findViewById(R.id.gyroscopeSensorValue);

        // Set up notification channel
        createNotificationChannel();

        // Set click listener for the "Light Sensor Chart" button
        Button lightSensorChartButton = findViewById(R.id.lightSensorButton);
        lightSensorChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LightSensorChartActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register sensor listeners
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister sensor listeners
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Update UI based on sensor values
        Sensor sensor = event.sensor;
        float value = event.values[0];

        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            lightSensorValue.setText("Light Sensor Value: " + value);
        } else if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximitySensorValue.setText("Proximity Sensor Value: " + value);
        } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerSensorValue.setText("Accelerometer Sensor Value: " + value);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeSensorValue.setText("Gyroscope Sensor Value: " + value);
        }

        // Record sensor values in SQLite DB every 5 minutes
        // Implement database code here
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sensor Notifications";
            String description = "Shows sensor values in the background";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String contentText) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sensor Values")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
