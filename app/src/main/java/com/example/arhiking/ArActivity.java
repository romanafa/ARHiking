package com.example.arhiking;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBindings;

import com.example.arhiking.Models.Hike_Activity;
import com.example.arhiking.fragments.MapFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.osmdroid.util.GeoPoint;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;


public class ArActivity extends AppCompatActivity {
    private static final String TAG = "ARCoreCamera";
    private static final double MIN_OPENGL_VERSION = 3.0;


    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private Snackbar loadingMessageSnackbar = null;

    private ArSceneView arSceneView;

    // Renderables for this example
//    private ModelRenderable andyRenderable;
    private ViewRenderable exampleLayoutRenderable;
    private ViewRenderable exampleLayoutRenderable2;
    private LocationScene locationScene;
    private Button btnBackToMap;
    private GeoPoint startingPoint;
    private String startingPointName;
    private String difficulty;
    private String distance;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        startingPoint = (GeoPoint) intent.getSerializableExtra("" +
                "startingPoint");
        startingPointName = intent.getStringExtra("" +
                "startingPointName");

        distance = intent.getStringExtra("distance");
        difficulty = intent.getStringExtra("difficulty");
        category = intent.getStringExtra("category");


        setContentView(R.layout.ar_activity);
        arSceneView = findViewById(R.id.ar_scene_view);

        btnBackToMap = findViewById(R.id.btnBackToMap);
        btnBackToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ArActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        //checkIsSupportedDeviceOrFinish(this);

        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(this, R.layout.example_layout)
                        .build();
        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> exampleLayout2 =
                ViewRenderable.builder()
                        .setView(this, R.layout.example_layout)
                        .build();

        CompletableFuture.allOf(
                        exampleLayout,
                        exampleLayout2)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                Helpers.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                exampleLayoutRenderable = exampleLayout.get();
                                exampleLayoutRenderable2 = exampleLayout2.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                Helpers.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

        Log.d("ARCoreCamera", "Updatelistener");
        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView.getScene().addOnUpdateListener(
                frameTime -> {
                    if (!hasFinishedLoading) {
                        return;
                    }

                    if (locationScene == null) {
                        // If our locationScene object hasn't been setup yet, this is a good time to do it
                        // We know that here, the AR components have been initiated.
                        locationScene = new LocationScene(this, this, arSceneView);

                        // Now lets create our location markers.
                        // First, a layout
                        LocationMarker layoutLocationMarker = new LocationMarker(17.481728, 68.4212189, getExampleView(exampleLayoutRenderable));

                        LocationMarker layoutLocationMarker2 = new LocationMarker(
                                startingPoint.getLongitude(),
                                startingPoint.getLatitude(),
                                getExampleView(exampleLayoutRenderable2)
                        );

                        // An example "onRender" event, called every frame
                        // Updates the layout with the markers distance
                        layoutLocationMarker.setRenderEvent(new LocationNodeRender() {
                            @Override
                            public void render(LocationNode node) {
                                View eView = exampleLayoutRenderable.getView();

                                TextView distanceTextView = eView.findViewById(R.id.textView2);
                                distanceTextView.setText((node.getDistance())/1000 + getString(R.string.ar_distance_to_hike));
                                TextView hikeDistanceTextView = eView.findViewById(R.id.textViewDistance);
                                hikeDistanceTextView.setText(getString(R.string.ar_hike_distance) + distance);
                                TextView difficultyTextView = eView.findViewById(R.id.textViewDifficulty);
                                difficultyTextView.setText(getString(R.string.ar_difficulty) + difficulty);
                                TextView categoryTextView = eView.findViewById(R.id.textViewCategory);
                                categoryTextView.setText(getString(R.string.ar_category) + category);
                                TextView nameView = eView.findViewById(R.id.textView1);
                                nameView.setText(startingPointName);

                            }
                        });

                        //Adding marker
                        locationScene.mLocationMarkers.add(layoutLocationMarker);
                        //locationScene.mLocationMarkers.add(layoutLocationMarker2);

                        // Adding a simple location marker of a 3D model
//                        locationScene.mLocationMarkers.add(new LocationMarker(-0.119677, 51.478494, getAndy()));
                    }

                    Frame frame = arSceneView.getArFrame();
                    if (frame == null) {
                        return;
                    }

                    if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                        return;
                    }

                    if (locationScene != null) {
                        locationScene.processFrame(frame);
                    }

                    if (loadingMessageSnackbar != null) {
                        for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                            if (plane.getTrackingState() == TrackingState.TRACKING) {
                                hideLoadingMessage();
                            }
                        }
                    }
                });


        Log.d("ARCoreCamera", "Request permission");

        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(this);

    }

    @SuppressLint("ClickableViewAccessibility")
    private Node getExampleView(ViewRenderable renderable) {
        Log.d("ARCoreCamera", "getExampleView");
        Node base = new Node();
        base.setRenderable(renderable);
        Context c = this;
        // Add  listeners etc here
        View eView = renderable.getView();
        eView.setOnTouchListener((v, event) -> {
            //Toast.makeText(c, "Location marker touched.", Toast.LENGTH_LONG).show();
            return false;
        });

        return base;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            Log.d("ARCoreCamera", "resume locationscene");
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Log.d("ARCoreCamera", "demoutils create session");
                Session session = Helpers.createArSession(this, installRequested);
                if (session == null) {
                    Log.d("ARCoreCamera", "session==null");
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    Log.d("ARCoreCamera", "setupSession");
                    arSceneView.setupSession(session);
                    Log.d("ARCoreCamera", "setupSession done");
                }
            } catch (UnavailableException e) {
                Log.d("ARCoreCamera", "exception in demoutils");
                Helpers.handleSessionException(this, e);
            }
        }

        try {
            Log.d("ARCoreCamera", "resume arsceneview");
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Log.d("ARCoreCamera", "cameranotaveilable exception");
            Helpers.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (locationScene != null) {
            Log.d(TAG, "onPause = null");
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                Log.d(TAG, "permissionDenied");
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                                this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ArActivity.this.findViewById(android.R.id.content),
                        "loading",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }


    public static boolean checkIsSupportedDeviceOrFinish(final MainActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.d(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.d(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }


}