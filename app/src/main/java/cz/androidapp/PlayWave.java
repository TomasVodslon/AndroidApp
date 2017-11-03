package cz.androidapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by krnansky on 18.5.2016.
 */
public class PlayWave {

    private final int SAMPLE_RATE = 44100; //samples per second
    private AudioTrack mAudio;
    private int sampleCount; //samples of one period
    private short amplitude = 32767;
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
        mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffsize, AudioTrack.MODE_STATIC); //AudioTrack.MODE_STREAM
    }


    public void play(int frequency) {
        sampleCount = (int) (float) SAMPLE_RATE / frequency; //kolik samplů má jedna perioda
        short samples[] = new short[sampleCount]; //pole samplů
        double twopi = 8. * Math.atan(1.);
        double phase = 0.0; //začátek fáze
        for (int i = 0; i < sampleCount; i++) { //vypočet periody pro sinus
            samples[i] = (short) (amplitude * Math.sin(phase)); //amplituda fáze pro sampl
            phase += twopi * frequency / SAMPLE_RATE;  //inkrement fáze
        }
        mAudio.write(samples, 0, sampleCount);
        mAudio.reloadStaticData();
        mAudio.setLoopPoints(0, sampleCount, 1);
        mAudio.play();
    }

    public void setWave(int delkaPeriody, byte pocetPulsu){
        //sampleCount = (int)(float)SAMPLE_RATE/frequency;
        //double twopi = 8. * Math.atan(1.);
        //double phase = 0.0;
        sampleCount = 2400;
        short samples[] = new short[sampleCount];
        short amplitude = 32767;

     /*sinus wave - void generateSamples(short[] samples) {
        double phase = getPhase();
        for (int i = 0; i < getBufferSize(); i++) {
            samples[i] = (short) (Synth.AMPLITUDE * Math.sin(phase));
            phase += (Math.PI * 2) * (getFrequency() / Synth.SAMPLE_RATE);
        }
        setPhase(phase);
    }*/

    /*square wave - void generateSamples(short[] samples) {
        double phase = getPhase();
        for (int i = 0; i < getBufferSize(); i++) {
            samples[i] = (short) (Math.signum(Math.sin(phase)) * Synth.AMPLITUDE);
            phase += (Math.PI * 2) * (getFrequency() / Synth.SAMPLE_RATE);
        }
        setPhase(phase);
    }*/

    /*triangle wave - void generateSamples(short[] samples) {
        double phase = getPhase();
        for (int i = 0; i < getBufferSize(); i++) {
            samples[i] = (short) ((2 / Math.PI) * Math.asin(Math.sin(phase)) * Synth.AMPLITUDE);
            phase += (Math.PI * 2) * (getFrequency() / Synth.SAMPLE_RATE);
        }
        setPhase(phase);
    }*/

    /*sawtooth wave - void generateSamples(short[] samples) {
        double phase = getPhase();
        for (int i = 0; i < getBufferSize(); i++) {
            samples[i] = (short) ((2 * ((phase / (2 * Math.PI)) - Math.floor(0.5 + (phase / (2 * Math.PI))))) * Synth.AMPLITUDE);
            phase += (Math.PI * 2) * (getFrequency() / Synth.SAMPLE_RATE);
        }
        setPhase(phase);
    }*/

        for (int i = 0; i < pocetPulsu; i++){
            for (int j = 0;j < delkaPeriody/2; j++)
            {
                samples[i*delkaPeriody + delkaPeriody/2 + j] = amplitude;
                samples[i*delkaPeriody + j] = 0;
            }
//            samples[i] = (short)(amplitude * Math.signum(phase));
//            phase += twopi * frequency / SAMPLE_RATE;
        }

        series = new LineGraphSeries<DataPoint>();
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

/* pause audio po odpojení sluchátek
private class BecomingNoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
        }
    }
}
private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

MediaSessionCompat.Callback callback = new
MediaSessionCompat.Callback() {
  @Override
  public void onPlay() {
    registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
  }

  @Override
  public void onStop() {
    unregisterReceiver(myNoisyAudioStreamReceiver);
  }
}

*/
