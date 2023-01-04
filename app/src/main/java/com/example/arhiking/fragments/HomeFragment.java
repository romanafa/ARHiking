package com.example.arhiking.fragments;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.arhiking.Adapters.HikesAdapter;
import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.HikeActivityDao;
import com.example.arhiking.Helpers;
import com.example.arhiking.Models.Hike_Activity;
import com.example.arhiking.R;
import com.example.arhiking.Tour;
import com.example.arhiking.databinding.FragmentHomeBinding;
import com.example.arhiking.viewmodels.HomeViewModel;
import com.example.arhiking.viewmodels.RegisterHikeViewModel;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;

    private LinearLayout startHike;
    private LinearLayout startBike;
    private LinearLayout startSki;
    private LinearLayout startOther;

    private CardView cardBrowseLibrary;
    private CardView cardViewMap;

    HikeActivityDao hikeActivityDao;
    AppDatabase_v2 db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       // Listeners for activity image buttons
        startHike = root.findViewById(R.id.buttonHikeTrip);
        startBike = root.findViewById(R.id.buttonBikeTrip);
        startSki = root.findViewById(R.id.buttonSkiTrip);
        startOther = root.findViewById(R.id.buttonOtherTrip);

        startHike.setOnClickListener(this);
        startBike.setOnClickListener(this);
        startSki.setOnClickListener(this);
        startOther.setOnClickListener(this);

        // Listener to navigate to library when clicking on cardview in home fragment
        // todo: Fix bottom navigation after library opens through cardview onClick
        cardBrowseLibrary = root.findViewById(R.id.cardBrowseLibrary);
        cardBrowseLibrary.setOnClickListener(this);

        // Listener to navigate to map when clicking on cardview in home fragment
        // todo: Fix bottom navigation after map opens through cardview onClick
        cardViewMap = root.findViewById(R.id.cardMapView);
        cardViewMap.setOnClickListener(this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHikesViewPager();
    }

    // Dummy data for hikes view in HomeFragment
    private void setupHikesViewPager() {
        ViewPager2 hikesViewPager = getView().findViewById(R.id.hikesViewPager);
        hikesViewPager.setClipToPadding(false);
        hikesViewPager.setClipChildren(false);
        hikesViewPager.setOffscreenPageLimit(3);
        hikesViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        hikesViewPager.setPageTransformer(compositePageTransformer);
        hikesViewPager.setAdapter(new HikesAdapter(getHikes()));
    }

    private List<Tour> getHikes() {
        ArrayList<Tour> hikes = new ArrayList<>();

        db = Room.databaseBuilder(getContext(),
                AppDatabase_v2.class, "database-v2").allowMainThreadQueries().build();

        hikeActivityDao = db.hikeActivityDao();

        List<Hike_Activity> activities = hikeActivityDao.getAll();

        if (activities.size() > 0) {

            if (activities.size() > 4) {//hvis 5, ikke ta med mer enn 5.

                for (Hike_Activity activity : activities.subList(
                        activities.size() - 6, activities.size() - 1
                )) {

                    Tour tur = new Tour();
                    tur.name = activity.hikeActivityName;
                    tur.hikeLength = String.format("%.2f", activity.hikeDistance);
                    tur.hikeElevation = String.format("%.2f", activity.highestElevation);
                    tur.hikeTime = String.valueOf(activity.duration / 60);
                    String dateString = new SimpleDateFormat("dd-MM-yyyy").format(new Date(
                            activity.timeEnd));
                    tur.hikeDate = dateString;
                    long hikeId = activity.hikeActivityId;

                    int poster = 0;
                    int img1 = 0;
                    int img2 = 0;
                    int img3 = 0;
                    //få tak i bilder som hører til hikeId
                    String path;
                    if (Helpers.isEmulator()) {
                        path = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                    } else {
                        path = "/storage/self/primary/Android/data/com.example.arhiking/files/Pictures/";
                    }
                    File f = new File(path);
                    File[] files = f.listFiles();


                    for (File file : files) {
                        String name = file.toString().split(":")[0];
                        String _name = name.split("/")[9];
                        Uri bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", file);
                        ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), bmpUri);

                        if (_name.equals(String.valueOf(hikeId))) {
                            if (poster == 0) {
                                poster = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpPoster = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (img1 == 0) {
                                img1 = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpImage1 = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (img2 == 0) {
                                img2 = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpImage2 = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (img3 == 0) {
                                img3 = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpImage3 = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }


                    hikes.add(tur);

                }

            } else {

                for (Hike_Activity activity : activities)
                 {

                    Tour tur = new Tour();
                    tur.name = activity.hikeActivityName;
                    tur.hikeLength = String.format("%.2f", activity.hikeDistance);
                    tur.hikeElevation = String.format("%.2f", activity.highestElevation);
                    tur.hikeTime = String.valueOf(activity.duration / 60);
                    String dateString = new SimpleDateFormat("dd-MM-yyyy").format(new Date(
                            activity.timeEnd));
                    tur.hikeDate = dateString;
                    long hikeId = activity.hikeActivityId;

                    int poster = 0;
                    int img1 = 0;
                    int img2 = 0;
                    int img3 = 0;
                    //få tak i bilder som hører til hikeId
                    String path;
                    if (Helpers.isEmulator()) {
                        path = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                    } else {
                        path = "/storage/self/primary/Android/data/com.example.arhiking/files/Pictures/";
                    }
                    File f = new File(path);
                    File[] files = f.listFiles();


                    for (File file : files) {
                        String name = file.toString().split(":")[0];
                        String _name = name.split("/")[9];
                        Uri bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", file);
                        ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), bmpUri);

                        if (_name.equals(String.valueOf(hikeId))) {
                            if (poster == 0) {
                                poster = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpPoster = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (img1 == 0) {
                                img1 = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpImage1 = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (img2 == 0) {
                                img2 = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpImage2 = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (img3 == 0) {
                                img3 = 1;

                                try {
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                    tur.btmpImage3 = bitmap;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }


                    hikes.add(tur);

                }

            }

        }
        return hikes;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonHikeTrip:
                Bundle bundle = new Bundle();
                bundle.putString("activity", "hike");
                Navigation.findNavController(getView()).
                        popBackStack();
                Navigation.findNavController(getView()).navigate(R.id.navigation_register_hike,
                        bundle);
                break;
            case R.id.buttonBikeTrip:
                Bundle bikeBundle = new Bundle();
                bikeBundle.putString("activity", "bike");
                Navigation.findNavController(getView()).
                        popBackStack();
                Navigation.findNavController(getView()).navigate(R.id.navigation_register_hike,
                        bikeBundle);
                break;
            case R.id.buttonSkiTrip:
                Bundle skiBundle = new Bundle();
                skiBundle.putString("activity", "ski");
                Navigation.findNavController(getView()).
                        popBackStack();
                Navigation.findNavController(getView()).navigate(R.id.navigation_register_hike,
                        skiBundle);
                break;
            case R.id.buttonOtherTrip:
                Toast.makeText(view.getContext(), "Possible future implementation :)", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cardBrowseLibrary:
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_navigation_library);
                break;
            case R.id.cardMapView:
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_navigation_map);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}