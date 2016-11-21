package com.example.alexapperley.augmentedrealitytest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.alexapperley.augmentedrealitytest.R;

/**
 * Created by Alex Apperley on 2016-10-27.
 */

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    private Camera camera = null;
    private boolean inView = false;

    public MainGameView(Context context) {
        super(context);
        init(context);
    }

    public MainGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(getHolder());
            camera.startPreview();
            inView = true;
        }
        catch(Exception ex) {
            Log.e("CameraError","Error while Creating Surface",ex);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (inView) {
            camera.stopPreview();
        }
        camera.release();
        camera = null;
        inView = false;
    }
}
