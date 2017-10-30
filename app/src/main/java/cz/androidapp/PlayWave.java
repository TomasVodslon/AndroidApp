package com.project.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by krnansky on 18.5.2016.
 */
public class PlayWave {

    private final int SAMPLE_RATE = 44100;
    private AudioTrack mAudio;
    private int sampleCount;
//    public boolean isRunnable() {
//        return isRunnable;
//    }
//    private boolean isRunnable = false;
    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    private LineGraphSeries<DataPoint> series;

    public PlayWave() {
        int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffsize, AudioTrack.MODE_STATIC);
    }

    public void setWave(int delkaPeriody, byte pocetPulsu){
        //sampleCount = (int)(float)SAMPLE_RATE/frequency;
        sampleCount = 2400;
        short samples[] = new short[sampleCount];
        short amplitude = 32767;
        //double twopi = 8. * Math.atan(1.);
        //double phase = 0.0;
//sinus na SQUARE_ PULSE chybí
        series = new LineGraphSeries<DataPoint>();
        for (int i = 0; i < pocetPulsu; i++){
            for (int j = 0;j < delkaPeriody/2; j++)
            {
                samples[i*delkaPeriody + delkaPeriody/2 + j] = amplitude;
                samples[i*delkaPeriody + j] = 0;
            }
//            samples[i] = (short)(amplitude * Math.signum(phase));
//            phase += twopi * frequency / SAMPLE_RATE;
        }
            for(int i = 0; i<sampleCount;i++){
                series.appendData(new DataPoint(i,samples[i]),true,sampleCount);
            }
        mAudio.write(samples, 0, sampleCount);
    }

    public void start(){
//        this.isRunnable = true;
        mAudio.reloadStaticData();
        mAudio.setLoopPoints(0,sampleCount,-1);
        mAudio.play();
    }

    public void stop(){
        mAudio.stop();
        //mAudio.release();
//        this.isRunnable = false;
    }
}
