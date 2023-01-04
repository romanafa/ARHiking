package com.example.arhiking.fragments;

import static android.content.Context.SENSOR_SERVICE;

import static org.osgeo.proj4j.parser.Proj4Keyword.f;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arhiking.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class AccelerometerVisualizationFragment extends Fragment {
    TextView tvRawAcceleratorData, tvFilteredAcceleratorData;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private double accelerationRawValue;
    private double accelerationFilteredValue;

    private Float[] gravity = {0.0f, 0.0f, 0.0f};
    private Float[] linear_acceleration = {0.0f, 0.0f, 0.0f};

    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    private Viewport viewport;


    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    LineGraphSeries<DataPoint> seriesFiltered = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            /*final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

            linear_acceleration[0] = x - gravity[0];
            linear_acceleration[1] = y - gravity[1];
            linear_acceleration[2] = z - gravity[2];

            float fx = linear_acceleration[0];
            float fy = linear_acceleration[1];
            float fz = linear_acceleration[2];*/

            accelerationRawValue = Math.sqrt(x * x + y * y + z * z);
            accelerationFilteredValue = new UserDataFragment().getHighPassFilteredAccelerometerData(x,y,z);

            tvRawAcceleratorData.setText("Raw data reading: " + accelerationRawValue);
            tvFilteredAcceleratorData.setText("Filtered data reading: " + accelerationFilteredValue);

            pointsPlotted++;

            series.appendData(new DataPoint(pointsPlotted, accelerationRawValue), true, pointsPlotted);
            seriesFiltered.appendData(new DataPoint(pointsPlotted, accelerationFilteredValue), true, pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted - 200);
            viewport.setMinY(-20);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerator_visualization, container, false);
        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        tvRawAcceleratorData = view.findViewById(R.id.tvRawAcceleratorData);
        tvFilteredAcceleratorData = view.findViewById(R.id.tvFilteredAcceleratorData);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);
        graph.addSeries(seriesFiltered);

        // Inflate the layout for this fragment
        return view;
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }
}