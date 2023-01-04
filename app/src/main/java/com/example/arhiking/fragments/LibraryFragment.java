package com.example.arhiking.fragments;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.arhiking.Adapters.HikesAdapter;
import com.example.arhiking.Adapters.LibraryAdapter;
import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.HikeActivityDao;
import com.example.arhiking.Helpers;
import com.example.arhiking.Models.Hike_Activity;
import com.example.arhiking.R;
import com.example.arhiking.Tour;
import com.example.arhiking.databinding.FragmentLibraryBinding;
import com.example.arhiking.viewmodels.LibraryViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;
    private RecyclerView mRecyclerView;
    private LibraryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    AppDatabase_v2 db;
    HikeActivityDao hikeActivityDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LibraryViewModel notificationsViewModel =
                new ViewModelProvider(this).get(LibraryViewModel.class);

        db = Room.databaseBuilder(getContext(),
                AppDatabase_v2.class, "database-v2").allowMainThreadQueries().build();

        hikeActivityDao = db.hikeActivityDao();

        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mRecyclerView = root.findViewById(R.id.hikeLibraryRecyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new LibraryAdapter(getHikes());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.library_menu, menu);

                MenuItem searchItem = menu.findItem(R.id.action_search_hike);
                SearchView searchView = (SearchView) searchItem.getActionView();

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private ArrayList<Tour> getHikes() {
        ArrayList<Tour> hikes = new ArrayList<>();


        List<Hike_Activity> activities = hikeActivityDao.getAll();
        for (Hike_Activity activity : activities) {

            Tour tur = new Tour();
            tur.name = activity.hikeActivityName;
            tur.hikeLength = String.valueOf(activity.hikeDistance);
            tur.hikeCategory = activity.category;
            tur.hikeElevation = String.valueOf(activity.highestElevation);
            tur.hikeDifficulty = activity.difficulty;
            tur.hikeTime = String.valueOf(activity.duration);
            long hikeId = activity.hikeActivityId;

            int poster = 0;
            int img1 = 0;
            int img2 = 0;
            int img3 = 0;
            //få tak i bilder som hører til hikeId
            String path;
            if (Helpers.isEmulator()) {
                path = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
            }else {
                path = "/storage/self/primary/Android/data/com.example.arhiking/files/Pictures/";
            }
            File f = new File(path);
            File[] files = f.listFiles();


            for (File file : files) {
                String name = file.toString().split(":")[0];
                String _name = name.split("/")[9];
                Uri bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", file);
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), bmpUri);

                if (_name.equals(String.valueOf(hikeId))){
                    if (poster == 0) {
                        poster = 1;

                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            tur.btmpPoster = bitmap;

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    else if (img1 == 0) {
                        img1 = 1;

                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            tur.btmpImage1 = bitmap;

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    else if (img2 == 0) {
                        img2 = 1;

                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            tur.btmpImage2 = bitmap;

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    else if (img3 == 0) {
                        img3 = 1;

                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            tur.btmpImage3 = bitmap;

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }

            hikes.add(tur);
        }


        //dummy data for RecyclerView
        Tour bakkanosiHike = new Tour();
        bakkanosiHike.poster = R.drawable.hike_1;
        bakkanosiHike.name = "Bakkanosi";
        bakkanosiHike.hikeLocation = "Jordalen, Voss, Norway";
        bakkanosiHike.hikeLength = "20km";
        bakkanosiHike.hikeAscent = "580 - 1398m";
        bakkanosiHike.hikeTime = "8h";
        bakkanosiHike.hikeDate = "23.07.2022";
        bakkanosiHike.hikeDifficulty = "Hard";
        bakkanosiHike.hikeElevation = "1398m";
        bakkanosiHike.image1 = R.drawable.hike_1;
        bakkanosiHike.image2 = R.drawable.hike_1;
        bakkanosiHike.image3 = R.drawable.hike_1;
        bakkanosiHike.rating = 5f;
        hikes.add(bakkanosiHike);

        Tour prestHike = new Tour();
        prestHike.poster = R.drawable.hike_3;
        prestHike.name = "Prest";
        prestHike.hikeLocation = "Aurland, Norway";
        prestHike.hikeLength = "6.5km";
        prestHike.hikeAscent = "793 - 1478m";
        prestHike.hikeTime = "3h";
        prestHike.hikeDate = "04.07.2022";
        prestHike.hikeDifficulty = "Moderate";
        prestHike.hikeElevation = "1478m";
        prestHike.image1 = R.drawable.hike_3;
        prestHike.image2 = R.drawable.hike_3;
        prestHike.image3 = R.drawable.hike_3;
        prestHike.rating = 4f;
        hikes.add(prestHike);

        Tour litlefjelletHike = new Tour();
        litlefjelletHike.poster = R.drawable.hike_2;
        litlefjelletHike.name = "Litlefjellet";
        litlefjelletHike.hikeLocation = "Romsdalen og Eikesdalen, Norway";
        litlefjelletHike.hikeLength = "1.7km";
        litlefjelletHike.hikeAscent = "540 - 790m";
        litlefjelletHike.hikeTime = "30min";
        litlefjelletHike.hikeDate = "21.06.2022";
        litlefjelletHike.hikeDifficulty = "Moderate";
        litlefjelletHike.hikeElevation = "790m";
        litlefjelletHike.image1 = R.drawable.hike_2;
        litlefjelletHike.image2 = R.drawable.hike_2;
        litlefjelletHike.image3 = R.drawable.hike_2;
        litlefjelletHike.rating = 4.5f;
        hikes.add(litlefjelletHike);

        Tour stalheimsnipaHike = new Tour();
        stalheimsnipaHike.poster = R.drawable.hike_4;
        stalheimsnipaHike.name = "Stalheimsnipa";
        stalheimsnipaHike.hikeLocation = "Stalheim, Voss, Norway";
        stalheimsnipaHike.hikeLength = "3.5km";
        stalheimsnipaHike.hikeAscent = "115 - 844m";
        stalheimsnipaHike.hikeTime = "2.5h";
        stalheimsnipaHike.hikeDate = "28.05.2022";
        stalheimsnipaHike.hikeDifficulty = "Hard";
        stalheimsnipaHike.hikeElevation = "844m";
        stalheimsnipaHike.image1 = R.drawable.hike_4;
        stalheimsnipaHike.image2 = R.drawable.hike_4;
        stalheimsnipaHike.image3 = R.drawable.hike_4;
        stalheimsnipaHike.rating = 4f;
        hikes.add(stalheimsnipaHike);

        return hikes;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}