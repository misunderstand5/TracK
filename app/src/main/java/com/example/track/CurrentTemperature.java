package com.example.track;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.track.View.CirStatisticGraph;
import com.example.track.View.CurrentTemperatureView;

import java.io.IOException;

public class CurrentTemperature extends AppCompatActivity {
    private CirStatisticGraph cirStatisticGraph;
    private CurrentTemperatureView mCurrentViewTemperature;

    private SoundPool mSoundPool;
    private int soundId;
    private Ringtone ringtone;
    private MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_temperature);
        init();
        SoundInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundInit();
        if(Double.parseDouble(mCurrentViewTemperature.getCurTemp())>=28){
            System.out.println("进来了");
            mMediaPlayer.start();
        }
    }

    public void SoundInit(){
        mMediaPlayer = MediaPlayer.create(CurrentTemperature.this, R.raw.theheartaskspleasurefirst);
    }
    /**
     * 初始化对象
     */
    public void init(){
        cirStatisticGraph = (CirStatisticGraph) findViewById(R.id.CirStatisticGraph);
        mCurrentViewTemperature = findViewById(R.id.view_221);
        LinearLayout layout_view=findViewById(R.id.activity_currentTemp_view);
        System.out.println(Double.parseDouble(mCurrentViewTemperature.getCurTemp()));

    }

}