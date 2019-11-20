package com.foxconn.liveness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.core.MatOfRect;

public class BoundingBoxView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = BoundingBoxView.class.getCanonicalName();
    protected SurfaceHolder mSurfaceHolder;

    private Paint mPaint;

    private boolean mIsCreated;

    public BoundingBoxView(Context context, AttributeSet attrs) {
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

    public void setResults(MatOfRect detRets) {
        if (!mIsCreated) {
            return;
        }

        Canvas canvas = mSurfaceHolder.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        canvas.drawColor(Color.TRANSPARENT);

        org.opencv.core.Rect[] facesArray = detRets.toArray();
        Log.d(TAG, "face number: " + facesArray.length);
        for (int i = 0; i < facesArray.length; i++) {
            Rect rect = new Rect((int)facesArray[i].tl().x, (int)facesArray[i].tl().y, (int)facesArray[i].br().x, (int)facesArray[i].br().y);
            canvas.drawRect(rect, mPaint);
        }

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
}
