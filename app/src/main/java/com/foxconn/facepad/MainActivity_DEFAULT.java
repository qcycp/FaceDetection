package com.foxconn.facepad;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity_DEFAULT extends AppCompatActivity {
    private final static String TAG = MainActivity_DEFAULT.class.getCanonicalName();
    private static final int REQUEST_PERMISSION = 1;

    static {
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_default);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadCascadeFile(R.raw.haarcascade_frontalface_alt, "haarcascade_frontalface_alt.xml");
        loadCascadeFile(R.raw.haarcascade_frontalface_default, "haarcascade_frontalface_default.xml");
        loadCascadeFile(R.raw.haarcascade_eye, "haarcascade_eye.xml");
        loadCascadeFile(R.raw.opencv_face_detector, "opencv_face_detector.pbtxt");
        loadCascadeFile(R.raw.opencv_face_detector_uint8, "opencv_face_detector_uint8.pb");

        if (hasPermission()) {
            if (null == savedInstanceState) {
                setFragment();
            }
        } else {
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setFragment();
            } else {
                requestPermission();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity_DEFAULT.this, "Camera permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
    }

    private void loadCascadeFile(int id, String path) {
        try {
            InputStream is = getResources().openRawResource(id);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, path);
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    private void setFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, CameraFragment_DEFAULT.newInstance())
                .commitNowAllowingStateLoss();
    }
}
