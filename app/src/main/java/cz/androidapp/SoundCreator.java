package cz.androidapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by krnansky on 10.11.2017.
 */

public class SoundCreator {
    private int SAMPLE_RATE = 44100; //samples per second
    private int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_STEREO;
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int STREAM = AudioManager.STREAM_MUSIC;
    private int MODE = AudioTrack.MODE_STREAM; //AudioTrack.MODE_STREAM
    private int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT, ENCODING);
    private double FREQUENCY = 200; //Hz
    private double PULSE_WIDTH = 1; //ms

    public boolean isPlaying() {
        return IsPlaying;
    }

    private boolean IsPlaying = false;

    private AudioTrack sound;
    private Thread soundThread;

    public SoundCreator() {
        sound = new AudioTrack(STREAM, SAMPLE_RATE, CHANNEL_OUT, ENCODING, BUFFER_SIZE, MODE);
    }

    public void playSquare(double frequency, double pulseWidth){
        this.FREQUENCY = frequency;
        this.PULSE_WIDTH = pulseWidth;
        if (!IsPlaying) {
            sound.play();
            IsPlaying = true;
            soundThread = new Thread(soundGenerator);
            soundThread.start();
        }

    }

    public void stop(){
        sound.stop();
        IsPlaying = false;
    }

    public void playLast(){
        if (!IsPlaying) {
            sound.play();
            IsPlaying = true;
        }
    }

    private Runnable soundGenerator = new Runnable() {
        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            while (IsPlaying) {
                //     short samples[] = sinus(FREQUENCY,SAMPLE_RATE);
                short samples[] = square();
                sound.write(samples, 0, samples.length);
            }
        }
    };

    private short[] square() {
        int sampleCount; //samples of one period
        short ampRight = 32767; // amplitude minimum -32767, maximum +32767
        short ampLeft = 32767 / 2;

        sampleCount = 2 * (int) ((float) SAMPLE_RATE / FREQUENCY); //kolik frame má jedna perioda
        short samples[] = new short[sampleCount]; //pole frame

        for (int i = 0; i < sampleCount; i += 2) {
            if (i < (int) (PULSE_WIDTH * SAMPLE_RATE / 1000)) {
                samples[i + 1] = ampLeft; //amplituda fáze pro frame -levý
                samples[i + 0] = ampRight; //amplituda fáze pro frame -pravý
            } else {
                samples[i + 1] = ampLeft;
                samples[i + 0] = 0;
            }

        }
        return samples;
    }

    private short[] sinus(double frequency, int sampleRate) {
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
    }
}