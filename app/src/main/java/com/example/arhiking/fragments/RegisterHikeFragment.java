package com.example.arhiking.fragments;

import static com.google.api.ResourceProto.resource;

import android.content.Context;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.room.Room;


import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.HikeActivityDao;
import com.example.arhiking.Helpers;
import com.example.arhiking.Models.HikeActivityGeoPoint;
import com.example.arhiking.Models.Hike_Activity;

import com.example.arhiking.KalmanFilter.KalmanLatLong;

import com.example.arhiking.R;
import com.example.arhiking.databinding.FragmentRegisterHikeBinding;
import com.example.arhiking.viewmodels.RegisterHikeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;


public class RegisterHikeFragment extends Fragment {

    private FragmentRegisterHikeBinding binding;

    private MapView map;
    private IMapController mapController;

    private ImageView imgPlay;
    private ImageView imgStop;
    private ImageView imgPause;
    private ImageView imgDelete;
    private FloatingActionButton btnCamera;
    public com.google.android.gms.maps.model.LatLng locationLatLong;
    List<Location> locationList;

    List<Location> oldLocationList;
    List<Location> noAccuracyLocationList;
    List<Location> inaccurateLocationList;
    List<Location> kalmanNGLocationList;
    long runStartTimeInMillis;

    TextView tvMovementStatus;

    boolean isLogging;

    float currentSpeed = 0.0f; // meters/second

    KalmanLatLong kalmanFilter;

    HikeActivityDao hikeActivityDao;
    RegisterHikeViewModel registerHikeViewModel;
    AppDatabase_v2 db;

    private static final String TAG = "RegisterHikeFragment";
    private int trackingStatus;

    private GeoPoint geoPoint;

    private GeoPoint startingPoint;
    private GeoPoint currentLocation;

    private Location locationFromGeoPoint;

    private List<GeoPoint> trackedPath;
    private String currentActivityType;
    ImageView btnSki;
    ImageView btnHike;
    ImageView btnBike;

    Polyline path;

    Context ctx;
    private MyLocationNewOverlay mLocationOverlay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registerHikeViewModel = new ViewModelProvider(getActivity()).get(RegisterHikeViewModel.class);

        binding = FragmentRegisterHikeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        map = root.findViewById(R.id.mapview);
        path = new Polyline(map, true);

        if (getArguments() != null && getArguments().containsKey("activity")) {
            currentActivityType = getArguments().getString("activity");
        }

        btnHike = binding.btnHike;
        btnSki = binding.btnSki;
        btnBike = binding.btnBike;

        if (currentActivityType == "hike"){
            btnHike.setVisibility(View.VISIBLE);
        }
        if (currentActivityType == "ski"){
            btnSki.setVisibility(View.VISIBLE);
        }
        if (currentActivityType == "bike"){
            btnBike.setVisibility(View.VISIBLE);
        }
      //  startingPoint = registerHikeViewModel.getCurrentLocation().getValue();
        tvMovementStatus = binding.tvMovementStatus;

        db = Room.databaseBuilder(getContext(),
                AppDatabase_v2.class, "database-v2").allowMainThreadQueries().build();

        //fungerer kun på mobil. Hardkoder tilfeldig GeoPoint hvis emulator
        if (!Helpers.isEmulator()) {
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = lm.getBestProvider(criteria, true);
            Location _location = lm.getLastKnownLocation(bestProvider);
            startingPoint = new GeoPoint(_location.getLatitude(), _location.getLongitude());
        }else
            startingPoint = new GeoPoint(68, 10);

        hikeActivityDao = db.hikeActivityDao();

