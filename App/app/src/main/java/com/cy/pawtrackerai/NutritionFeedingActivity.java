package com.cy.pawtrackerai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;

public class NutritionFeedingActivity extends AppCompatActivity {

    private BarChart chartCalories;
    private LineChart chartActivityWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition_feeding);

        // Initialize charts
        chartCalories = findViewById(R.id.chartCalories);
        chartActivityWeight = findViewById(R.id.chartActivityWeight);

        // Setup charts
        setupCaloriesChart();
        setupActivityWeightChart();
    }

    private void setupCaloriesChart() {
        // Demo data for 7 days
        ArrayList<BarEntry> burnedEntries = new ArrayList<>();
        ArrayList<BarEntry> intakeEntries = new ArrayList<>();

        // Burned calories data
        burnedEntries.add(new BarEntry(0f, 650f));
        burnedEntries.add(new BarEntry(1f, 580f));
        burnedEntries.add(new BarEntry(2f, 720f));
        burnedEntries.add(new BarEntry(3f, 610f));
        burnedEntries.add(new BarEntry(4f, 680f));
        burnedEntries.add(new BarEntry(5f, 700f));
        burnedEntries.add(new BarEntry(6f, 640f));

        // Intake calories data
        intakeEntries.add(new BarEntry(0f, 800f));
        intakeEntries.add(new BarEntry(1f, 850f));
        intakeEntries.add(new BarEntry(2f, 780f));
        intakeEntries.add(new BarEntry(3f, 820f));
        intakeEntries.add(new BarEntry(4f, 790f));
        intakeEntries.add(new BarEntry(5f, 810f));
        intakeEntries.add(new BarEntry(6f, 800f));

        // Create datasets
        BarDataSet burnedDataSet = new BarDataSet(burnedEntries, "Burned");
        burnedDataSet.setColor(Color.parseColor("#FF9A3E"));
        burnedDataSet.setValueTextSize(10f);
        burnedDataSet.setDrawValues(false);

        BarDataSet intakeDataSet = new BarDataSet(intakeEntries, "Intake");
        intakeDataSet.setColor(Color.parseColor("#4ECDC4"));
        intakeDataSet.setValueTextSize(10f);
        intakeDataSet.setDrawValues(false);

        // Create bar data
        BarData barData = new BarData(burnedDataSet, intakeDataSet);
        barData.setBarWidth(0.35f);

        // Configure chart
        chartCalories.setData(barData);
        chartCalories.getDescription().setEnabled(false);
        chartCalories.setDrawGridBackground(false);
        chartCalories.setDrawBarShadow(false);
        chartCalories.setHighlightFullBarEnabled(false);
        chartCalories.setDrawValueAboveBar(true);
        chartCalories.setPinchZoom(false);
        chartCalories.setDoubleTapToZoomEnabled(false);
        chartCalories.getLegend().setEnabled(false);

        // Configure X-axis
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        XAxis xAxis = chartCalories.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setLabelCount(7);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#666666"));

        // Configure Y-axis (left)
        YAxis leftAxis = chartCalories.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1000f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.parseColor("#666666"));

        // Disable right Y-axis
        chartCalories.getAxisRight().setEnabled(false);

        // Group bars
        float groupSpace = 0.1f;
        float barSpace = 0.05f;
        chartCalories.groupBars(0f, groupSpace, barSpace);
        chartCalories.setFitBars(true);
        chartCalories.invalidate();
    }

    private void setupActivityWeightChart() {
        // Demo data for 4 weeks
        ArrayList<Entry> activityEntries = new ArrayList<>();
        ArrayList<Entry> weightEntries = new ArrayList<>();

        // Activity data (minutes per day)
        activityEntries.add(new Entry(0f, 62f));
        activityEntries.add(new Entry(1f, 68f));
        activityEntries.add(new Entry(2f, 58f));
        activityEntries.add(new Entry(3f, 65f));

        // Weight data (kg)
        weightEntries.add(new Entry(0f, 25.2f));
        weightEntries.add(new Entry(1f, 25.0f));
        weightEntries.add(new Entry(2f, 24.8f));
        weightEntries.add(new Entry(3f, 24.7f));

        // Create datasets
        LineDataSet activityDataSet = new LineDataSet(activityEntries, "Activity (min/day)");
        activityDataSet.setColor(Color.parseColor("#FF9A3E"));
        activityDataSet.setCircleColor(Color.parseColor("#FF9A3E"));
        activityDataSet.setCircleRadius(5f);
        activityDataSet.setCircleHoleRadius(3f);
        activityDataSet.setLineWidth(2.5f);
        activityDataSet.setDrawValues(false);
        activityDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        activityDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet weightDataSet = new LineDataSet(weightEntries, "Weight (kg)");
        weightDataSet.setColor(Color.parseColor("#4ECDC4"));
        weightDataSet.setCircleColor(Color.parseColor("#4ECDC4"));
        weightDataSet.setCircleRadius(5f);
        weightDataSet.setCircleHoleRadius(3f);
        weightDataSet.setLineWidth(2.5f);
        weightDataSet.setDrawValues(false);
        weightDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        weightDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        // Create line data
        LineData lineData = new LineData(activityDataSet, weightDataSet);

        // Configure chart
        chartActivityWeight.setData(lineData);
        chartActivityWeight.getDescription().setEnabled(false);
        chartActivityWeight.setDrawGridBackground(false);
        chartActivityWeight.setPinchZoom(false);
        chartActivityWeight.setDoubleTapToZoomEnabled(false);
        chartActivityWeight.getLegend().setEnabled(false);

        // Configure X-axis
        String[] weeks = new String[]{"Week 1", "Week 2", "Week 3", "Week 4"};
        XAxis xAxis = chartActivityWeight.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
        xAxis.setLabelCount(4);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#666666"));

        // Configure left Y-axis (Activity)
        YAxis leftAxis = chartActivityWeight.getAxisLeft();
        leftAxis.setAxisMinimum(50f);
        leftAxis.setAxisMaximum(80f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.parseColor("#666666"));

        // Configure right Y-axis (Weight)
        YAxis rightAxis = chartActivityWeight.getAxisRight();
        rightAxis.setAxisMinimum(24f);
        rightAxis.setAxisMaximum(26f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setTextSize(10f);
        rightAxis.setTextColor(Color.parseColor("#666666"));

        chartActivityWeight.invalidate();
    }
}