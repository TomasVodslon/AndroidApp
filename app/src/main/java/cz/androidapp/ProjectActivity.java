package cz.androidapp;

import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProjectActivity extends AppCompatActivity {

    TextView displayFrequency;
    SeekBar seekBar1;
    PlayWave wave = new PlayWave();
    int progressValue = 1000;
    int minValue = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_project);
  //      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
  //      setSupportActionBar(toolbar);
        initializeView();
    }

    @Override
    protected void onStop(){
        wave.stop();
        super.onStop();
        finish();
    }

    public void setSeekBar(){
        seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
        displayFrequency = (TextView)findViewById(R.id.textView);
        displayFrequency.setText(String.valueOf(minValue)+ " Hz");
        seekBar1.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressValue = minValue + progress;
                        wave.stop();
                        wave.sinus(progressValue);
                        //wave.square(progressValue,1);
                        displayFrequency.setText(String.valueOf(progressValue) + " Hz");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        displayFrequency.setText(String.valueOf(progressValue) + " Hz");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        displayFrequency.setText(String.valueOf(progressValue) + " Hz");
                        wave.stop();
                    }
                }
        );
    }

    private void initializeView() {
        setSeekBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
 //           return true;
 //       }

        return super.onOptionsItemSelected(item);
    }
}
