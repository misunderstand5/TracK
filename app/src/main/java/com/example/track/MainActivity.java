package com.example.track;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.track.R;
import com.example.track.datepicker.CustomDatePicker;
import com.example.track.datepicker.DateFormatUtils;
import com.example.track.entity.Safety;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView mTvSelectedDate, mTvSelectedTime;//可变的TextView
    private TextView mMain_textView_kip;
    private CustomDatePicker mDatePicker, mTimerPicker;//CustomDatePicker对象
    private Button mButton_skip_temperatureData;
    private LinearLayout mMain_LinearLayout;
    private LineChart chart;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private YAxis yAxis;
    private Button testButton;
    private List<Safety> mSafetyList;
    private String search_time;//时间选择器选择的时间
//    private String weeks[];//X轴()
//    private float datas[];//Y轴
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMain_LinearLayout=findViewById(R.id.LinearLayout_list_item_temperature);
        chart = (LineChart) findViewById(R.id.chart);
        findViewById(R.id.ll_date).setOnClickListener(this);//对应第一个LinearLayout控件
        mTvSelectedDate = findViewById(R.id.tv_selected_date);//后面的可改变日期
        mMain_textView_kip=findViewById(R.id.main_testView_data_skip);
//        mButton_skip_temperatureData=findViewById(R.id.temperature_data_skip);
        initDatePicker();//时间变动的方法
        try {
            ListSafety();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//设置整体背景颜色
//        mMain_LinearLayout.setBackgroundColor(Color.blue(R.id.dpv_year));
//点击跳转到全部信息的页面
//        mMain_textView_kip.setOnClickListener();
//        mButton_skip_temperatureData.setOnClickListener(new View.OnClickListener() {
        mMain_textView_kip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//可能要传数据
                Intent intent_NList = new Intent(MainActivity.this, TemperatureData.class);
                startActivity(intent_NList);
                finish();
            }
        });
    }
    @Override
    public void onClick(View v) {//????
        switch (v.getId()) {
            case R.id.ll_date:
                // 日期格式为yyyy-MM-dd
                mDatePicker.show(mTvSelectedDate.getText().toString());
                break;
//
//            case R.id.ll_time:
//                // 日期格式为yyyy-MM-dd HH:mm
//                mTimerPicker.show(mTvSelectedTime.getText().toString());
//                break;
        }
    }

    @Override
    protected void onDestroy() {//?????
        super.onDestroy();
        mDatePicker.onDestroy();
    }

    private void initDatePicker() {//????
        long beginTimestamp = DateFormatUtils.str2Long("2009-05-01", false);//设置可以选择的最早时间
        long endTimestamp = System.currentTimeMillis();//当前时间
//      初始化时间
        mTvSelectedDate.setText(DateFormatUtils.long2Str(endTimestamp, false));
        search_time= DateFormatUtils.long2Str(endTimestamp, false);
        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
//               点击的时间
                mTvSelectedDate.setText(DateFormatUtils.long2Str(timestamp, false));
                    search_time= DateFormatUtils.long2Str(timestamp, false);
                try {
                    ListSafety();
                               } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, beginTimestamp, endTimestamp);
        // false不允许点击屏幕或物理返回键关闭;true允许
        mDatePicker.setCancelable(true);
        // 不显示时和分
        mDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mDatePicker.setCanShowAnim(false);
    }
//    private void initTimerPicker() {
//        String beginTime = "2018-10-17 18:00";
//        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
//
//        mTvSelectedTime.setText(endTime);
//
//        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
//        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
//            @Override
//            public void onTimeSelected(long timestamp) {
//                mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
//            }
//        }, beginTime, endTime);
//        // 允许点击屏幕或物理返回键关闭
//        mTimerPicker.setCancelable(true);
//        // 显示时和分
//        mTimerPicker.setCanShowPreciseTime(true);
//        // 允许循环滚动
//        mTimerPicker.setScrollLoop(true);
//        // 允许滚动动画
//        mTimerPicker.setCanShowAnim(true);
//    }
//        ------------------------
    /**
     * 下面时折线图对应的方法
     */

    /**
     * 设置标题及其属性
     */
    /**
     * 设置折线图的警告线
     */
    private void SetHeightLimit(float height, String name, int color) {
        //实例化警告线，并传输高度即警告先在图表中的位置,name 警告线叫什么名字
        LimitLine limitLine = new LimitLine(height, name);
        //设置警告先的宽度
        if (name.equals("时间"))
            limitLine.setLineWidth(2f);
        else
            limitLine.setLineWidth(3f);
        //设置警告线上文本的颜色
        limitLine.setTextColor(color);
        //设置警告线的颜色
        limitLine.setLineColor(color);
        //设计警告线上文本的字体类型
        limitLine.setTypeface(Typeface.DEFAULT_BOLD);
        //设计警告线在x轴上的偏移量
//        limitLine.setXOffset();
        //应用警告线--------没有定义
        yAxis.addLimitLine(limitLine);//增加个Y轴
    }

    /**
     * 设置折线图X轴的坐标点属性
     */
    private void SetXAxis() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(2);
