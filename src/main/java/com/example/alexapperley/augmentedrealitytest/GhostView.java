package com.example.alexapperley.augmentedrealitytest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Alex Apperley on 2016-11-06.
 */
public class GhostView extends SurfaceView {
    private Bitmap ghost;
    private Paint basicPaint;

    //these are the offset of the current ghost.
    //should be randomized within a range to spawn a new one
    public final int[] ghostSize = {500,500};
    public float ghostX = 0;
    public float ghostY = 0;
    public float ghostZ = 0;
    public boolean jumpScare = false;

    private float[] smoothed_coords = {0,0,0};

    //list of arrays to contain raw sensor output
    ArrayList<float[]> raw_output = new ArrayList<>();

    public GhostView(Context context)
    {
        super(context);
        init(context);
    }

    public GhostView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public GhostView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        mHandler = new Handler();
        ghost = BitmapFactory.decodeResource(getResources(), R.drawable.spooky_ghost);
        ghost = Bitmap.createScaledBitmap(ghost, ghostSize[0], ghostSize[1], false);
        basicPaint = new Paint();
        setWillNotDraw(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!jumpScare)
        {
        //smooth the movement
        //might be better to do pre-draw but it probably doesn't matter
        smoothed_coords[0] = 0;
        smoothed_coords[1] = 0;
        smoothed_coords[2] = 0;
        float[][] raw_array = raw_output.toArray(new float[raw_output.size()][3]);

        // add 'em up
        for (int i = 0; i < raw_array.length; i++) {
            smoothed_coords[0] += raw_array[i][0];
            smoothed_coords[1] += raw_array[i][1];
            smoothed_coords[2] += raw_array[i][2];
        }

        //devide 'em down
        smoothed_coords[0] /= raw_array.length;
        smoothed_coords[1] /= raw_array.length;
        smoothed_coords[2] /= raw_array.length;

        //clear raw sensor output
        raw_output.clear();

        //apply drawing
        canvas.rotate(-smoothed_coords[2] + ghostZ, smoothed_coords[0] + ghostX, smoothed_coords[1] + ghostY);
        canvas.drawBitmap(ghost, smoothed_coords[0] + ghostX, smoothed_coords[1] + ghostY, basicPaint);
        canvas.rotate(smoothed_coords[2] + ghostZ, smoothed_coords[0] + ghostX, smoothed_coords[1] + ghostY);

        //Log.d("Drawing","Cute Ghost - " + smoothed_coords[0] + "," + smoothed_coords[1] + "," + smoothed_coords[2]);
        }
        else
        {
            canvas.drawBitmap(ghost, 0, 0, basicPaint);
        }
    }

    //Updating Mechanics
    private int drawInterval = 33; // ~30 frames per second by default, sorry pc master race
    private Handler mHandler;

    Runnable runnableDraw = new Runnable() {
        @Override
        public void run() {
            try {
                reDraw();
            } finally {
                // this code always runs even if
                // your update method throws an exception
                mHandler.postDelayed(runnableDraw, drawInterval);
            }
        }
    };

    public void startDrawCycle() {
        runnableDraw.run();
    }

    public void stopDrawCycle() {
        mHandler.removeCallbacks(runnableDraw);
    }

    public void reDraw()
    {
        this.invalidate();
    }

    public boolean isOnScreen()
    {
        if(smoothed_coords[0] +  ghostX >= -ghostSize[0] && smoothed_coords[0] +  ghostX <= this.getWidth() + ghostSize[0])
        {
            if(smoothed_coords[1] +  ghostY >= -ghostSize[1]&& smoothed_coords[1] +  ghostY <= this.getWidth() + ghostSize[1])
            {
                return true;
            }
        }
        return false;
    }

    public void randomizeGhost()
    {
        int xRange = 2500;
        int yRange = 750;
        ghostX = ThreadLocalRandom.current().nextInt(-xRange, xRange + 1);
        ghostY = ThreadLocalRandom.current().nextInt(-yRange, yRange + 1);
        if(isOnScreen())
        {
            randomizeGhost();
        }
    }

    //JUMPSCARE
    public void Spook()
    {
        jumpScare = true;
        ghost = Bitmap.createScaledBitmap(ghost, ghostSize[0]*2, ghostSize[1]*2, false);
        reDraw();
        stopDrawCycle();
    }
}
