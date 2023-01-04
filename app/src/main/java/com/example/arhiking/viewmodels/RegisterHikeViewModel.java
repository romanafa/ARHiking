package com.example.arhiking.viewmodels;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.arhiking.Data.AccelerometerDao;
import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.GeomagneticDao;
import com.example.arhiking.Data.HikeActivityDao;
import com.example.arhiking.Models.AccelerometerData;
import com.example.arhiking.Models.GeomagneticSensorData;
import com.example.arhiking.Models.HikeGeoPoint;
import com.google.android.gms.maps.model.LatLng;


import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterHikeViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    public MutableLiveData<float[]> sensorData; //orientation
    public MutableLiveData<Float> accelerationData;
    public MutableLiveData<String> movementStatus;
    public MutableLiveData<Integer> trackingStatus;
    public MutableLiveData<Long> hikeActivityId;
    public MutableLiveData<GeoPoint> currentLocation;
    public MutableLiveData<Double> highestElevation;
    private LocationProvider _locationProvider;
    private LocationManager _locationManager;
    SensorService sensorService;


    public RegisterHikeViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is register hike fragment");
        sensorService = new SensorService(
                getApplication().getApplicationContext());

    }

    public MutableLiveData<Integer> getTrackingStatus() {
        if (trackingStatus == null) {
            trackingStatus = new MutableLiveData<>();
        }

        return trackingStatus;
        //1 - start, 2 - pause, 3 - stop
    }

    public MutableLiveData<Long> getHikeActivityId() {
        if (hikeActivityId == null) {
            hikeActivityId = new MutableLiveData<>();
        }

        return hikeActivityId;
    }

    public MutableLiveData<String> getMovementStatus
            () {
        if (movementStatus == null) {
            movementStatus = new MutableLiveData<>();
        }

        return movementStatus;
    }

    public MutableLiveData<Double> getHighestElevation() {
        if (highestElevation == null) {
            highestElevation = new MutableLiveData<>();
        }

        return highestElevation;
    }

    public MutableLiveData<float[]> getSensorData() {
        if (sensorData == null) {
            sensorData = new MutableLiveData<float[]>();
        }

        return sensorData;

    }

    public void calculateMovement(float[] accelerometerReading){

        float movementX = accelerometerReading[0];
        float movementY = accelerometerReading[1];

        if (movementX < 0.5 && movementY < 0.5)
            getMovementStatus().setValue("Standing still...");
        if (movementX >  0.5 || movementY > 0.5)
            getMovementStatus().setValue("Walking...");
        if (movementX >  10 || movementY > 10)
            getMovementStatus().setValue("Running...");

    }

    public MutableLiveData<GeoPoint> getCurrentLocation() {
        if (currentLocation == null) {
            currentLocation = new MutableLiveData<>();
        }

        return currentLocation;

    }


    public MutableLiveData<Float> getAccelerationData() {
        if (accelerationData == null) {
            accelerationData = new MutableLiveData<Float>();
        }

        return accelerationData;

    }

    private MutableLiveData<Boolean> startPos;

    public MutableLiveData<Boolean> getStartPos() {
        if (startPos == null) {
            startPos = new MutableLiveData<Boolean>();
        }
        return startPos;
    }

    private MutableLiveData<Boolean> tracking;

    public MutableLiveData<Boolean> getTracking() {
        if (tracking == null) {
            tracking = new MutableLiveData<Boolean>();
        }
        return tracking;
    }

    public LiveData<String> getText() {

        return mText;
    }


    public void pauseSensorService() {
        //getTrackingStatus().setValue(4);
        sensorService.stopListening();

    }

    public void startSensorService() {
          //  getTrackingStatus().setValue(1);
            sensorService.listenToSensors();

    }
    public void stopSensorService() {
        //getTrackingStatus().setValue(3);
        sensorService.stopListening();
    }

    public class SensorService extends AppCompatActivity implements SensorEventListener {

        private static final int REQUEST_CODE = 999;
        LocationManager locationManager;
        LatLng latLng;
     /*   private boolean startPos = false;
        private boolean tracking = false;*/
        GeoPoint startPoint;
        /*GeoPoint currentPosition;*/
        private List<GeoPoint> trackedPath = new ArrayList<>();

        private final SensorManager sensorManager;
        private final Sensor geoMagneticSensor;
        private final Sensor accelerometerSensor;
        private final Sensor gyroscopeSensor;
        private final float[] accelerometerReading = new float[3];
        private final float[] magnetometerReading = new float[3];

        private final float[] rotationMatrix = new float[9];
        private final float[] orientationAngles = new float[3];

        Context _context;

        HikeActivityDao hikeActivityDao;

        AppDatabase_v2 db;

        public SensorService(Context context) {

            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            geoMagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            _context = context;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            setUpLocationManager();

            db = Room.databaseBuilder(context,
            AppDatabase_v2.class, "database-v2").allowMainThreadQueries().build();

            hikeActivityDao = db.hikeActivityDao();

        }


        private void setUpLocationManager() {
            getHighestElevation().setValue(0D);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            Log.i("location updated: ",
                                    String.valueOf(location.getAccuracy()));
                            latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            Log.i("location information: ",
                                    getLocationInfo(latLng));
                         //   saveToDatabase(latLng);

                            /*if (startPos.getValue() == false){*/

                                getCurrentLocation().setValue(new GeoPoint(latLng.latitude, latLng.longitude));


                            _locationManager = (LocationManager) _context
                                    .getSystemService(LOCATION_SERVICE);

                            _locationProvider = _locationManager
                                    .getProvider(LocationManager.GPS_PROVIDER);
                            location = _locationManager.getLastKnownLocation
                                    (LocationManager.GPS_PROVIDER);

                            _locationProvider.supportsAltitude();
                            location.hasAltitude();

                            double currentAltitude = location.getAltitude();

                            double highestElevation =
                                    getHighestElevation().getValue();
                                //update highest elevation:
                            if (currentAltitude > highestElevation)
                                getHighestElevation().setValue(currentAltitude);


                              /*  startPos.setValue(true);
                                trackingStatus.setValue(1);*/

                                /*trackedPath.add(startPoint);*/
                                saveToDatabase(latLng);
                          /*  }*/

                           /* if (trackingStatus.getValue() == 1){*/
                                currentLocation.setValue( new GeoPoint(latLng.latitude, latLng.longitude));
                                /*trackedPath.add(currentLocation.getValue());*/
                                saveToDatabase(latLng);
                           /* }*/

                        }
                    });
        }

        private void saveToDatabase(LatLng latLng) {
            if (getHikeActivityId().getValue() != null) {
                GeoPoint point = new GeoPoint(latLng.latitude, latLng.longitude);
                HikeGeoPoint hikeGeoPoint = new HikeGeoPoint();
                hikeGeoPoint.geoPoint = point;
                hikeGeoPoint.hikeId = getHikeActivityId().getValue();//todo testes
                db.HikeGeoPointsDao().insertAll(hikeGeoPoint);
            }
        }

        private String getLocationInfo(LatLng latLng) {
            Geocoder geoCoder = new Geocoder(_context, Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(latLng.latitude,
                        latLng.longitude, 1);
                Address adr = addresses.get(0);
                return adr.getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
///\todo get last Known Location
            return "";
        }


        public void stopListening() {
            sensorManager.unregisterListener(this);

        }

         public void calculateAcceleration(float[] values){

            Float acceleration = (float)Math.sqrt(
                    values[0]*values[0]+values[1]*values[1]+values[2]*
                    values[2]);

            if (acceleration > 0.5f)  //filter
             getAccelerationData().setValue(acceleration);


         }

        public void updateOrientationAngles() {
            // Update rotation matrix, which is needed to update orientation angles.
            SensorManager.getRotationMatrix(rotationMatrix, null,
                    accelerometerReading, magnetometerReading);
            // "rotationMatrix" now has up-to-date information.

            float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
            getSensorData().setValue(orientation);
            // "orientationAngles" now has up-to-date information.
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            AccelerometerDao accelerometerDao = db.accelerometerDao();
            GeomagneticDao geomagneticDao = db.geomagneticDao();

            Date date = new Date();

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(sensorEvent.values, 0, accelerometerReading,
                        0, accelerometerReading.length);
                Log.i("acceleromater sensor changed. value_x: ", String.valueOf(accelerometerReading[0]));
                Log.i("acceleromater sensor changed. value_y: ", String.valueOf(accelerometerReading[1]));
                Log.i("acceleromater sensor changed. value_z: ", String.valueOf(accelerometerReading[2]));

                //calculate movement
                calculateMovement(accelerometerReading);

                AccelerometerData accelerometerData = new AccelerometerData();
                accelerometerData.timeRegistered = date.getTime();
                accelerometerData.hike_activity_id = getHikeActivityId().getValue();
                accelerometerData.xValue = accelerometerReading[0];
                accelerometerData.yValue = accelerometerReading[1];
                accelerometerData.zValue = accelerometerReading[2];

                accelerometerDao.insertAll(accelerometerData);

                calculateAcceleration(sensorEvent.values);


            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(sensorEvent.values, 0, magnetometerReading,
                        0, magnetometerReading.length);
                Log.i("magnetometer sensor changed. value_x: ", String.valueOf(magnetometerReading[0]));
                Log.i("magnetometer sensor changed. value_y: ", String.valueOf(magnetometerReading[1]));
                Log.i("magnetometer sensor changed. value_z: ", String.valueOf(magnetometerReading[2]));

                GeomagneticSensorData geomagneticData = new GeomagneticSensorData();
                geomagneticData.timeRegistered = date.getTime();
                if (getHikeActivityId().getValue() != null)
                    geomagneticData.hike_activity_id = getHikeActivityId().getValue();
                geomagneticData.xValue = magnetometerReading[0];
                geomagneticData.yValue = magnetometerReading[1];
                geomagneticData.zValue = magnetometerReading[2];

                geomagneticDao.insertAll(geomagneticData);

            }

            calculateOrientationFromSensors(magnetometerReading, accelerometerReading);

        }

        public void listenToSensors() {
            if (geoMagneticSensor != null) {
                sensorManager.registerListener(this, geoMagneticSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            if (gyroscopeSensor != null) {
                sensorManager.registerListener(this, gyroscopeSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

        }

        public void calculateOrientationFromSensors(float[] magnetometerValues, float[] acceleromaterValues) {

            final float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrix(rotationMatrix, null,
                    acceleromaterValues, magnetometerValues);

            final float[] orientationAngles = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            updateOrientationAngles();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    }