//        xAxis.setDrawAxisLine(true);//是否绘制坐标线。
        xAxis.setTextSize(14f);//设置字体大小
        xAxis.setTextColor(Color.BLACK);//设置字体颜色
        xAxis.setLabelRotationAngle(45f);//设置旋转角度
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//将x轴位于底部
        xAxis.setDrawGridLines(false);//绘制不绘制网格线,默认为true????平行与Y轴的线
        xAxis.setGranularity(1);//间隔1
        /**
         * 将XAxis的最小值设置为负值，可以改变距离与Y轴的距离
         */
//        weeks = new String[]{"昨天", "今天", "明天", "周五", "周六", "周天", "周天"};

        xAxis.setLabelCount(mSafetyList.size()); //设置横坐标的标点的数量
        //自定义样式
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String temp=mSafetyList.get((int) value).getInsert_time();
                temp=temp.split(" ")[1];
                temp=temp.split(":")[0]+":"+temp.split(":")[1];
                return temp;//直接返回数组的值
            }
        });
    }
    /**
     * 设置折线图两侧Y轴
     */
    private void SetYAxis() {
        chart.getAxisRight().setEnabled(false);
        yAxis = chart.getAxisLeft();//左边Y轴
//        yAxis.setLabelCount(6,true); //Y轴数改变大小6---12--18
//        yAxis.setAxisMaximum(30f);//设置Y轴显示的最大值？
//            yAxis.setAxisMinimum(0f);//设置Y轴显示的最小值
        yAxis.setTextSize(14f);//设置文本大小
//            yAxis.setXOffset(5); //设置15dp的距离Y轴与屏幕边界距离
        yAxis.setGridColor(Color.BLACK);//网格颜色（平行X轴的线）
        yAxis.setGridLineWidth(1);
//            yAxis.setDrawTopYLabelEntry(true);//???
        yAxis.setAxisLineWidth(2);
        yAxis.setAxisLineColor(Color.BLACK);
        //自定义格式
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                /**
                 * value 原来的值
                 * 这个if表示的是折线图坐标左下角的提示文字，其中if条件和你前面设置的Y轴现实的最小值相对应，
                 * 最小值为多少，value==最小值的时候返回文字提示
                 */
                String tep = value + "";//将Y轴的坐标值以字符串的形式赋值给tep
                return tep.substring(0, tep.indexOf(".")) + "℃";//截取从0到第一个小数点之间的数，并拼接上℃显示在Y轴上
            }
        });
    }

    /**
     * 设置标题及其属性
     */
    private void SetDesc() {
        //声明并初始化这个文本设置类
        Description description = new Description();
        //设置折线图的标识文本
        description.setText("刹车片温度-时间图");
        //设置折线图文本的大小
        description.setTextSize(25f);
        //设置文本样式加粗显示
        description.setTypeface(Typeface.DEFAULT_BOLD);
        //设置文本颜色
        description.setTextColor(Color.BLACK);
        //设置X轴的偏移量，float值
        description.setXOffset(10f);//?????
        //设置Y轴的偏移量，float值
        description.setYOffset(10f);
//            安卓 Android获取屏幕宽度的,高度
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
//            静态的可能有问题
        description.setPosition(550, 80);
        //最后在应用这个标题
        chart.setDescription(description);
    }

    /**
     * 创建最原始的折线图，并进行折线线条样式的设置
     */
    private void SetData() {
//        datas= new float[]{19f, 23f, 16f, 20f, 20f, 24f,40f};//直接取值-后端
        /**
         * 一般使用MPAndroidChart画图表的时候都是通过list对象来封装数据
         */
        List<Entry> entries = new ArrayList<>();
        //循环将数组中的元素放到list集合中
        for (int i = 0; i < mSafetyList.size(); i++) {
            entries.add(new Entry(i, Float.parseFloat(mSafetyList.get(i).getTemperature())));
//                entries1.add(new Entry(i, datat[i]));
        }
        //一个LineDataSet对象就是一条曲线,添加数据及图例
        lineDataSet = new LineDataSet(entries, "刹车片温度");

//            直线型还可以变换为曲线
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        //设置曲线的Mode强度，0-1
//            lineDataSet1.setCubicIntensity(0.2f);
        lineDataSet.setCubicIntensity(0.2f);
        //设置背景渐变
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillDrawable(getResources().getDrawable(R.drawable.line_gradient_bg_shape));

//----------------------------------------------？？？？？？？？？？
        //提交折线图给试图工具
         lineData = new LineData(lineDataSet);

        //应用视图
        chart.setData(lineData);
    }
    /**
     * 设置图例的样式
     */
    private void SetLegend(){
        //设置图表四个方向上的边距

        chart.setExtraOffsets(10,60,10,10);//在原有的位置空间上设mapping
//        Legend      // 图例，即标识哪一条曲线，如用红色标识电流折线，蓝色标识电压折线
        Legend legend = chart.getLegend();//获取图例管理器
        legend.setEnabled(true); //设置启不启用图例
        legend.setDrawInside(false); //设置是否将图例绘制在内部，默认为false
        legend.setTextSize(10f);
        legend.setTypeface(Typeface.DEFAULT_BOLD);//文字加粗
        legend.setFormSize(10f);//设置图例的大小
        legend.setTextColor(Color.BLUE);
        legend.setFormToTextSpace(10f);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setYOffset(10f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }
    private void setChartProperties() {
        //设置描述文本不显示
//        chart.getDescription().setEnabled(false);
        //设置是否显示表格背景
//        chart.setDrawGridBackground(true);
        //设置是否可以触摸
        chart.setTouchEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);
        //设置是否可以拖拽
        chart.setDragEnabled(true);
        //设置是否可以缩放?????
        chart.setScaleEnabled(false);//上下拉动
        chart.setDrawGridBackground(false);//???
        chart.setHighlightPerDragEnabled(true);
        chart.setPinchZoom(true);
        //设置背景颜色
//        chart.setBackgroundColor(ColorAndImgUtils.CHART_BACKGROUND_COLOR);
//        chart.setBackgroundColor(Color.BLUE);
        //设置一页最大显示个数为6，超出部分就滑动
        float ratio = (float) mSafetyList.size()/(float) 6;
        Log.d("ratio","ratio："+ratio+"mSafetyList.size():"+mSafetyList.size());
        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
        chart.zoom(0, 1f, 0, 0);//先重置
        chart.zoom(ratio,1f,0,0);
        //设置从X轴出来的动画时间
        //chart.animateX(1500);
        //设置XY轴动画
        chart.animateXY(1500,1500, Easing.EasingOption.EaseInSine, Easing.EasingOption.EaseInSine);
    }
    /**
     * 获取选择时间的ListSafety数据
     */
    private void ListSafety() throws InterruptedException {
        TemperatureListViewModel temperatureListViewModel=new TemperatureListViewModel();
        Log.d("time555","search_time:"+search_time);
        if (mSafetyList!=null){
            System.out.println("null:"+mSafetyList.size());
            mSafetyList.clear();
            System.out.println("null:"+mSafetyList.size());
        }
        mSafetyList=temperatureListViewModel.getSafetyList(search_time);
            System.out.println("null:"+mSafetyList.size());
        Log.d("time","List:"+mSafetyList);
        if (mSafetyList.toString()=="[]"||mSafetyList.size()==0){
            chart.clear();//清空上一次的图形
            chart.invalidate();//刷新图表
           return;
        }
        SetYAxis();//
        SetXAxis();
        SetData();
        setChartProperties();
        SetDesc();//折线图的标题
        SetLegend();
        SetHeightLimit(28f, "高温预警-28℃", Color.rgb(255, 0, 0));
        chart.invalidate();//图形刷新
    }

}
