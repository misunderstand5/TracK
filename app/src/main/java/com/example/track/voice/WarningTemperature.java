package com.example.track.voice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.databinding.tool.util.L;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import com.android.qzs.voiceannouncementlibrary.VoiceUtils;
import com.example.track.R;

public class WarningTemperature extends AppCompatActivity {
    private static final String TAG="WarningTemperature";

    private Vibrator vibrator;

    private Button buttonSetting;
    private Object Context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_temperature);
    }

    @Override
    protected void onStart() {
        super.onStart();

        buttonSetting = findViewById(R.id.buttonSetting);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Play("大哥大哥");
            }
        });
    }
//    private synchronized void Play(final String str) {
//
//        if (VoiceUtils.with(this).GetIsPlay()){
//
//            System.out.println("正在播放语音 ");
//
//            new Thread() {
//
//                @Override
//
//                public void run() {
//
//                    super.run();
//
//                    try {
//
//                        Thread.sleep(100);//休眠0.1秒
//
//                        Play(str);
//
//                    } catch (InterruptedException e) {
//
//                        e.printStackTrace();
//
//                    }
//
//                }
//
//            }.start();
//
//        }else {
//
//            VoiceUtils.with(this).Play(str,true);
//
//        }
//
//    }
}
