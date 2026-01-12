package com.cy.pawtrackerai;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;

public class MoodAnalysisActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView btnDaily, btnWeekly;
    private ImageView btnBack;
    private Button btnCheckMood;
    private boolean isWeeklyView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_analysis);

        initViews();
        setupChart();
        setupListeners();
        loadWeeklyData();
    }

    private void initViews() {
        lineChart = findViewById(R.id.lineChart);
        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnBack = findViewById(R.id.btnBack);
        btnCheckMood = findViewById(R.id.btnCheckMood);
    }

    private void setupChart() {
        // Configure chart appearance
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawBorders(false);
        lineChart.getLegend().setEnabled(false);

        // Configure X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setTextSize(10f);

        // Configure left Y-axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F0F0F0"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(Color.parseColor("#999999"));
        leftAxis.setTextSize(10f);
        leftAxis.setGranularity(25f);

        // Disable right Y-axis
        lineChart.getAxisRight().setEnabled(false);

        // Add margin
        lineChart.setExtraOffsets(5, 10, 5, 10);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDaily.setOnClickListener(v -> {
            if (isWeeklyView) {
                isWeeklyView = false;
                updateToggleButtons();
                loadDailyData();
            }
        });

        btnWeekly.setOnClickListener(v -> {
            if (!isWeeklyView) {
                isWeeklyView = true;
                updateToggleButtons();
                loadWeeklyData();
            }
        });

        btnCheckMood.setOnClickListener(v -> {
            // Handle check mood action
            // You can add your logic here
        });
    }

    private void updateToggleButtons() {
        if (isWeeklyView) {
            btnWeekly.setBackgroundResource(R.drawable.toggle_selected);
            btnWeekly.setTextColor(Color.WHITE);
            btnDaily.setBackgroundResource(android.R.color.transparent);
            btnDaily.setTextColor(Color.parseColor("#666666"));
        } else {
            btnDaily.setBackgroundResource(R.drawable.toggle_selected);
            btnDaily.setTextColor(Color.WHITE);
            btnWeekly.setBackgroundResource(android.R.color.transparent);
            btnWeekly.setTextColor(Color.parseColor("#666666"));
        }
    }

    private void loadWeeklyData() {
        ArrayList<Entry> entries = new ArrayList<>();

        // Demo weekly data (Mon to Sun)
        entries.add(new Entry(0, 82f)); // Mon
        entries.add(new Entry(1, 88f)); // Tue
        entries.add(new Entry(2, 92f)); // Wed
        entries.add(new Entry(3, 89f)); // Thu
        entries.add(new Entry(4, 91f)); // Fri
        entries.add(new Entry(5, 94f)); // Sat
        entries.add(new Entry(6, 93f)); // Sun

        setupChartData(entries, new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"});
    }

    private void loadDailyData() {
        ArrayList<Entry> entries = new ArrayList<>();

        // Demo daily data (24 hours)
        for (int i = 0; i < 24; i += 3) {
            entries.add(new Entry(i / 3, 85f + (float)(Math.random() * 10)));
        }

        setupChartData(entries, new String[]{"0h", "3h", "6h", "9h", "12h", "15h", "18h", "21h"});
    }

    private void setupChartData(ArrayList<Entry> entries, String[] labels) {
        LineDataSet dataSet = new LineDataSet(entries, "Mood");

        // Line appearance
        dataSet.setColor(Color.parseColor("#00C896"));
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.parseColor("#00C896"));
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setCircleHoleColor(Color.WHITE);

        // Fill
        dataSet.setDrawFilled(false);

        // Values
        dataSet.setDrawValues(false);

        // Smooth curve
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Set X-axis labels
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.length) {
                    return labels[index];
                }
                return "";
            }
        });

        lineChart.invalidate();
    }
}