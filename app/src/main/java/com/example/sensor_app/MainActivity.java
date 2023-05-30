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
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.sensor_app.DatabaseHelper;
import com.example.sensor_app.LightSensorChartActivity;
import com.example.sensor_app.R;

import java.util.List;

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

    private Handler handler;
    private static final long INTERVAL = 5 * 60 * 1000; // 5 minutes

    private DatabaseHelper dbHelper;

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


        // Set click listener for the "Proximity Sensor Chart" button
        Button proximitySensorChartButton = findViewById(R.id.proximitySensorButton);
        proximitySensorChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProximitySensorChartActivity.class));
            }
        });


        // Set click listener for the "Accelerometer Sensor Chart" button
        Button accelerometerSensorChartButton = findViewById(R.id.accelerometerSensorButton);
        accelerometerSensorChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccelerometerSensorChartActivity.class));
            }
        });


        // Set click listener for the "Gyroscope Sensor Chart" button
        Button gyroscopeSensorChartButton = findViewById(R.id.gyroscopeSensorButton);
        gyroscopeSensorChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GyroscopeSensorChartActivity.class));
            }
        });

        // Create instance of DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set up handler for periodic database operations
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Perform database operations here
                recordSensorData();
                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
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
            showNotification("Light Sensor Value: " + value);
        } else if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximitySensorValue.setText("Proximity Sensor Value: " + value);
        } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerSensorValue.setText("Accelerometer Sensor Value: " + value);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeSensorValue.setText("Gyroscope Sensor Value: " + value);
        }
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

    private void recordSensorData() {
        // Retrieve sensor values from UI or other sources
        float lightValue = Float.parseFloat(lightSensorValue.getText().toString().substring(19));
        float proximityValue = Float.parseFloat(proximitySensorValue.getText().toString().substring(24));
        float accelerometerValue = Float.parseFloat(accelerometerSensorValue.getText().toString().substring(29));
        float gyroscopeValue = Float.parseFloat(gyroscopeSensorValue.getText().toString().substring(25));

        // Create SensorData objects with timestamp and sensor values
        long timestamp = System.currentTimeMillis();
        DatabaseHelper.SensorData lightSensorData = new DatabaseHelper.SensorData(timestamp, lightValue);
        DatabaseHelper.SensorData proximitySensorData = new DatabaseHelper.SensorData(timestamp, proximityValue);
        DatabaseHelper.SensorData accelerometerSensorData = new DatabaseHelper.SensorData(timestamp, accelerometerValue);
        DatabaseHelper.SensorData gyroscopeSensorData = new DatabaseHelper.SensorData(timestamp, gyroscopeValue);

        // Insert sensor values into the database
        dbHelper.insertLightSensorValue(lightSensorData);
        dbHelper.insertProximitySensorValue(proximitySensorData);
        dbHelper.insertAccelerometerSensorValue(accelerometerSensorData);
        dbHelper.insertGyroscopeSensorValue(gyroscopeSensorData);
    }
}
