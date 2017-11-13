package cz.androidapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by tomas on 13.11.17.
 */

public class TiltedSensor implements SensorEventListener {

    //Sensor
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    Context mContext;
    long lastTimeUpdate;


    public TiltedSensor(Context mContext) {
        //Registrace sensoru
        lastTimeUpdate = System.currentTimeMillis();
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void RegisterSensor(){
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);//DELAY_UI = 60 ms, DELAY_GAME (NORMAL) = 20 ms, DELAY_FASTES = 0 ms
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI); //DELAY_UI = 60 ms, DELAY_GAME (NORMAL) = 20 ms, DELAY_FASTES = 0 ms
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long actualTime = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (actualTime - lastTimeUpdate > 200) {
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

                //Hodnota která nedává moc smysl, ale je správně bud pozitivní nebo negativní


                if (success) {
                    float values[] = new float[3];
                    float signum = Math.signum(R[4]);


                    SensorManager.getOrientation(R, values);
                    float pitch = values[1];
                    if (signum > 0) {
                        // Převod na pozitivní hodnotu
                        pitch = pitch * -1;
                        double degree = Math.toDegrees(pitch);
                        degree = Math.round((90 - degree));
                        Log.d("Roll většá než nula", String.valueOf(degree));
                        sound.playSquare(100 + degree * 2, 1);
                        displayDegree.setText(String.valueOf(degree) + " °");
                        displayFrequency.setText(String.valueOf(100 + degree * 2) + " Hz, pulse 1 ms");
                    } else {
                        pitch = pitch * -1;
                        double degree = Math.toDegrees(pitch);
                        degree = Math.round((90 - degree) * -1);
                        Log.d("Roll menší než nula", String.valueOf(degree));
                        sound.playSquare(100 - degree * 2, 1);
                        displayDegree.setText(String.valueOf(degree) + " °");
                        displayFrequency.setText(String.valueOf(100 - degree * 2) + " Hz, pulse 1 ms");
                    }

                }

                lastTimeUpdate = actualTime;
            }


        }
    }
}
