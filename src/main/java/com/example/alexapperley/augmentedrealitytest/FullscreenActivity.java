package com.example.alexapperley.augmentedrealitytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class FullscreenActivity extends AppCompatActivity implements SensorEventListener{
    GhostView ghostView = null;
    int score = 0;

    private SensorManager sensorManager;
    private Sensor sensor;

    private int SpookInterval = 10000; // 10 seconds by default
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ghostView = (GhostView) findViewById(R.id.ghostView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        View cameraButton = findViewById(R.id.button);
        cameraButton.setOnClickListener(buttonListener);
        mHandler = new Handler();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes and you care.
        // this  must be implemented in your code. but it doesn't need to do anything
    }


    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        ghostView.startDrawCycle();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        ghostView.stopDrawCycle();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //not needed but good practice
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            //I think this just gets a different data type to work with
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            //this allows you to specify axis configuration
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, adjustedRotationMatrix);

            //get rid of quaternions, they destroy brains
            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            //convert to degrees
            float pitch = orientation[0] * -57;
            float roll = orientation[2] * -57;
            float yaw = orientation[1] * -57;

            //moving the object 360 pixels won't even take it off screen
            //so move it a whole lot more
            float[] rotation = {pitch*20, yaw*20, roll};
            ghostView.raw_output.add(rotation);

            //ghostView.invalidate();
        }
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (ghostView.isOnScreen())
            {
                ghostView.randomizeGhost();
                score += 1;
                stopSpookTimer();
                startSpookTimer();
            }
        }
    };

    Runnable spookTimer = new Runnable() {
        @Override
        public void run() {
            Spook();
        }
    };

    public void startSpookTimer() {
        mHandler.postDelayed(spookTimer,SpookInterval);
    }

    public void stopSpookTimer() {
        mHandler.removeCallbacks(spookTimer);
    }

    private void Spook()
    {
        ghostView.Spook();
        Log.wtf("Spook","Boo");
        mHandler.postDelayed(runEnd,100);
    }

    Runnable runEnd = new Runnable() {
        @Override
        public void run() {
            end();
        }
    };

    private void end()
    {
        Intent intent = this.getIntent();
        intent.putExtra("score",score);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        end();
        super.onBackPressed();
    }
}
