package com.foxconn.facepad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FDdemoActivity extends AppCompatActivity {
    private final static String TAG = FDdemoActivity.class.getCanonicalName();
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private ImageView ImageView_avatar;
    private CascadeClassifier mJavaDetector;
    //private DetectionBasedTracker mNativeDetector;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fddemo);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        ImageView_avatar = (ImageView) findViewById(R.id.avatar);
        tv.setText(stringFromJNI());

        //convertGray();
        loadCascadeFile();
        faceDetection();
        //playRtspStreaming();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void convertGray() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        Mat src = new Mat();
        Mat temp = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, temp, Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(dst, bitmap);
        ImageView_avatar.setImageBitmap(bitmap);
    }

    private void playRtspStreaming() {
        VideoCapture cap = new VideoCapture("rtsp://172.18.227.58/0824.mp4");
        if (cap.isOpened()) {
            Log.d(TAG, "rtsp streaming is oepned ok");
        } else {
            Log.d(TAG, "rtsp streaming is oepned failed");
        }
        Mat frame = new Mat();
        while (true) {
            boolean status = cap.read(frame);
            if (status) {
                Log.d(TAG, "read frame ok");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fd);
                Utils.matToBitmap(frame, bitmap);
                ImageView_avatar.setImageBitmap(bitmap);
            } else {
                Log.d(TAG, "read frame failed");
            }
        }
    }

    private void loadCascadeFile() {
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
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

    private void faceDetection() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fd);
        Mat src = new Mat();
        Mat temp = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, temp, Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2GRAY);

        File mCascadeFile = new File(getDir("cascade", Context.MODE_PRIVATE),
                "haarcascade_frontalface_alt.xml");
        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        if (mJavaDetector.empty()) {
            Log.e(TAG, "Failed to load cascade classifier");
            mJavaDetector = null;
            return;
        }
        MatOfRect faces = new MatOfRect();
        long startTime = System.currentTimeMillis();
        mJavaDetector.detectMultiScale(dst, faces, 1.3, 5, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                new Size(35, 35), new Size());
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Time cost: " + String.valueOf((endTime - startTime) / 1000f) + " sec");

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(src, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }
        Utils.matToBitmap(src, bitmap);
        ImageView_avatar.setImageBitmap(bitmap);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
