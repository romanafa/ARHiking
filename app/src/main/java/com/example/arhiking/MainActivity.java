package com.example.arhiking;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.HikeDao;
import com.example.arhiking.Data.UserDao;
import com.example.arhiking.Models.Hike;
import com.example.arhiking.Models.HikesWithHikesActivities;
import com.example.arhiking.Models.User;
import com.example.arhiking.Models.UserWithHikes;
import com.example.arhiking.databinding.FragmentMapBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.arhiking.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;

import java.util.List;
import java.util.Locale;

import uk.co.appoly.arcorelocation.LocationScene;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_CODE = 999;
    LocationManager locationManager;
    private ArSceneView arSceneView;
    private ViewRenderable exampleRenderableLayout;
    private ViewRenderable exampleRenderableLayout2
            ;

    private MapView map;
    private IMapController mapController;

    private static final String TAG = "MapFragment";

    private static final int PERMISSION_REQUEST_CODE = 1;

    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private Snackbar loadingMessageSnackbar;

    private LocationScene locationScene;
    private LocationManager lm;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        askForPermission();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_library,
                R.id.navigation_register_hike)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    private void askForPermission() {
        if (!permissionsGranted())
            requestPermissionsForLocation();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Log.i("permission info", "permission granted");
                    Toast.makeText(getApplicationContext(),
                            "Starting tracking...", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("permission info", "permission not granted");
                    Toast.makeText(getApplicationContext(),
                            "Premission not granted", Toast.LENGTH_SHORT).show();
                }
            } break;
            default:
                super.onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        }
    }



    private void requestPermissionsForLocation() {
        requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA}, REQUEST_CODE

        );
    }

    private boolean permissionsGranted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        return true;
    }



}