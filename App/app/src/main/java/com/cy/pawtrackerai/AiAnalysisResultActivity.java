package com.cy.pawtrackerai;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AiAnalysisResultActivity extends AppCompatActivity {

    private LinearLayout fever_card, vetenary_linear;

    private LineChart heartRateChart, temperatureChart, spo2Chart;
    private ImageView btnBack;
    private MaterialButton btnSaveRecord, btnShareVet, btnRecheckSymptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_analysis_result);


        initializeViews();
        setupCharts();
        setupClickListeners();

    }

    private void initializeViews(){
        btnBack = findViewById(R.id.btnBack);
        heartRateChart = findViewById(R.id.heartRateChart);
        temperatureChart = findViewById(R.id.temperatureChart);
        spo2Chart = findViewById(R.id.spo2Chart);
        btnSaveRecord = findViewById(R.id.btnSaveRecord);
        btnShareVet = findViewById(R.id.btnShareVet);
        btnRecheckSymptoms = findViewById(R.id.btnRecheckSymptoms);



    }
    private void setupCharts() {
        // Setup Heart Rate Chart with demo data (80-115 BPM - Elevated trend)
        setupHeartRateChart();

        // Setup Temperature Chart with demo data (37-39.8°C - High trend)
        setupTemperatureChart();

        // Setup SpO2 Chart with demo data (98-94% - Normal but declining)
        setupSpo2Chart();
    }



    private void setupHeartRateChart() {
        // Demo data: Heart rate increasing from 80 to 115 BPM over last hour
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 80));   // 10:00
        entries.add(new Entry(1, 85));   // 10:15
        entries.add(new Entry(2, 95));   // 10:30
        entries.add(new Entry(3, 100));  // 10:45
        entries.add(new Entry(4, 108));  // 10:50
        entries.add(new Entry(5, 115));  // 11:00

        LineDataSet dataSet = new LineDataSet(entries, "Heart Rate");
        dataSet.setColor(Color.parseColor("#FF5722")); // Red/Orange color for elevated
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.parseColor("#FF5722"));
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#FFCCBC"));
        dataSet.setFillAlpha(80);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData lineData = new LineData(dataSet);

        String[] timeLabels = {"10:00", "10:15", "10:30", "10:45", "10:50", "11:00"};
        configureChart(heartRateChart, lineData, timeLabels, 70, 120, Color.parseColor("#FFEBEE"));
    }

    private void setupTemperatureChart() {
        // Demo data: Temperature rising from 37°C to 39.8°C
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 37.0f));  // 10:00 - Normal
        entries.add(new Entry(1, 37.5f));  // 10:15
        entries.add(new Entry(2, 38.2f));  // 10:30 - Getting warm
        entries.add(new Entry(3, 38.8f));  // 10:45
        entries.add(new Entry(4, 39.3f));  // 10:50
        entries.add(new Entry(5, 39.8f));  // 11:00 - Fever range

        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(Color.parseColor("#FF9800")); // Orange color for high temp
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.parseColor("#FF9800"));
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#FFE0B2"));
        dataSet.setFillAlpha(80);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData lineData = new LineData(dataSet);

        String[] timeLabels = {"10:00", "10:15", "10:30", "10:45", "10:50", "11:00"};
        configureChart(temperatureChart, lineData, timeLabels, 36, 41, Color.parseColor("#FFF3E0"));
    }

    private void setupSpo2Chart() {
        // Demo data: SpO2 declining slightly from 98% to 94% (still normal range)
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 98));     // 10:00
        entries.add(new Entry(1, 97));     // 10:15
        entries.add(new Entry(2, 96));     // 10:30
        entries.add(new Entry(3, 95));     // 10:45
        entries.add(new Entry(4, 94.5f));  // 10:50
        entries.add(new Entry(5, 94));     // 11:00

        LineDataSet dataSet = new LineDataSet(entries, "Blood Oxygen");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Green color for normal
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.parseColor("#4CAF50"));
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#C8E6C9"));
        dataSet.setFillAlpha(80);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData lineData = new LineData(dataSet);

        String[] timeLabels = {"10:00", "10:15", "10:30", "10:45", "10:50", "11:00"};
        configureChart(spo2Chart, lineData, timeLabels, 90, 100, Color.parseColor("#E8F5E9"));
    }

    private void configureChart(LineChart chart, LineData lineData, String[] timeLabels,
                                float yMin, float yMax, int backgroundColor) {
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(true);
        chart.setGridBackgroundColor(backgroundColor);
        chart.setExtraOffsets(5, 10, 5, 10);

        // X Axis Configuration
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(9f);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setLabelCount(timeLabels.length);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < timeLabels.length) {
                    return timeLabels[index];
                }
                return "";
            }
        });

        // Left Y Axis Configuration
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(yMin);
        leftAxis.setAxisMaximum(yMax);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setTextSize(9f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setLabelCount(5, false);

        // Right Y Axis - Disable
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Animation
        chart.animateX(1200);
        chart.invalidate();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AiAnalysisResultActivity.this,
                        "Analysis saved to health records", Toast.LENGTH_SHORT).show();
                // TODO: Implement save to database
            }
        });

        btnShareVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AiAnalysisResultActivity.this,
                        "Opening share options...", Toast.LENGTH_SHORT).show();
                // TODO: Implement share functionality (Email, WhatsApp, etc.)
            }
        });

        btnRecheckSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AiAnalysisResultActivity.this,
                        "Starting new symptom check...", Toast.LENGTH_SHORT).show();
                // TODO: Navigate back to symptom checker or restart analysis
                finish();
            }
        });
    }
}