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
    private static final long INTERVAL = 5000; // 5 sec

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, SensorService.class);
        startService(serviceIntent);

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
    protected void onDestroy() {
        Intent serviceIntent = new Intent(this, SensorService.class);
        stopService(serviceIntent);

        super.onDestroy();
        stopService(serviceIntent);
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

        // Get sensor values as strings
        String lightValueText = lightSensorValue.getText().toString().substring(19);
        String proximityValueText = proximitySensorValue.getText().toString().substring(24);
        String accelerometerValueText = accelerometerSensorValue.getText().toString().length() >= 29
                ? accelerometerSensorValue.getText().toString().substring(29)
                : "N/A";
        String gyroscopeValueText = gyroscopeSensorValue.getText().toString().length() >= 25
                ? gyroscopeSensorValue.getText().toString().substring(25)
                : "N/A";

        // Parse sensor values to float with error handling
        float lightValue = 0.0f;
        float proximityValue = 0.0f;
        float accelerometerValue = 0.0f;
        float gyroscopeValue = 0.0f;

        try {
            if (lightValueText.length() >= 19) {
                lightValue = Float.parseFloat(lightValueText);
            }

            if (proximityValueText.length() >= 24) {
                proximityValue = Float.parseFloat(proximityValueText);
            }

            if (!accelerometerValueText.equals("N/A")) {
                accelerometerValue = Float.parseFloat(accelerometerValueText);
            }

            if (!gyroscopeValueText.equals("N/A")) {
                gyroscopeValue = Float.parseFloat(gyroscopeValueText);
            }
        } catch (NumberFormatException e) {
            // Handle the parsing error here
            e.printStackTrace();
            // Set default values
            lightValue = 0.0f;
            proximityValue = 0.0f;
            accelerometerValue = 0.0f;
            gyroscopeValue = 0.0f;
        }

        String[] sensorValues = { String.valueOf(lightValue), String.valueOf(proximityValue),
                String.valueOf(accelerometerValue), String.valueOf(gyroscopeValue) };

        showNotification(sensorValues);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sensor Notifications";
            String description = "Shows sensor values in the background";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String[] sensorValues) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        for (int i = 0; i < sensorValues.length; i++) {
            String sensorName = "";
            String contentText = "";

            switch (i) {
                case 0:
                    sensorName = "Light Sensor";
                    contentText = "Light Sensor Value: " + sensorValues[i];
                    break;
                case 1:
                    sensorName = "Proximity Sensor";
                    contentText = "Proximity Sensor Value: " + sensorValues[i];
                    break;
                case 2:
                    sensorName = "Accelerometer Sensor";
                    contentText = "Accelerometer Sensor Value: " + sensorValues[i];
                    break;
                case 3:
                    sensorName = "Gyroscope Sensor";
                    contentText = "Gyroscope Sensor Value: " + sensorValues[i];
                    break;
            }

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(sensorName)
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            notificationManager.notify(NOTIFICATION_ID + i, notification);

        }
    }

    private void recordSensorData() {
        // Retrieve sensor values from UI or other sources
        String lightSensorText = lightSensorValue.getText().toString();
        String proximitySensorText = proximitySensorValue.getText().toString();
        String accelerometerSensorText = accelerometerSensorValue.getText().toString();
        String gyroscopeSensorText = gyroscopeSensorValue.getText().toString();

        // Parse float values or assign default values if the text is null or empty
        float lightValue = parseFloatOrDefault(lightSensorText, 19, 0.0f);
        float proximityValue = parseFloatOrDefault(proximitySensorText, 24, 0.0f);
        float accelerometerValue = parseFloatOrDefault(accelerometerSensorText, 29, 0.0f);
        float gyroscopeValue = parseFloatOrDefault(gyroscopeSensorText, 25, 0.0f);

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

    private float parseFloatOrDefault(String value, int startIndex, float defaultValue) {
        if (value != null && value.length() >= startIndex) {
            String floatValue = value.substring(startIndex);
            try {
                return Float.parseFloat(floatValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

}
//...
