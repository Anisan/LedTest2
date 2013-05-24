package com.eraser.ledtest2;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.io.FileWriter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements
        OnSeekBarChangeListener, OnClickListener {

    private LedBlinker ledBlinker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar red = (SeekBar)findViewById(R.id.seekBar);
        red.setOnSeekBarChangeListener(this);
        red.setTag("red");

        SeekBar green = (SeekBar)findViewById(R.id.seekBar2);
        green.setOnSeekBarChangeListener(this);
        green.setTag("green");

        SeekBar blue = (SeekBar)findViewById(R.id.seekBar3);
        blue.setOnSeekBarChangeListener(this);
        blue.setTag("blue");

        Button quit = (Button)findViewById(R.id.button);
        quit.setOnClickListener(this);
    }


    public void onClick(View v) {
        ledControl("red", 0);
        ledControl("green", 0);
        ledControl("blue",  0);
        //finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onStart() {
        super.onStart();
        ledBlinker = new LedBlinker();
        //ledBlinker.start();
    }

    protected void onStop() {
        super.onStop();
        if (ledBlinker != null && !ledBlinker.stopped) {
            ledBlinker.stopped = true;
            ledBlinker.interrupt();
            ledBlinker = null;
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) { }
    public void onStopTrackingTouch(SeekBar seekBar) { }
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        ledControl((String)seekBar.getTag(), progress);
    }

    public void ledControl(String name, int brightness) {
        try {
            FileWriter fw = new FileWriter("/sys/class/leds/" + name + "/brightness");
            fw.write(Integer.toString(brightness));
            fw.close();
        } catch (Exception e) {
            try {
                ((TextView)findViewById(R.id.textView4)).setText(e.toString());
            } catch (Exception e2) { }
        }
    }

    class LedBlinker extends Thread {
        volatile boolean stopped = false;

        public void run() {
            for (int ledState = 0; !stopped; ledState = (ledState + 1) % 6) {
                switch (ledState) {
                    case 0: ledControl("red", 255); break;
                    case 1: ledControl("blue",  255); break;
                    case 2: ledControl("red",   0); break;
                    case 3: ledControl("green", 255); break;
                    case 4: ledControl("blue",    0); break;
                    case 5: ledControl("green",   0); break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
            ledControl("red", 0);
            ledControl("green", 0);
            ledControl("blue",  0);
        }
    }
}
