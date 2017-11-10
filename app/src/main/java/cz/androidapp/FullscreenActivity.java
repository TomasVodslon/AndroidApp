package cz.androidapp;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements SensorEventListener {
    private int SAMPLE_RATE = 44100; //samples per second
    private int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_STEREO;
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int STREAM = AudioManager.STREAM_MUSIC;
    private int MODE = AudioTrack.MODE_STREAM; //AudioTrack.MODE_STREAM
    private int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT, ENCODING);
    private double FREQUENCY = 200;

    AudioTrack sound = new AudioTrack(STREAM, SAMPLE_RATE, CHANNEL_OUT, ENCODING, BUFFER_SIZE, MODE);
    Thread soundThread;
    boolean soundStop = false;

    //UI Content
    TextView displayDegree;
    TextView displayFrequency;
    //  SeekBar seekBar1;


    long lastTimeUpdate;

    //Sensor
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    Float azimut;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MyApp", "I am here");
        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        //Incializace lasttimeupdate
        lastTimeUpdate = System.currentTimeMillis();

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //setSeekBar();
        displayDegree = (TextView) findViewById(R.id.textView1);
        displayFrequency = (TextView) findViewById(R.id.textView2);
        sound.play();
        soundThread = new Thread(soundGenerator);
        soundThread.start();


        //Registrace sensoru
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

/*    public void setSeekBar() {

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);

        displayDegree.setText(String.valueOf(minValue) + " Hz");
        seekBar1.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressValue = minValue + progress;
                        //wave.stop();
                        //wave.sinus(progressValue);
                        //wave.square(progressValue,1);
                        displayDegree.setText(String.valueOf(progressValue) + " Hz");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        displayDegree.setText(String.valueOf(progressValue) + " Hz");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        displayDegree.setText(String.valueOf(progressValue) + " Hz");
                        //wave.stop();
                    }
                }
        );
    }*/

/*    private short[] sinus(double frequency, int sampleRate) {
        int sampleCount; //samples of one period
        short amplitude = 32767; // amplitude minimum -32767, maximum +32767
        double inkrement = 0.0; //začátek fáze
        double twopi = 2 * Math.PI;

        sampleCount = (int)((float)sampleRate / frequency); //kolik frame má jedna perioda
        short samples[] = new short[sampleCount]; //pole frame

        for (int i = 0; i < sampleCount; i =+ 2) { //vypočet periody sinus
            samples[i+1] = (short) (amplitude * Math.sin(inkrement)); //amplituda fáze pro frame
            inkrement += twopi * frequency / sampleRate;  //inkrement fáze
        }
        return samples;
    }*/

    private short[] square(double frequency, double pulseWidth) {
        int sampleCount; //samples of one period
        short ampRight = 32767; // amplitude minimum -32767, maximum +32767
        short ampLeft = 15000;

        sampleCount = 2*(int) ((float) SAMPLE_RATE / frequency); //kolik frame má jedna perioda
        short samples[] = new short[sampleCount]; //pole frame

        for (int i = 0; i < sampleCount; i += 2) {
            if(i < (int) (pulseWidth * SAMPLE_RATE / 1000)) {
                samples[i + 1] = ampLeft; //amplituda fáze pro frame -levý
                samples[i + 0] = ampRight; //amplituda fáze pro frame -pravý
            } else {
                samples[i + 1] = ampLeft;
                samples[i + 0] = 0;
            }

        }
        return samples;
    }

    Runnable soundGenerator = new Runnable()
    {
        public void run()
        {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            while(!soundStop)
            {
           //     short samples[] = sinus(FREQUENCY,SAMPLE_RATE);
                short samples[] = square(FREQUENCY,1);

                sound.write(samples, 0, samples.length);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);//DELAY_UI = 60 ms, DELAY_GAME (NORMAL) = 20 ms, DELAY_FASTES = 0 ms
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI); //DELAY_UI = 60 ms, DELAY_GAME (NORMAL) = 20 ms, DELAY_FASTES = 0 ms
    }

    float[] mGravity;
    float[] mGeomagnetic;

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
                    if(signum > 0){
                        // Převod na pozitivní hodnotu
                        pitch = pitch * -1;
                        double degree = Math.toDegrees(pitch);
                        degree = Math.round((90 - degree));
                        Log.d("Roll většá než nula", String.valueOf(degree));
                        FREQUENCY = 100+degree*2;
                        displayDegree.setText(String.valueOf(degree) + " °");
                        displayFrequency.setText(String.valueOf(FREQUENCY) + " Hz, pulse 1 ms");
                    } else {
                        pitch = pitch * -1;
                        double degree = Math.toDegrees(pitch);
                        degree = Math.round((90 - degree) * -1 );
                        Log.d("Roll menší než nula", String.valueOf(degree));
                        FREQUENCY = 100-degree*2;
                        displayDegree.setText(String.valueOf(degree) + " °");
                        displayFrequency.setText(String.valueOf(FREQUENCY) + " Hz, pulse 1 ms");
                    }

                }

                lastTimeUpdate = actualTime;
            }


        }
    }

    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;

  /*  private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;
        Log.d("Pitch: ",String.valueOf(pitch));
        Log.d("Roll: ", String.valueOf(roll));
    }*/


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
