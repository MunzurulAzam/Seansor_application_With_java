package com.example.sensor_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sensor_data.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_LIGHT_SENSOR = "light_sensor";
    private static final String TABLE_PROXIMITY_SENSOR = "proximity_sensor";
    private static final String TABLE_ACCELEROMETER_SENSOR = "accelerometer_sensor";
    private static final String TABLE_GYROSCOPE_SENSOR = "gyroscope_sensor";

    // Common column names
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_VALUE = "value";

    // Table create statements
    private static final String CREATE_TABLE_LIGHT_SENSOR = "CREATE TABLE " + TABLE_LIGHT_SENSOR + "("
            + COLUMN_TIMESTAMP + " INTEGER PRIMARY KEY,"
            + COLUMN_VALUE + " REAL)";

    private static final String CREATE_TABLE_PROXIMITY_SENSOR = "CREATE TABLE " + TABLE_PROXIMITY_SENSOR + "("
            + COLUMN_TIMESTAMP + " INTEGER PRIMARY KEY,"
            + COLUMN_VALUE + " REAL)";

    private static final String CREATE_TABLE_ACCELEROMETER_SENSOR = "CREATE TABLE " + TABLE_ACCELEROMETER_SENSOR + "("
            + COLUMN_TIMESTAMP + " INTEGER PRIMARY KEY,"
            + COLUMN_VALUE + " REAL)";

    private static final String CREATE_TABLE_GYROSCOPE_SENSOR = "CREATE TABLE " + TABLE_GYROSCOPE_SENSOR + "("
            + COLUMN_TIMESTAMP + " INTEGER PRIMARY KEY,"
            + COLUMN_VALUE + " REAL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables
        db.execSQL(CREATE_TABLE_LIGHT_SENSOR);
        db.execSQL(CREATE_TABLE_PROXIMITY_SENSOR);
        db.execSQL(CREATE_TABLE_ACCELEROMETER_SENSOR);
        db.execSQL(CREATE_TABLE_GYROSCOPE_SENSOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGHT_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROXIMITY_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCELEROMETER_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GYROSCOPE_SENSOR);

        // Create tables again
        onCreate(db);
    }

    // Light Sensor Methods

    public void insertLightSensorValue(long timestamp, float value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_VALUE, value);
        db.insert(TABLE_LIGHT_SENSOR, null, values);
        db.close();
    }

    public List<SensorData> getAllLightSensorValues() {
        List<SensorData> sensorDataList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LIGHT_SENSOR + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                long timestamp;
                float value;

                int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
                if (timestampIndex != -1) {
                    timestamp = cursor.getLong(timestampIndex);
                } else {
                    // Column does not exist
                    continue;
                }

                int valueIndex = cursor.getColumnIndex(COLUMN_VALUE);
                if (valueIndex != -1) {
                    value = cursor.getFloat(valueIndex);
                } else {
                    // Column does not exist
                    continue;
                }

                SensorData sensorData = new SensorData(timestamp, value);
                sensorDataList.add(sensorData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sensorDataList;
    }

    // Add methods for other sensor tables (Proximity, Accelerometer, Gyroscope) similarly...

    public class SensorData {
        private long timestamp;
        private float value;

        public SensorData(long timestamp, float value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public float getValue() {
            return value;
        }
    }
}
