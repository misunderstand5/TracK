package com.example.track.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.track.R;
import com.example.track.View.WaveView;
import com.example.track.View.WaveView;



/**
 * ======================================================================
 * <p/>
 * 作者：Renj
 * <p/>
 * 创建时间：2016-12-23    9:25
 * <p/>
 * 描述：
 * <p/>
 * 修订历史：
 * <p/>
 * ======================================================================
 */
public class CircleActivity extends Activity {
    private static final String TAG="CircleActivity";
    private WaveView waveview;
    private Button btStart;
    private Button btStop;
    private float waterPrecent;//范围0.00--1.00
//    private TextView tvPrecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        initialize();
/**
 *
 */


//设置水位百分比
        waveview.setPrecentChangeListener(new WaveView.PrecentChangeListener() {
            @Override
            public void precentChange(double precent) {
//                tvPrecent.setText("当前进度：" + precent + "");
            }
        });

/**
 * 刷新水位
 */
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveview.reset();
                waveview.start();
            }
        });
/**
 * 业务——数据查询,查看近期加冷却液的时间
 */
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                业务——数据查询
//                waveview.stop();
            }
        });
    }
    public void initialize(){
        btStart = (Button) findViewById(R.id.bt_start);
        btStop = (Button) findViewById(R.id.bt_stop);
        waveview = (WaveView) findViewById(R.id.waveview);

        setWaterPrecent();
        waveview.reset();
        waveview.start();
    }
    public void setWaterPrecent(){
        /**
         *当前是静态数据
         */
        waterPrecent=0.3f;
        if (waterPrecent<=0.4f){//为什么不可以？？？
            Log.d(TAG,"这里");
           waveview.setTextColor(Color.RED);
        }
        waveview.setPrecent(waterPrecent);
    }
}
