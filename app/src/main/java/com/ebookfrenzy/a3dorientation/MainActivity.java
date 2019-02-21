package com.ebookfrenzy.a3dorientation;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private EditText sensorXGyro;
    private EditText sensorYGyro;
    private EditText sensorZGyro;

    private EditText sensorXAccel;
    private EditText sensorYAccel;
    private EditText sensorZAccel;
    private EditText accelNoise;

    private EditText sensorXcomp;
    private EditText sensorYcomp;
    private EditText sensorZcomp;


    private Button convertButton;
    private Button cal;
    private SensorManager SensorManager_;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sensorXAccel = (EditText) findViewById(R.id.x_axis_accel);
        sensorYAccel = (EditText) findViewById(R.id.y_axis_accel);
        sensorZAccel= (EditText) findViewById(R.id.z_axis_accel);
        accelNoise = (EditText) findViewById(R.id.noise_accel);

        sensorXGyro = (EditText) findViewById(R.id.x_axis_gyro);
        sensorYGyro = (EditText) findViewById(R.id.y_axis_gyro);
        sensorZGyro = (EditText) findViewById(R.id.z_axis_gyro);

        sensorXcomp = (EditText) findViewById(R.id.x_axis_comp);
        sensorYcomp = (EditText) findViewById(R.id.y_axis_comp);
        sensorZcomp = (EditText) findViewById(R.id.z_axis_comp);



        convertButton = (Button) findViewById(R.id.CollectData);
        cal = (Button) findViewById(R.id.calibrate);

        convertButton.setOnClickListener(this);
        cal.setOnClickListener(this);
        SensorManager_= (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = SensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = SensorManager_.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        compass = SensorManager_.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CollectData:
                SensorManager_.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                SensorManager_.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
                SensorManager_.registerListener(this,compass,SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case R.id.calibrate:
                Intent calibrating = new Intent(MainActivity.this, Calibrate.class);
                startActivity(calibrating);
                break;

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Float accX = event.values[0];
            Float accY = event.values[1];
            Float accZ = event.values[2];
            sensorXAccel.setText(accX.toString());
            sensorYAccel.setText(accY.toString());
            sensorZAccel.setText(accZ.toString());

            //calculate accelerometer noise
            double speed = Math.sqrt(accX*accX + accY * accY + accZ * accZ);
            double noise = speed - SensorManager.GRAVITY_EARTH;

            accelNoise.setText(Double.toString(noise));

        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Float gyroX = event.values[0];
            Float gyroY = event.values[1];
            Float gyroZ = event.values[2];
            sensorXGyro.setText(gyroX.toString());
            sensorYGyro.setText(gyroY.toString());
            sensorZGyro.setText(gyroZ.toString());
        }

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            Float compX = event.values[0];
            Float compY = event.values[1];
            Float compZ = event.values[2];

            sensorXcomp.setText(compX.toString());
            sensorYcomp.setText(compY.toString());
            sensorZcomp.setText(compZ.toString());
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}