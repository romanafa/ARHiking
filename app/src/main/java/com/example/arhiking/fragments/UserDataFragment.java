package com.example.arhiking.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arhiking.R;
import com.example.arhiking.databinding.FragmentUserDataBinding;
import com.example.arhiking.viewmodels.SettingsViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDataFragment extends Fragment {

    private FragmentUserDataBinding binding;
    private SettingsViewModel settingsViewModel;

    BarChart barChartHikesPerMonth;
    BarData barDataHikesPerMonth;
    BarDataSet barDataSetHikesPerMonth;
    ArrayList barHikesPerMonthEntriesArrayList;
    TextView totalHikesCountTV;
    TextView totalDistanceSumTV;
    String[] monthLabels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        barChartHikesPerMonth = root.findViewById(R.id.barCharUserData);
        getHikesPerMonthDataEntries();

        barDataSetHikesPerMonth = new BarDataSet(barHikesPerMonthEntriesArrayList, "Hikes");

       barDataHikesPerMonth = new BarData(barDataSetHikesPerMonth);

        XAxis xAxis = barChartHikesPerMonth.getXAxis();
       xAxis.setPosition(
               XAxis.XAxisPosition.TOP
       );
        xAxis.setLabelCount(12);

       xAxis.setValueFormatter(new IAxisValueFormatter() {
           @Override
           public String getFormattedValue(float value, AxisBase axis) {
               return monthLabels[(int)value-1];
           }
       });

        barChartHikesPerMonth.setData(barDataHikesPerMonth);
        barChartHikesPerMonth.setBackgroundColor(getResources().getColor(R.color.white));
        barChartHikesPerMonth.getDescription().setText("The total number of hikes in each month this year");
        barDataSetHikesPerMonth.setColors(ColorTemplate.COLORFUL_COLORS);
        barChartHikesPerMonth.animateY(1000);


        totalHikesCountTV = root.findViewById(R.id.totalHikesDataTextView);

        // Observe data and set string text to the result of getCount() method
        settingsViewModel.getCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                totalHikesCountTV.setText(String.valueOf(integer));
            }
        });

        totalDistanceSumTV = root.findViewById(R.id.totalDistanceDataTextView);

        // Observe data and set string text to the result of getTotalDistanceSum() method
        settingsViewModel.getTotalDistanceSum().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                totalDistanceSumTV.setText(String.valueOf(aDouble) + " meters");
            }
        });

        return root;
    }

    private void getHikesPerMonthDataEntries(){
        barHikesPerMonthEntriesArrayList = new ArrayList<>();

        barHikesPerMonthEntriesArrayList.add(new BarEntry(1, 1));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(2, 3));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(3, 1));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(4, 5));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(5, 4));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(6, 8));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(7, 6));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(8, 7));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(9, 5));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(10, 3));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(11, 0));
        barHikesPerMonthEntriesArrayList.add(new BarEntry(12, 2));

        // TODO find out and fix x to show month strings
       monthLabels = new String[]
       {
               "jan", "feb", "mar", "apr", "mai", "jun",
               "jul", "aug", "sep", "okt", "nov", "des"
       };

    }


    
    public double getHighPassFilteredAccelerometerData(float x, float y, float z) {
         Float[] gravity = {0.0f, 0.0f, 0.0f};
         final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

        float fx = x - gravity[0];
        float fy = y - gravity[1];
        float fz = z - gravity[2];

        double accelerationFilteredValue = Math.sqrt(fx * fx + fy * fy + fz * fz);
        return accelerationFilteredValue;
    }
}