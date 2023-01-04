package com.example.arhiking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arhiking.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;


public class ChartsFragment extends Fragment {
    BarChart barChart;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        barChart=(BarChart)view.findViewById(R.id.bar_chart);

        // Inflate the layout for this fragment
        return view;
    }

    private ArrayList<BarEntry> dataValues1() {
        ArrayList<BarEntry> dataValues = new ArrayList<BarEntry>();
        dataValues.add(new BarEntry(0, 20));
        dataValues.add(new BarEntry(1, 15));
        dataValues.add(new BarEntry(2, 22));
        dataValues.add(new BarEntry(3, 35));
        dataValues.add(new BarEntry(4, 24));

        return dataValues;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BarDataSet barDataSet = new BarDataSet(dataValues1(),"Data Set 1");
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        BarData data = new BarData(dataSets);
        barChart.setData(data);
        barChart.invalidate();
    }
}