        locationList = new ArrayList<>();
        noAccuracyLocationList = new ArrayList<>();
        oldLocationList = new ArrayList<>();
        inaccurateLocationList = new ArrayList<>();
        kalmanNGLocationList = new ArrayList<>();
        kalmanFilter = new KalmanLatLong(3);



        ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // Inflate the layout for this fragment
        map = binding.registerHikeMapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(13.0);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),map);
        this.mLocationOverlay.enableFollowLocation();
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        mapController.setCenter(startingPoint);

        trackedPath = new ArrayList<>();

        registerHikeViewModel.getMovementStatus().observe(
                getViewLifecycleOwner(), status -> {

                    tvMovementStatus.setText(status);

                });

        btnCamera = binding.floatingBtnCamera;
        btnCamera.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("hikeId", String.valueOf(
                    registerHikeViewModel.getHikeActivityId().getValue()
            ));
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_register_hike_to_cameraFragment2,
                    bundle);
        });

        imgPlay = binding.imageViewPlay;
        imgPause = binding.imageViewPause;

        imgPause.setOnClickListener(((View.OnClickListener) v -> {

            registerHikeViewModel.getTrackingStatus().setValue(4);

            registerHikeViewModel.pauseSensorService();
            Toast.makeText(ctx, "Activity paused", Toast.LENGTH_SHORT).show();
            registerHikeViewModel.getMovementStatus().setValue("Paused");


        }));

        imgDelete = binding.imageViewDelete;
        imgDelete.setOnClickListener(((View.OnClickListener) v -> {

            registerHikeViewModel.getTrackingStatus().setValue(3);
            registerHikeViewModel.stopSensorService();
            registerHikeViewModel.getHikeActivityId().setValue(0L);
            Toast.makeText(ctx, "Activity deleted",
                    Toast.LENGTH_SHORT).show();
            registerHikeViewModel.getMovementStatus().setValue("");
        }));

        registerHikeViewModel.getTrackingStatus().observe(
                getViewLifecycleOwner(), status -> {

                    trackingStatus = status;

                });

        registerHikeViewModel.getCurrentLocation().observe(
                getViewLifecycleOwner(), location -> {

                    currentLocation = location;

                });

        imgPlay.setOnClickListener(v -> {
            Toast.makeText(ctx, "Activity started",
                    Toast.LENGTH_SHORT).show();
            //nullstille status etter lagring eller sletting av tur,
            //eller hvis man trykker på play på nytt
            if (trackingStatus == 1 || trackingStatus == 2
            || trackingStatus == 3)
                registerHikeViewModel.getTrackingStatus().setValue(0);



            if (trackingStatus == 0 || trackingStatus == 3)
             {//hvis play eller pause, ikke opprett ny tur i database
                 //lagrer id og timeRegistered for ny tur.
                Hike_Activity newActivity = new Hike_Activity();
                 Date date = new Date();
                 long timeRegistered = date.getTime();
                 newActivity.timeRegistered = timeRegistered;
                 newActivity.hikeActivityStartingPoint
                         = startingPoint;
                 if (currentActivityType != null){
                     newActivity.category = currentActivityType;
                 }
                long[] id = hikeActivityDao.insertAll(newActivity);
                registerHikeViewModel.getHikeActivityId().
                        setValue(id[0]);

            }

            registerHikeViewModel.getTrackingStatus().setValue(1);


            mapController.setCenter(startingPoint);
            Marker startPosMarker = new Marker(map);
//            startPosMarker.setPosition(startingPoint);
            startPosMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
            startPosMarker.setTitle("Startposisjon");
            startPosMarker.setSubDescription("Turen starter her");
            map.getOverlays().add(startPosMarker);

            registerHikeViewModel.startSensorService();

            registerHikeViewModel.getSensorData().observe(
                    getViewLifecycleOwner(), aFloat -> {
                        //todo... analysere/filtrere data fra sensorer
                        Log.i("Sensor change observed. OrientationX: ",
                                String.valueOf(aFloat[0]));
                        Log.i("Sensor change observed. OrientationY: ",
                                String.valueOf(aFloat[1]));
                        Log.i("Sensor change observed. OrientationZ: ",
                                String.valueOf(aFloat[2]));
                    });

            registerHikeViewModel.getAccelerationData().observe(
                    getViewLifecycleOwner(), accel -> {

                        Log.i("Akselerasjon er: ", String.valueOf(accel));

                    });
            registerHikeViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), curLoc -> {
                locationFromGeoPoint = new Location(LocationManager.GPS_PROVIDER);
                locationFromGeoPoint.setLatitude(curLoc.getLatitude());
                locationFromGeoPoint.setLongitude(curLoc.getLongitude());
                filterAndAddLocation(locationFromGeoPoint);
                HikeActivityGeoPoint hikeActivityGeoPoint = new HikeActivityGeoPoint();
                hikeActivityGeoPoint.geoPoint = curLoc;
                db.HikeActivityGeoPointsDao().insertAll(hikeActivityGeoPoint);
                path.setPoints(trackedPath);
                map.getOverlayManager().add(path);

                map.invalidate();

            });
        });

        imgStop = binding.imageViewStop;
        imgStop.setOnClickListener(v -> {

            registerHikeViewModel.stopSensorService();
            //trackingStatus = 2;
            registerHikeViewModel.getTrackingStatus().setValue(2);
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_register_hike_to_completedHikeFragment);
        });

        return root;
    }


    public void onResume() {
        super.onResume();

        if (map != null)
            map.onResume();
    }

    public void onPause() {
        super.onPause();
        if (map != null)
            map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean filterAndAddLocation(Location location){

        long age = getLocationAge(location);

        if(age > 10 * 1000){ //more than 10 seconds
            Log.d(TAG, "Location is old");
            oldLocationList.add(location);
            return false;
        }

        if(location.getAccuracy() <= 0){
            Log.d(TAG, "Latitidue and longitude values are invalid.");
            noAccuracyLocationList.add(location);
            return false;
        }

        //setAccuracy(newLocation.getAccuracy());
        float horizontalAccuracy = location.getAccuracy();
        if(horizontalAccuracy > 100){
            Log.d(TAG, "Accuracy is too low.");
            inaccurateLocationList.add(location);
            return false;
        }


        /* Kalman Filter */
        float Qvalue;

        long locationTimeInMillis = (long)(location.getElapsedRealtimeNanos() / 1000000);
        long elapsedTimeInMillis = locationTimeInMillis - runStartTimeInMillis;

        if(currentSpeed == 0.0f){
            Qvalue = 3.0f; //3 meters per second
        }else{
            Qvalue = currentSpeed; // meters per second
        }

        kalmanFilter.Process(location.getLatitude(), location.getLongitude(), location.getAccuracy(), elapsedTimeInMillis, Qvalue);
        double predictedLat = kalmanFilter.get_lat();
        double predictedLng = kalmanFilter.get_lng();

        Location predictedLocation = new Location("");//provider name is unecessary
        predictedLocation.setLatitude(predictedLat);//your coords of course
        predictedLocation.setLongitude(predictedLng);
        float predictedDeltaInMeters =  predictedLocation.distanceTo(location);

        if(predictedDeltaInMeters > 60){
            Log.d(TAG, "Kalman Filter detects mal GPS, we should probably remove this from track");
            kalmanFilter.consecutiveRejectCount += 1;

            if(kalmanFilter.consecutiveRejectCount > 3){
                kalmanFilter = new KalmanLatLong(3); //reset Kalman Filter if it rejects more than 3 times in raw.
            }

            kalmanNGLocationList.add(location);
            return false;
        }else{
            kalmanFilter.consecutiveRejectCount = 0;
        }

        /* Notifiy predicted location to UI */
        Intent intent = new Intent("PredictLocation");
        intent.putExtra("location", predictedLocation);
        LocalBroadcastManager.getInstance(getActivity().getApplication()).sendBroadcast(intent);

        Log.d(TAG, "Location quality is good enough.");
        currentSpeed = location.getSpeed();
        geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        locationList.add(location);
        trackedPath.add(geoPoint);


        return true;
    }

    private long getLocationAge(Location newLocation){
        long locationAge;
        if(android.os.Build.VERSION.SDK_INT >= 17) {
            long currentTimeInMilli = (long)(SystemClock.elapsedRealtimeNanos() / 1000000);
            long locationTimeInMilli = (long)(newLocation.getElapsedRealtimeNanos() / 1000000);
            locationAge = currentTimeInMilli - locationTimeInMilli;
        }else{
            locationAge = System.currentTimeMillis() - newLocation.getTime();
        }
        return locationAge;
    }
}
