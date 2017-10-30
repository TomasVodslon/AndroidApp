package com.project.myapplication;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;

public class ProjectActivity extends AppCompatActivity {

    TextView displayFrequency;
    SeekBar seekBar1;
    PlayWave wave = new PlayWave();
    Button button;
    int progressValue = 50;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        displayFrequency.setText(String.valueOf(progressValue));

        graph = (GraphView)findViewById(R.id.graph);





        seekBar1.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {


                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressValue = 50 + progress;
                        displayFrequency.setText(String.valueOf(progressValue));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        wave.stop();
                        displayFrequency.setText(String.valueOf(progressValue));
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        displayFrequency.setText(String.valueOf(progressValue));
                    }
                }
        );
    }
    public void setButton(){
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wave.setWave(progressValue, (byte) 2);
                graph.removeAllSeries();
                graph.addSeries(wave.getSeries());
                wave.start();
                displayFrequency.setText(String.valueOf(progressValue));
            }
        });
    }

    private void initializeView() {
        setSeekBar();
        setButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
