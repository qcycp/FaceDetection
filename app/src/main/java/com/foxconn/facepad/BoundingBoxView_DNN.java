package com.foxconn.facepad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.core.Mat;

public class BoundingBoxView_DNN extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = BoundingBoxView.class.getCanonicalName();
    protected SurfaceHolder mSurfaceHolder;

    private Paint mPaint;

    private boolean mIsCreated;
    final double THRESHOLD = 0.7;
    private int screen_width;
    private int screen_height;

    public BoundingBoxView_DNN(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);

        mPaint = new Paint();
        //mPaint.setAntiAlias(true);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(10f);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mIsCreated = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsCreated = false;
    }

    public void setResults(Mat detections) {
        if (!mIsCreated) {
            return;
        }

        Canvas canvas = mSurfaceHolder.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        canvas.drawColor(Color.TRANSPARENT);

        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence > THRESHOLD) {
                int classId = (int)detections.get(i, 1)[0];
                int left   = (int)(detections.get(i, 3)[0] * 1080);
                int top    = (int)(detections.get(i, 4)[0] * 1440);
                int right  = (int)(detections.get(i, 5)[0] * 1080);
                int bottom = (int)(detections.get(i, 6)[0] * 1440);
                // Draw rectangle around detected object.
                Rect rect = new Rect((int)left, (int)top, (int)right, (int)bottom);
                canvas.drawRect(rect, mPaint);
            }
        }

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
}
