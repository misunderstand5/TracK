package com.example.track;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.track.View.CirStatisticGraph;

public class CurrentTemperature extends AppCompatActivity {
    private CirStatisticGraph cirStatisticGraph;
    private View mCurrentTemperature;//为什么不能直接用类名
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_temperature);
        init();
    }

    /**
     * 初始化对象
     */
    public void init(){
        cirStatisticGraph = (CirStatisticGraph) findViewById(R.id.CirStatisticGraph);

        mCurrentTemperature = findViewById(R.id.view_221);
        LinearLayout layout_view=findViewById(R.id.activity_currentTemp_view);
    }
}
