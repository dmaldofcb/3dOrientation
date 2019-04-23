package com.ebookfrenzy.a3dorientation;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private boolean calledA = false;
    private boolean calledG = false;

    private EditText compFiterX;
    private EditText compFiterY;
    private EditText compFiterZ;
    private EditText sensorXGyro;
    private EditText sensorYGyro;
    private EditText sensorZGyro;

    public static final float EPSILON = 0.000000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp = 0;

    private EditText sensorXAccel;
    private EditText sensorYAccel;
    private EditText sensorZAccel;

    private EditText accelNoise;
    private EditText biasXA;
    private EditText biasyA;
    private EditText biaszA;
    private EditText biasXG;
    private EditText biasYG;
    private EditText biasZG;

    private EditText sensorXcomp;
    private EditText sensorYcomp;
    private EditText sensorZcomp;

    private EditText accelXAngle;
    private EditText accelYAngle;
    private EditText accelZAngle;

    private float accel_ang_x;
    private float accel_ang_y;
    private float accel_ang_z;

    private float gyro_ang_x;
    private float gyro_ang_y;
    private float gyro_ang_z;

    private float prevXang = 0;
    private float prevYang = 0;
    private float prevZang = 0;

    private EditText gyroXAngle;
    private EditText gyroYAngle;
    private EditText gyroZAngle;

    private Button convertButton;
    private Button cal;
    private SensorManager SensorManager_;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor compass;

    private float accelOffsetX;
    private float accelOffsetY;
    private float accelOffsetZ;
    private float gyroOffsetX;
    private float gyroOffsetY;
    private float gyroOffsetZ;

    private ArrayList<GyroscopeSampler> gyroSample = new ArrayList<>();
    private GyroscopeSampler previousGyro;
    //private AccelerometerSampler[] accelSample = new AccelerometerSampler[200];
    //private float[] gyroSample;
    private int sampleRate = 10;
    private float Ts = 1/20;
    private int total = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //bias
        biasXA = (EditText) findViewById(R.id.yBiasAccel);
        biasyA = (EditText) findViewById(R.id.zBiasAccel);
        biasXG = (EditText) findViewById(R.id.xBiasGyro);
        biasYG = (EditText) findViewById(R.id.yBiasGyro);
        biasZG = (EditText) findViewById(R.id.zBiasGyro);

        //accelerometer sensors
        sensorXAccel = (EditText) findViewById(R.id.x_axis_accel);
        sensorYAccel = (EditText) findViewById(R.id.y_axis_accel);
        sensorZAccel= (EditText) findViewById(R.id.z_axis_accel);
        accelNoise = (EditText) findViewById(R.id.noise_accel);

        //gyroscope sensors
        sensorXGyro = (EditText) findViewById(R.id.x_axis_gyro);
        sensorYGyro = (EditText) findViewById(R.id.y_axis_gyro);
        sensorZGyro = (EditText) findViewById(R.id.z_axis_gyro);

        //compass sensors
        sensorXcomp = (EditText) findViewById(R.id.x_axis_comp);
        sensorYcomp = (EditText) findViewById(R.id.y_axis_comp);
        sensorZcomp = (EditText) findViewById(R.id.z_axis_comp);

        //accel angles
        accelXAngle = (EditText) findViewById(R.id.accelAngleX);
        accelYAngle = (EditText) findViewById(R.id.accelAngleY);
        accelZAngle = (EditText) findViewById(R.id.accelAngleZ);

        //gyro angles
        gyroXAngle = (EditText) findViewById(R.id.gyroAngleX);
        gyroYAngle = (EditText) findViewById(R.id.gyroAngleY);
        gyroZAngle = (EditText) findViewById(R.id.gyroAngleZ);

        //complimentary filters
        compFiterX = (EditText) findViewById(R.id.compX);
        compFiterY = (EditText) findViewById(R.id.compY);
        compFiterZ = (EditText) findViewById(R.id.compZ);

        //buttons
        convertButton = (Button) findViewById(R.id.CollectData);
        cal = (Button) findViewById(R.id.calibrate);

        //setting listiners for buttons and setting up the sensor managers
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
                startActivityForResult(calibrating,99);
                //Toast.makeText(MainActivity.this,"Starting Calibration will take about",Toast.LENGTH_SHORT);

                //startActivity(calibrating);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 99 && resultCode == RESULT_OK){

            accelOffsetX = data.getFloatExtra("x_accel", -999);
            accelOffsetY = data.getFloatExtra("y_accel", -999);
            accelOffsetZ = data.getFloatExtra("z_accel", -999);
            gyroOffsetX  = data.getFloatExtra("x_gyro",-999);
            gyroOffsetY  = data.getFloatExtra("y_gyro",-999);
            gyroOffsetZ  = data.getFloatExtra("z_gyro",-999);

            accelNoise.setText(Float.toString(accelOffsetX));
            biasXA.setText(Float.toString(accelOffsetY));
            biasyA.setText(Float.toString(accelOffsetZ));
            biasXG.setText(Float.toString(gyroOffsetX));
            biasYG.setText(Float.toString(gyroOffsetY));
            biasZG.setText(Float.toString(gyroOffsetZ));

            Log.d("Samples", "x-axis " + Float.toString(gyroOffsetX));
            Log.d("Samples", "y-axis " + Float.toString(gyroOffsetY));
            Log.d("Samples", "z-axis " + Float.toString(gyroOffsetZ));
        }
        else
            Toast.makeText(MainActivity.this, "Please Calibrate Before Starting", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            calledA = true;
            Float accX = event.values[0];
            Float accY = event.values[1];
            Float accZ = event.values[2];
            sensorXAccel.setText(accX.toString());
            sensorYAccel.setText(accY.toString());
            sensorZAccel.setText(accZ.toString());

            //calculate accelerometer noise
//            double speed = Math.sqrt(accX*accX + accY * accY + accZ * accZ);
//            double noise = speed - SensorManager.GRAVITY_EARTH;
//
//            accelNoise.setText(Double.toString(noise));

            //remove bias from each reading that you got from the calibration
            float accXCal = accX - accelOffsetX;
            float accYCal = accY - accelOffsetY;
            float accyZCal = accZ - accelOffsetZ;

            float x2,y2,z2;

                //calculate squares and square root
            x2 = (float) Math.sqrt(accYCal * accYCal + accyZCal * accyZCal);
            y2 = (float) Math.sqrt(accXCal * accXCal + accyZCal * accyZCal);
            z2 = (float) Math.sqrt(accXCal * accXCal+ accYCal * accYCal);

             accel_ang_x = (float) ((Math.atan2(accXCal,x2)) * (180/Math.PI));
             accel_ang_y = (float) ((Math.atan2(accYCal,y2)) * (180/Math.PI));
             accel_ang_z = (float) ((Math.atan2(accyZCal,z2)) * (180/Math.PI));

                //Log.d("Angles:", "Time:" + Long.toString(temp.getTime()));
                Log.d("Angles", "x-axis" + Float.toString(accel_ang_x));
                Log.d("Angles", "y-axis" + Float.toString(accel_ang_y));
                Log.d("Angles", "z-axis" + Float.toString(accel_ang_z));
               // Log.d("Angles", "Size so far " + Integer.toString(accelSample.size()));

                accelXAngle.setText(Float.toString(accel_ang_x));
                accelYAngle.setText(Float.toString(accel_ang_y));
                accelZAngle.setText(Float.toString(accel_ang_z));

            //create a sample for accelerometer used to store values
           // get 200 samples of Accelerometer, add to arrayList once arrayList is full then we start calculating the angles average out the samples
//            if(accelSample.size() < sampleRate){
//                AccelerometerSampler eve = new AccelerometerSampler(accXCal,accYCal,accyZCal,event.timestamp);
//                accelSample.add(eve);
//
//                Log.d("Offset:","-------------");
//                Log.d("SAMPLES:", "Time:" + Long.toString(eve.getTime()));
//                Log.d("Samples", "x-axis" + Float.toString(eve.getX()) );
//                Log.d("Samples", "y-axis" + Float.toString(eve.getY()) );
//                Log.d("Samples", "z-axis" + Float.toString(eve.getZ()) );
//                Log.d("LEFT", "Size so far " + Integer.toString(accelSample.size()));
//                Log.d("Offset:","-------------");
//            }
//            else {
//
//                //float [] angles = new float[3];
//                float x2,y2,z2;
//                for(AccelerometerSampler temp: accelSample){
//
//                    //calculate squares and square root
//                    x2 = (float) Math.sqrt(temp.getY() * temp.getY() + temp.getZ() * temp.getZ());
//                    y2 = (float) Math.sqrt(temp.getX() * temp.getX() + temp.getZ() * temp.getZ());
//                    z2 = (float) Math.sqrt(temp.getX() * temp.getX() + temp.getY() * temp.getY());
//
//                    angleX = (float) ((Math.atan2(temp.getX(),x2)) * (180/Math.PI));
//                    angleY = (float) ((Math.atan2(temp.getX(),y2)) * (180/Math.PI));
//                    angleZ = (float) ((Math.atan2(temp.getX(),z2)) * (180/Math.PI));
//
//                    Log.d("Angles:", "Time:" + Long.toString(temp.getTime()));
//                    Log.d("Angles", "x-axis" + Float.toString(angleX));
//                    Log.d("Angles", "y-axis" + Float.toString(angleY));
//                    Log.d("Angles", "z-axis" + Float.toString(angleZ));
//                    Log.d("Angles", "Size so far " + Integer.toString(accelSample.size()));
//
//                    accelXAngle.setText(Float.toString(angleX));
//                    accelYAngle.setText(Float.toString(angleY));
//                    accelZAngle.setText(Float.toString(angleZ));
//
//                }
//                accelSample.clear();
//
//            }

        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            calledG = true;
            Float gyroX = event.values[0];
            Float gyroY = event.values[1];
            Float gyroZ = event.values[2];
            sensorXGyro.setText(gyroX.toString());
            sensorYGyro.setText(gyroY.toString());
            sensorZGyro.setText(gyroZ.toString());

            float gyroXCal = gyroX - gyroOffsetX;
            float gyroYCal = gyroY - gyroOffsetY;
            float gyroZCal = gyroZ - gyroOffsetZ;

            float xangle;
            float yangle;
            float zangle;

            float currentSampleX;
            float currentSampleY;
            float currentSampleZ;

//            prevXang = 0;
//            prevYang = 0;
//            prevZang = 0;
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = gyroZCal;
                float axisY = gyroYCal;
                float axisZ = gyroZCal;

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) Math.sqrt((axisX*axisX + axisY*axisY + axisZ*axisZ));

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the time step
                // in order to get a delta rotation from this sample over the time step
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                gyro_ang_x = sinThetaOverTwo * axisX;
                gyro_ang_y = sinThetaOverTwo * axisY;
                gyro_ang_z = sinThetaOverTwo * axisZ;
                gyro_ang_x = (float) (gyro_ang_x * (180/Math.PI));
                gyro_ang_y = (float) (gyro_ang_y * (180/Math.PI));
                gyro_ang_z = (float) (gyro_ang_z * (180/Math.PI));

            }
            gyroXAngle.setText(Float.toString(gyro_ang_x));
            gyroYAngle.setText(Float.toString(gyro_ang_y));
            gyroZAngle.setText(Float.toString(gyro_ang_z));
            timestamp = event.timestamp;



        }

        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            Float compX = event.values[0];
            Float compY = event.values[1];
            Float compZ = event.values[2];

            sensorXcomp.setText(compX.toString());
            sensorYcomp.setText(compY.toString());
            sensorZcomp.setText(compZ.toString());
        }

        if(calledA && calledG){
            complimentaryFiler(accel_ang_x,accel_ang_y,accel_ang_z,gyro_ang_x,gyro_ang_y,gyro_ang_z);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class GyroscopeSampler{
        //private SensorEvent e;
        private float x,y,z, angleX,angleY,angleZ;
        private long time;
        public GyroscopeSampler(float x_,float y_, float z_,long t_){
            //this.e = e_;
            this.x = x_;
            this.y = y_;
            this.z = z_;
            this.time = t_;

        }

        public float getX(){
            return x;
        }
        public float getY(){
            return y;
        }
        public float getZ(){
            return z;
        }
        public long getTime(){
            return time;
        }


    }


    public void complimentaryFiler(float accelXAngle, float accelYAngle, float accelZAngle, float gyroXAngle,float gyroYAngle,float gyroZAngle){

        float angX = (float) (.02 * gyroXAngle + .98 * accelXAngle);
        float angY = (float) (.02 * gyroYAngle + .98 * accelYAngle);
        float angZ = (float) (.02 * gyroZAngle + .98 * accelZAngle);

        compFiterX.setText(Float.toString(angX));
        compFiterY.setText(Float.toString(angY));
        compFiterZ.setText(Float.toString(angZ));
        Log.d("END Looping:","*******");

    }


}