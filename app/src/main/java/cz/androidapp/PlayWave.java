package cz.androidapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by krnansky on 18.5.2016.
 */
public class PlayWave {

    private final int SAMPLE_RATE = 44100; //samples per second
    private AudioTrack mAudio;

    public PlayWave() {
        int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffsize, AudioTrack.MODE_STATIC); //AudioTrack.MODE_STREAM
    }

    public void sinus(int frequency) {
        int sampleCount; //samples of one period
        short amplitude = 32767; // amplitude minimum -32767, maximum +32767
        double phase = 0.0; //začátek fáze
        double twopi = 8. * Math.atan(1.);

        sampleCount = (int) (float) SAMPLE_RATE / frequency; //kolik frame má jedna perioda
        short samples[] = new short[sampleCount]; //pole frame

        for (int i = 0; i < sampleCount; i++) { //vypočet periody sinus
            samples[i] = (short) (amplitude * Math.sin(phase)); //amplituda fáze pro frame
            phase += twopi * frequency / SAMPLE_RATE;  //inkrement fáze
        }

        mAudio.write(samples, 0, sampleCount); //write samples to AudioTrack
        // mAudio.reloadStaticData();
        mAudio.setLoopPoints(0, sampleCount, -1); //set loop
        mAudio.play();
    }

    public void square(int frequency, double pulseWidth) {
        int sampleCount; //samples of one period
        short amplitude = 32767; // amplitude minimum -32767, maximum +32767
        double phase = 0.0; //začátek fáze
        double twopi = 8. * Math.atan(1.);

        sampleCount = (int) (float) SAMPLE_RATE / frequency; //kolik frame má jedna perioda
        short samples[] = new short[sampleCount]; //pole frame

        for (int i = 0; i < sampleCount; i++) { //vypočet periody
            if(i < (int) (pulseWidth * SAMPLE_RATE / 1000)) {
                samples[i] = amplitude; //amplituda fáze pro frame
            } else {
                samples[i] = 0;
            }
        }


        mAudio.write(samples, 0, sampleCount); //write samples to AudioTrack
        // mAudio.reloadStaticData();
        mAudio.setLoopPoints(0, sampleCount, -1); //set loop
        mAudio.play();
    }

    public void stop() {
        if (mAudio.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudio.stop();
        }
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
