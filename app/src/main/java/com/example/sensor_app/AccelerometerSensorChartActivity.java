package com.example.sensor_app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sensor_app.DatabaseHelper;
import com.example.sensor_app.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class AccelerometerSensorChartActivity extends AppCompatActivity {
    private LineChart chart;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_sensor_chart);

        chart = findViewById(R.id.chart);

        // Set chart title
        TextView chartTitle = findViewById(R.id.chartTitle);
        chartTitle.setText("Accelerometer Sensor Time Series Chart");

        // Retrieve data from SQLite database
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT timestamp, value FROM accelerometer_sensor", null);

        List<Entry> entries = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                long timestamp = cursor.getLong(0);
                float value = cursor.getFloat(1);

                entries.add(new Entry(timestamp, value));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Create a dataset from the entries
        LineDataSet dataSet = new LineDataSet(entries, "Accelerometer Sensor Values");

        // Customize the dataset's appearance
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);

        // Create a LineData object with the dataset
        LineData lineData = new LineData(dataSet);

        // Set the LineData to the chart
        chart.setData(lineData);

        // Set chart description
        Description description = new Description();
        description.setText("Time vs Accelerometer Sensor Values");
        chart.setDescription(description);

        // Refresh the chart
        chart.invalidate();
    }
}
