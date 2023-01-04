package com.example.arhiking.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.HikeActivityDao;
import com.example.arhiking.MainActivity;
import com.example.arhiking.Models.Hike_Activity;
import com.example.arhiking.R;
import com.example.arhiking.Tour;
import com.example.arhiking.databinding.FragmentCompletedHikeBinding;
import com.example.arhiking.databinding.FragmentHomeBinding;
import com.example.arhiking.viewmodels.RegisterHikeViewModel;

import org.osmdroid.util.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CompletedHikeFragment extends Fragment {

    private FragmentCompletedHikeBinding binding;
    DatePickerDialog picker;
    EditText calendarEditText;
    RegisterHikeViewModel viewModel;
    Button btnSave;
    AppDatabase_v2 db;
    Context ctx;
    HikeActivityDao dao;
    EditText etHikeName;
    TextView tvDuration;
    TextView tvDistance;
    TextView tvHighestElevation;
    TextView tvSpeed;
    TextView tvCategory;
    Hike_Activity hikeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompletedHikeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ctx = getActivity().getApplicationContext();
        db = Room.databaseBuilder(ctx,
                AppDatabase_v2.class, "database-v2").allowMainThreadQueries().build();

        dao = db.hikeActivityDao();
        viewModel =
                new ViewModelProvider(getActivity()).get(RegisterHikeViewModel.class);

         hikeActivity = dao.getHikeActivityById(
                viewModel.getHikeActivityId().getValue());

        etHikeName = binding.hikeNameEditText;
        tvDuration = binding.durationNewHikeTextView;
        tvDistance = binding.distanceNewHikeTextView;
        tvHighestElevation = binding.highestElevationNewHikeTextView;
        tvSpeed = binding.completedHikeSpeedResultTextView;
        tvCategory = binding.completedHikeCategoryTextView;

        long timeRegistered = hikeActivity.timeRegistered;
        Date date = new Date();
        long currentTime = date.getTime();
        long duration = currentTime - timeRegistered;
        tvDuration.setText(((int) duration/1000) + " sec");


        GeoPoint startPoint = hikeActivity.hikeActivityStartingPoint;
        GeoPoint endPoint = viewModel.getCurrentLocation().getValue();

        double highestElevation = viewModel.getHighestElevation().getValue();

        tvHighestElevation.setText(String.valueOf((int)highestElevation));

        float[] results = new float[1];
        Location.distanceBetween(startPoint.getLatitude(),
                startPoint.getLongitude(), endPoint.getLatitude(),
                endPoint.getLongitude(), results);

        float distance = results[0] / 1000;
        tvDistance.setText(((int) distance) +
                " km");

        int speed = (int) (results[0] / duration);
        tvSpeed.setText(String.valueOf(speed));

        // Dropdown list for hike difficulty
        Spinner difficultySpinner = root.findViewById(R.id.difficultySpinner);
        // Array adapter with default layout and string array from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hike_difficulty_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply adapter to the spinner
        difficultySpinner.setAdapter(adapter);


        Spinner categorySpinner = root.findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hike_category_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(catAdapter);

        if (hikeActivity.category != null){
            categorySpinner.setVisibility(View.INVISIBLE);
            tvCategory.setVisibility(View.INVISIBLE);
        }

        btnSave = binding.saveNewHikeButton;
        btnSave.setOnClickListener(v -> {

            hikeActivity.hikeActivityStartingPoint = startPoint;
            hikeActivity.hikeActivityEndingPoint = endPoint;

            hikeActivity.difficulty = (String) difficultySpinner.getSelectedItem();

            if (hikeActivity.category == null) {
                hikeActivity.category = (String) categorySpinner.getSelectedItem();
            }

            hikeActivity.highestElevation = highestElevation;
            hikeActivity.hikeActivityName
                    = etHikeName.getText().toString();

            hikeActivity.duration = duration / 1000;

            hikeActivity.timeEnd = currentTime;
            hikeActivity.hikeDistance = distance;
            //oppdaterer hike med input fra skjema og navigerer til RegisterHikeFragment
            db.hikeActivityDao().updateHikeActivity(hikeActivity);

            Toast.makeText(getContext(), "Hike saved",
                    Toast.LENGTH_SHORT).show();
            Navigation.findNavController(getView()).navigate(R.id.
                    action_completedHikeFragment_to_navigation_register_hike);
        });



        return root;

    }

    //TODO : listener for hike difficulty dropdown

    // Implement OnItemSelectedListener
    /* public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}