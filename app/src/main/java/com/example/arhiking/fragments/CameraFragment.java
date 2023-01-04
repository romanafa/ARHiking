package com.example.arhiking.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.arhiking.Helpers;
import com.example.arhiking.R;
import com.example.arhiking.databinding.FragmentCameraBinding;
import com.example.arhiking.databinding.FragmentRegisterHikeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE = 999;

    private FloatingActionButton btnTakePictue;

    ActivityResultLauncher<Intent> saveImage;

    public final String APP_TAG = "ArHiking";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    //public String photoFileName = "arhiking.jpg";
    File photoFile;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentCameraBinding binding;

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};
    TextureView cameraView;
    private String cameraId;
    private Size imageDimension;
    protected CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession cameraCaptureSessions;
    private Handler mBackgroundHandler;
//    private BufferedInputStream imageReader;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    TextureView.SurfaceTextureListener cameraListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    private void openCamera() {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics chts = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = chts.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            /*if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }*/
            manager.openCamera(cameraId, statecallback  , null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback statecallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreview() {
        try {

            SurfaceTexture texture = cameraView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSess) {
                    if(cameraDevice == null)
                        return;

                    cameraCaptureSessions = cameraCaptureSess;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getContext(),"Configuration changed", Toast.LENGTH_LONG).show();
                }
            }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null)
            return;
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
       // askForPermission();


        saveImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
                        Toast.makeText(CameraFragment.this.getContext(), "Image captured", Toast.LENGTH_SHORT)
                                .show();
                    }
                });



        cameraView = binding.cameraView;
        cameraView.setSurfaceTextureListener(cameraListener);

        btnTakePictue = binding.floatingBtnCamera;
        btnTakePictue.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a File reference for future access
            //photoFile = getPhotoFileUri(photoFileName);
        //kilde: https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#setup-fileprovider
            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher

            String hikeId = getArguments().getString("hikeId");


            if (Helpers.isEmulator()) {
                File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), hikeId + ":" + System.currentTimeMillis() + ".png");
                //filomåde for lagring av bilder:
                Log.i("filområde: ", getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString());


                Uri bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, bmpUri);
            }else {
                Log.i("intern lagring: ", "/storage/self/primary/Android/data/com.example.arhiking/files/Pictures/");
                // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
                //ContextWrapper cw = new ContextWrapper(getContext());
                //File file = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File("/storage/self/primary/Android/data/com.example.arhiking/files/Pictures/", hikeId + ":" + System.currentTimeMillis() + ".png");

                Uri bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, bmpUri);
            }
            saveImage.launch(intent);
            Navigation.findNavController(getView()).navigate(R.id.action_cameraFragment_to_navigation_register_hike2);

        });

        return root;
    }
/*
    public File getPhotoFileUri(String fileName) {

    // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
*/

}