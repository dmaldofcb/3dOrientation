package com.ebookfrenzy.a3dorientation;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Calibrate extends AppCompatActivity implements View.OnClickListener ,SensorEventListener {

    private Button calibrate;
    private SensorManager SensorManager_;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private float accelOffsetX;
    private float accelOffsetY;
    private float accelOffsetZ;
    private float gyroOffsetX;
    private float gyroOffsetY;
    private float gyroOffsetZ;

    private int accelCount = 0;
    private int gyroCount = 0;
    private int samples = 50;
    private Boolean pressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        calibrate = (Button) findViewById(R.id.start_cal);

        SensorManager_= (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = SensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = SensorManager_.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        calibrate.setOnClickListener(this);
        setTitle("Calibrate");
       // Intent cal = getIntent();
       // int number = inte
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            Float accX = event.values[0];
            Float accY = event.values[1];
            Float accZ = event.values[2];

            if(accelCount < 50){
                accelOffsetX += accX;
                accelOffsetY += accY;
                accelOffsetZ += accZ;
            }



            accelCount++;

        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Float gyroX = event.values[0];
            Float gyroY = event.values[1];
            Float gyroZ = event.values[2];

            if(gyroCount < 50){
                gyroOffsetX += gyroX;
                gyroOffsetY += gyroY;
                gyroOffsetZ += gyroZ;
            }


            Log.d("CALIBRATE:", "-------------");
            Log.d("Samples", "x-axis" + Float.toString(gyroOffsetX));
            Log.d("Samples", "y-axis" + Float.toString(gyroOffsetY));
            Log.d("Samples", "z-axis" + Float.toString(gyroOffsetZ));
            Log.d("LEFT", "Size so far " + gyroCount);
            Log.d("SAMPLES:", "-------------");

            gyroCount++;
        }

        if (accelCount >= samples && gyroCount >= samples) {
            float x_cal = (float) (0.0 - (accelOffsetX/samples));
            float y_cal = (float) (0.0 - (accelOffsetY/samples));
            float z_cal = (SensorManager.GRAVITY_EARTH - (accelOffsetZ/samples));

            float x_gy = (float) (0.0 - (gyroOffsetX)/samples);
            float y_gy = (float) (0.0 - (gyroOffsetY)/samples);
            float z_gy = (float) (0.0 - (gyroOffsetZ)/samples);

            Log.d("FINAL:", "-------------");
            Log.d("Samples", "x-axis " + Float.toString(x_cal));
            Log.d("Samples", "y-axis " + Float.toString(y_cal));
            Log.d("Samples", "z-axis " + Float.toString(z_cal));
            Log.d("LEFT accel", "Size so far " + accelCount);
            Log.d("Samples", "x-axis " + Float.toString(x_gy));
            Log.d("Samples", "y-axis " + Float.toString(y_gy));
            Log.d("Samples", "z-axis " + Float.toString(z_gy));
            Log.d("LEFT gyro", "Size so far " + gyroCount);
            Log.d("SAMPLES:", "-------------");

            Intent resultIntent = new Intent();
            resultIntent.putExtra("x_accel", x_cal);
            resultIntent.putExtra("y_accel", y_cal);
            resultIntent.putExtra("z_accel", z_cal);

            resultIntent.putExtra("x_gyro", x_gy);
            resultIntent.putExtra("y_gyro", y_gy);
            resultIntent.putExtra("z_gyro", z_gy);

            setResult(RESULT_OK,resultIntent);
            Toast.makeText(Calibrate.this, "finished calibrating", Toast.LENGTH_SHORT).show();
            SensorManager_.unregisterListener(this);
            finish();


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_cal){
            SensorManager_.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
            SensorManager_.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
            if(!pressed) {
                Toast.makeText(Calibrate.this, "Starting Calibration will take few seconds", Toast.LENGTH_SHORT).show();
                pressed = true;
            }
            else
                Toast.makeText(Calibrate.this, "Please wait calibrating", Toast.LENGTH_SHORT).show();
        }
    }
}
