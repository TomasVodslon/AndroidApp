package cz.androidapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by krnansky on 10.11.2017.
 */

public class SoundCreator implements Observer {
    public static final int CHANNEL_LEFT = 1;
    public static final int CHANNEL_RIGHT = 0;
    public static final int CHANNEL_BOTH = 2;
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
    private boolean ISPLAYING = false;
    private AudioTrack sound;
    private Thread soundThread;

    @Override
    public void update(double degree) {
        playSquare(100 + degree * 2, 1);
    }

    // Declare the @IntDef for these constants:
    @Retention(RetentionPolicy.SOURCE)
    @IntDef ({CHANNEL_RIGHT, CHANNEL_LEFT, CHANNEL_BOTH})
    private @interface Channels{}

    SoundCreator(@Channels int channel) {
        sound = new AudioTrack(STREAM, SAMPLE_RATE, CHANNEL_OUT, ENCODING, BUFFER_SIZE, MODE);
        this.CHANNELS = channel;
    }

    public void setChannel(@Channels int channel){
    this.CHANNELS = channel;
    }

    public int getChannel(){
    return CHANNELS;
    }

    public int getSamplesLength() {
        return SAMPLES_LENGTH;
    }

    public boolean isPlaying() {
        return ISPLAYING;
    }

    public int getBufferSize() {
        return BUFFER_SIZE;
    }

    public void playSquare(double frequency, double pulseWidth){
        if (!ISPLAYING) {
            sound.play();
            ISPLAYING = true;
            soundThread = new Thread(soundGenerator);
            soundThread.start();
        } else {

        }
        this.FREQUENCY = frequency;
        this.PULSE_WIDTH = pulseWidth;
    }

    public void stop(){
        sound.stop();
        ISPLAYING = false;
    }

    public void playLast(){
        if (!ISPLAYING) {
            sound.play();
            ISPLAYING = true;
        }
    }

    private Runnable soundGenerator = new Runnable() {

        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

            while (ISPLAYING) {

                short samples[]; //pole pro buffer
                    switch (SIGNAL_TYPE) {
                        case 0: samples = square(FREQUENCY, PULSE_WIDTH, SAMPLE_RATE);
                            break;
                        case 1: samples = sinus(FREQUENCY, SAMPLE_RATE);
                            break;
                        case 2: samples = linear(SAMPLES_LENGTH);
                            break;
                        default: samples = sinus(FREQUENCY, SAMPLE_RATE);
                            break;
                    }
                sound.write(samples, 0, samples.length);
                SAMPLES_LENGTH = samples.length;
            }
        }
    };

    private short[] sinus(double frequncy, int sampleRate) {
        int samplesLenght = 2 * (int) ((float) sampleRate / frequncy); //kolik frame má jedna perioda
        double twopi = 2 * Math.PI;
        double inkrement = 0;
        short[] samples = new short[samplesLenght]; //pole frame
        for (int i = 0; i < samples.length; i += 2) {

            samples[i + 1] = (short) (AMPLITUDE * Math.sin(inkrement)); //amplituda fáze pro frame
            inkrement += twopi * frequncy / samples.length;  //inkrement fáze
        }
        return samples;
    }

    private short[] square(double frequncy, double pulseWidth, int sampleRate) {
        int samplesLenght = 2 * (int) ((float) sampleRate / frequncy); //kolik frame má jedna perioda
        short[] samples = new short[samplesLenght]; //pole frame
        for (int i = 0; i < samples.length; i += 2) {
            if (i < (int) (pulseWidth * sampleRate / 1000)) {
                samples[i + 1] = AMPLITUDE; //amplituda fáze pro frame -levý
            } else {
                samples[i + 1] = 0;
            }
        }
        return samples;
    }

    private short[] linear(int samplesLength) {
        short[] samples = new short[samplesLength]; //pole frame
        for (int i = 0; i < samples.length; i += 2) {
            samples[i + 1] = AMPLITUDE; //amplituda fáze pro frame -levý
        }
        return samples;
    }

}