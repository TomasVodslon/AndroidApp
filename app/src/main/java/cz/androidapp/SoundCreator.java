package cz.androidapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by krnansky on 10.11.2017.
 */

public class SoundCreator //implements Observer {
{
    static final int CHANNEL_LEFT = 1;
    static final int CHANNEL_RIGHT = 0;
    static final int CHANNEL_BOTH = 2;
    private int SIGNAL_TYPE = 0; //sinus, square, linear
    private short AMPLITUDE = 32767; // amplitude minimum -32767, maximum +32767
    private int CHANNELS = 0;
    private int SAMPLES_LENGTH = 0; //count samples of one period
    private int SAMPLE_RATE = 44100; //samples per second
    private int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_STEREO;
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int STREAM = AudioManager.STREAM_MUSIC;
    private int MODE = AudioTrack.MODE_STREAM; //AudioTrack.MODE_STREAM
    private int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT, ENCODING);
    private double FREQUENCY = 200; //Hz
    private double PULSE_WIDTH = 1; //ms
    private boolean LEFT = false;
    private boolean RIGHT = false;
    private boolean ISPLAYING = false;
    private AudioTrack sound;
    private Thread soundThread;
    private short[] samples;

/*    @Override
    public void update(double degree) {
        playSquare(100 + degree * 2, 1);
    }*/

    // Declare the @IntDef for these constants:
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CHANNEL_RIGHT, CHANNEL_LEFT, CHANNEL_BOTH})
    private @interface Channels {
    }

    SoundCreator(@Channels int channel) {
        sound = new AudioTrack(STREAM, SAMPLE_RATE, CHANNEL_OUT, ENCODING, BUFFER_SIZE, MODE);
        setChannel(channel);
    }

    public void setChannel(@Channels int channel) {
        this.CHANNELS = channel;
        switch (channel) {
            case 1:
                LEFT = true;
                RIGHT = false;
                break;
            case 0:
                LEFT = false;
                RIGHT = true;
                break;
            case 2:
                LEFT = true;
                RIGHT = true;
                break;
        }
    }

    public void setAmplitude(double AMPLITUDE) {
        this.AMPLITUDE = (short) (AMPLITUDE * 32767);
    }

    public String getChannel() {
        String text = null;
        switch (CHANNELS) {
            case 1:
                text = "Left";
                break;
            case 0:
                text = "Right";
                break;
            case 2:
                text = "Both";
        }
        return text;
    }

    public short getAmplitude() {
        return AMPLITUDE;
    }

    public double getFrequency() {
        return FREQUENCY;
    }

    public double getPulseWidth() {
        return PULSE_WIDTH;
    }

    public int getSamplesLength() {
        return SAMPLES_LENGTH;
    }

    public String getSignalType() {
        String text = null;
        switch (SIGNAL_TYPE) {
            case 1:
                text = "Square";
                break;
            case 0:
                text = "Sinus";
                break;
            case 2:
                text = "Linear";
        }
        return text;
    }

    public int getBufferSize() {
        return BUFFER_SIZE;
    }

    public void Square(double frequency, double pulseWidth) {
        SIGNAL_TYPE = 1;
        this.FREQUENCY = frequency;
        this.PULSE_WIDTH = pulseWidth;
    }

    public void Sinus(double frequency) {
        SIGNAL_TYPE = 0;
        this.FREQUENCY = frequency;
        PULSE_WIDTH = 0;
    }

    public void Linear(double pulseWidth) {
        SIGNAL_TYPE = 2;
        this.PULSE_WIDTH = pulseWidth;
        FREQUENCY = 0;
    }

    public void play(){
        ISPLAYING = true;
        sound.play();
        soundThread = new Thread(soundGenerator);
        soundThread.start();
    }

    void stop(){
        ISPLAYING = false;
        sound.stop();
    }

    boolean isPlaying() {
        return ISPLAYING;
    }

    private Runnable soundGenerator = new Runnable() {

        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            while (ISPLAYING) {
                short samples[]; //pole pro buffer
                    switch (SIGNAL_TYPE) {
                        case 1: samples = square(FREQUENCY, PULSE_WIDTH, SAMPLE_RATE);
                            break;
                        case 0: default: samples = sinus(FREQUENCY, SAMPLE_RATE);
                            break;
                        case 2: samples = linear(PULSE_WIDTH, SAMPLE_RATE);
                            break;
                    }
                sound.write(samples, 0, samples.length);
                SAMPLES_LENGTH = samples.length;
            }
        }
    };

    private short[] sinus(double frequency, int sampleRate) {
        int samplesLenght = 2 * (int) ((float) sampleRate / frequency); //kolik frame má jedna perioda
        double twopi = 2 * Math.PI;
        double inkrement = 0;
        //pole frame
        samples = new short[samplesLenght];
        for (int i = 0; i < samples.length; i += 2) {
            if(LEFT)samples[i]= (short)(AMPLITUDE * Math.sin(inkrement)); //amplituda fáze pro frame -levý
            if(RIGHT)samples[i + 1] = (short) (AMPLITUDE * Math.sin(inkrement)); //amplituda fáze pro frame -pravý
            inkrement += (float)(twopi * frequency / sampleRate);  //inkrement fáze
        }
        return samples;
    }

    private short[] square(double frequency, double pulseWidth, int sampleRate) {
        int samplesLenght = 2 * (int) ((float) sampleRate / frequency); //kolik frame má jedna perioda
        short[] samples = new short[samplesLenght]; //pole frame
        for (int i = 0; i < samples.length; i += 2) {
            if (i < (int) (pulseWidth * sampleRate / 1000)) {
                if(LEFT)samples[i] = AMPLITUDE; //amplituda fáze pro frame -levý
                if(RIGHT)samples[i + 1] = AMPLITUDE; //amplituda fáze pro frame -pravý
            }
        }
        return samples;
    }

    private short[] linear(double pulseWidth, int sampleRate) {
        int samplesLength = 2 * (int) (pulseWidth * sampleRate / 1000); //kolik frame má jedna perioda
        short[] samples = new short[samplesLength]; //pole frame
        for (int i = 0; i < samples.length; i += 2) {
            if(LEFT)samples[i] = AMPLITUDE; //amplituda fáze pro frame -pravý
            if(RIGHT)samples[i + 1] = AMPLITUDE; //amplituda fáze pro frame -levý
        }
        return samples;
    }

}