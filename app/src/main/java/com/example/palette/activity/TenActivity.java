package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.palette.R;
import com.example.palette.marker.CustomMarker;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TenActivity extends AppCompatActivity {
    public static final String TAG = "TenActivity";
    private LineChart lineChart;
    private String[] days = {"2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00",
            "2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00",
            "2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00","2023-10-27 00:00"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ten);

        lineChart = findViewById(R.id.lineChart);
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Float> e1= new ArrayList<>();
        List<Float> e2= new ArrayList<>();
        float[] datas = {-5f,10f,15f,10f,20f,5f,10f,15f,10f,20f,5f,10f,15f,10f,20f,5f,10f,15f,10f,20f,5f,10f,15f,10f,20f,5f,10f,15f,10f,20f};
        for (int i = 0; i < datas.length; i++) {
            e1.add(datas[i]);
            entries.add(new Entry(i,datas[i]));
        }
        float[] datas2 = {0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
        for (int i = 0; i < datas2.length; i++) {
            e2.add(datas2[i]);
            entries2.add(new Entry(i,datas2[i]));
        }
        Log.i(TAG,Collections.max(e1)+"");
        Log.i(TAG,Collections.min(e1)+"");
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(29);
        xAxis.setAxisMaximum(29);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        xAxis.setYOffset(10);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return days[(int) value];
            }
        });
        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setTextColor(Color.RED);
        axisLeft.setAxisMaximum(130);
        axisLeft.setAxisMinimum(-20);
        axisLeft.setLabelCount(6,true);
        axisLeft.setDrawGridLines(true);
        axisLeft.setGridLineWidth(1);


        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setAxisMaximum(40);
        axisRight.setAxisMinimum(-10);
        axisRight.setTextColor(Color.BLUE);
        axisRight.setDrawGridLines(true);
        axisRight.setGridLineWidth(1);
        axisRight.setLabelCount(6,true);
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setYOffset(20);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        CustomMarker mv = new CustomMarker(this);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        lineChart.setExtraBottomOffset(5);

        LineDataSet lineDataSet = new LineDataSet(entries,"温度(℃)");
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(false);
        LineDataSet lineDataSet2 = new LineDataSet(entries2,"湿度(%)");
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet2.setColor(Color.BLUE);
        lineDataSet2.setCircleColor(Color.BLUE);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawVerticalHighlightIndicator(false);
        LineData lineData = new LineData(lineDataSet);
        lineData.addDataSet(lineDataSet2);

        lineChart.setData(lineData);
    }
}