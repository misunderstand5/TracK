package com.example.track.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.track.R;
import com.example.track.TemperatureData;
import com.example.track.TemperatureListViewModel;
import com.example.track.activity.CircleActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
//继承View

public class CurrentTemperatureView extends SurfaceView {
    private static final String TAG = "CurrentTemperatureView";
    /**
     * @name CenterPoint
     * @Descripation 中心点<br>
     */
    class CenterPoint {
        float x;
        float y;
    }
    /**
     * 边界宽高、中心坐标、外环和内环半径
     */
    private CenterPoint centerPoint = new CenterPoint();
    private CenterPoint centerPoint2 = new CenterPoint();
    private CenterPoint centerPoint3 = new CenterPoint();
    private float radius;//半径
    private float paintWidth;//圆环的宽度
    private float genPaintWidth;//圆环的宽度/7(多天线的宽度)
    /**
     * 几种不同的画笔
     */
    private Paint bitmap;
    private Paint defaultPaint;//
    private Paint genPaint;//
    private Paint progressTextPaint;//
    private Paint flagPaint;//

    /**
     * 进度后期需要变的数据对应每一个圆环的数据
     */
    private String curTemperature;//实时当前温度
    private float sweepAngle=0;
    private String text = "";
    /**
     * 对象类
     */
    private TemperatureListViewModel mTemperatureListViewModel;
    /**
     * 1)context先看第一个构造方法，他只有一个context，这个构造方法使用在代码中直接new出一个控件，它不附带任何自定义属性。
     *
     * 2)attrs第二个构造方法，当你在xml布局你的的时候会被调用，
     *
     * 3)defStyleRes那么好像这两个方法就够了，为什么还有另外两个构造方法呢？我认为设计师应该是从代码的易用、健壮性等方面考虑的吧。
     * 后两个方法都是为了让我们可以在外部style中直接给我们的自定义控件设置属性。
     * @param context
     */
    //1.只有Context的构造函数
    public CurrentTemperatureView(Context context) {
        super(context);
    }

    //2.含有Context和AttributeSet的构造函数

    /**
     *
     * @param context
     * @param attrs AttributeSet与自定义属性：系统自带的View可以在xml中配置属性，对于已经写好的自定义的View同样可以在xml中配置属性，
     *              为了使自定义View的属性可以在xml中配置，需要一下四个步骤：
     *              mCurrentTemperature = findViewById(R.id.view_221);就是可以通过R.id找到
     */
    public CurrentTemperatureView(Context context, @Nullable AttributeSet attrs) throws InterruptedException {
        super(context, attrs);
        initialize();
        getCurTemperature();
        start();
    }
//初始化数据变量
    public void initialize() throws InterruptedException {
//
        bitmap = new Paint();
//
        defaultPaint = new Paint();
        defaultPaint.setColor(Color.argb(0xEE, 0x8E, 0x8E, 0x8E));
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setStrokeWidth(paintWidth);
        defaultPaint.setAntiAlias(true);

        // 比重环画笔
        genPaint = new Paint();
        genPaint.setStyle(Paint.Style.STROKE);
        genPaint.setStrokeWidth(genPaintWidth);
        genPaint.setAntiAlias(true);

        // 中心进度文本和评级画笔
        progressTextPaint = new Paint();
        progressTextPaint.setColor(Color.WHITE);
        progressTextPaint.setStyle(Paint.Style.STROKE);
        progressTextPaint.setStrokeWidth(0);
        progressTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // 指示器画笔????
        flagPaint = new Paint();
        flagPaint.setColor(Color.WHITE);
        flagPaint.setStyle(Paint.Style.STROKE);
        flagPaint.setStrokeWidth(3);
        flagPaint.setAntiAlias(true);
//        对象
        mTemperatureListViewModel = new TemperatureListViewModel();
    }
        //3.重写onMesure方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize(100,widthMeasureSpec);
        int height = getMySize(100,heightMeasureSpec);
        if (width<height){
            height = width;
        }else {
            width = height;
        }
        setMeasuredDimension(width,height);
//        初始化数据

        paintWidth=10;
        radius=70;
//        改变圆形时作用改这的
        centerPoint.x=100;
        centerPoint.y=75;
        centerPoint2.x=320;
        centerPoint2.y=75;
        centerPoint3.x=540;
        centerPoint3.y=75;
        genPaintWidth = paintWidth;
        try {
            initialize();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//   centerPoint.x = 700 / 2;
////        centerPoint.y = boundsHeigh / 2;
//        centerPoint.y = 300 / 2;
//
////        radius = boundsHeigh * 1 / 3;
//        radius = 300 * 1 / 3;
//        paintWidth = 50;//???
//        genPaintWidth = paintWidth / 7;//??
//        genPaintWidth = paintWidth ;//??
    }

    //根据测量模式
    private int getMySize(int defaultSize, int measureSpec) {
        int mSize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode){
            //父容器没有对当前View有任何限制，当前View可以任意取尺寸
            case MeasureSpec.UNSPECIFIED:
                mSize = defaultSize;
                Log.d(TAG,"父容器没有对当前View有任何限制，当前View可以任意取尺寸");
                break;
            //View能取得的最大尺寸
            case MeasureSpec.AT_MOST:
                mSize = size;
                Log.d(TAG,"View能取得的最大尺寸");
                break;
            //当前的尺寸就是当前View应该取的尺寸
            case MeasureSpec.EXACTLY:
                mSize = size;
                Log.d(TAG,"当前的尺寸就是当前View应该取的尺寸在这种模式下，尺寸的值是多少，那么这个组件的长或宽就是多少。");
                break;
        }
        return mSize;
    }

    /**
     * 画布
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
         * 方法 说明 drawRect 绘制矩形 drawCircle 绘制圆形 drawOval 绘制椭圆 drawPath 绘制任意多边形
         * drawLine 绘制直线 drawPoin 绘制点
         */

        //        执行方法
        try {
            getCurTemperature();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        贴图
        canvas.drawBitmap(changeBitmapSize(), 60,180, bitmap);//????
//画实时温度的圆
        circular1(canvas);
        circular2(canvas);
        circular3(canvas);



    }


    private Bitmap changeBitmapSize() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.mid);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.e("width","width:"+width);
        Log.e("height","height:"+height);
        //设置想要的大小
        int newWidth=600;
        int newHeight=260;

        //计算压缩的比率
        float scaleWidth=((float)newWidth)/width;
        float scaleHeight=((float)newHeight)/height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        //获取新的bitmap
        bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        bitmap.getWidth();
        bitmap.getHeight();
        Log.e("newWidth","newWidth"+bitmap.getWidth());
        Log.e("newHeight","newHeight"+bitmap.getHeight());
        return bitmap;
    }

    /**
     * 提取当前温度
     */
    public void getCurTemperature() throws InterruptedException {
        Log.d("result：","getCurTemperature()");
        curTemperature= mTemperatureListViewModel.getCurTemperature();
        Log.d(TAG,"curTemperature:"+curTemperature);
    }
    /**
     *刷新view
     */
    public void start() {
        Log.d("test","start()");
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {//将curProgress加一直到加到targetProgress=88
            @Override
            public void run() {
//                curTemperature =(Integer.parseInt(curTemperature) + 1);
//                在跳转页面的时候关闭
//简单来说，invalidate()方法可以从UI线程调用，postInvalidate()可以从非UI线程调用，以告诉android在对其进行一些更改后更新我们的自定义视图。
                postInvalidate();
                Log.d(TAG,"start()_curTemperature:"+curTemperature);
            }
        };
        timer.schedule(timerTask, 0, 6000);
    }
    public void circular1(Canvas canvas){
        // 底环（灰色）
        canvas.drawCircle(centerPoint.x,centerPoint.y, radius, defaultPaint);

        // 很重要的一个半径(最外层环即里程环的半径)
        float sroundRadius = radius + paintWidth / 2 - genPaintWidth / 2;
        Log.d(TAG,"sroundRadius:"+sroundRadius);
        // 比重环（绿色24.00/黄色28.00/红色）
        genPaint.setColor(Color.argb(0xEE, 0xFF, 0x35, 0x9A));
        RectF oval1 = new RectF(centerPoint.x- sroundRadius, centerPoint.y
                - sroundRadius, centerPoint.x + sroundRadius, centerPoint.y
                + sroundRadius);
//        28.00为上限=360度

        if (Float.parseFloat(curTemperature)>=28.00F){
            text="温度警报";
            sweepAngle=360.00f;
            genPaint.setColor(Color.RED);
        }else if (28.00f>Float.parseFloat(curTemperature)&&Float.parseFloat(curTemperature)>=24.00F){
            text="温度预警";
            genPaint.setColor(Color.YELLOW);

        }
        else {
            text="温度正常";
            genPaint.setColor(Color.GREEN);
        }
        sweepAngle=360/28.00F*Float.parseFloat(curTemperature);
        canvas.drawArc(oval1, -90, sweepAngle, false, genPaint);
//画个指示圆
        float temp = sroundRadius;
        float relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint.x + relativePoint, centerPoint.y
                + relativePoint, radius / 12, flagPaint);
//
        // 连线
        float[] pts1 = new float[8];
        pts1[0] = centerPoint.x + relativePoint + radius / 24;
//         pts1[0] = centerPoint.x + relativePoint + radius / 24;
        pts1[1] = centerPoint.y + relativePoint + radius / 24;

        pts1[2] = centerPoint.x + relativePoint +40;
        pts1[3] = centerPoint.y + relativePoint + 30;

        pts1[4] = pts1[2];
        pts1[5] = pts1[3];
        pts1[6] = pts1[4] ;
        pts1[7] = pts1[5]+80;
        canvas.drawLines(pts1, flagPaint);

        // 环中心进度文本（动态迭加的）
        String curPercent = curTemperature;
        Log.d("环中心进度文本:","环中心进度文本");
        progressTextPaint.setTextSize(45);
//        Paint的measureText()方法取得字符串显示的宽度值
        float ww = progressTextPaint.measureText(curPercent + "℃");
        Log.d(TAG,"ww:"+ww);
//        根据ww计算位置
        canvas.drawText(curPercent + "℃", centerPoint.x - ww / 2,
                centerPoint.y, progressTextPaint);

        // 评级提示
        progressTextPaint.setTextSize(25);
        float w = 0;
//        String text = "";
//        text="温度正常";
        w = progressTextPaint.measureText(text);
        canvas.drawText(text, centerPoint.x - w / 2, centerPoint.y + 40,
                progressTextPaint);
    }
    public void circular2(Canvas canvas){
        // 底环（灰色）
        canvas.drawCircle(centerPoint2.x,centerPoint2.y, radius, defaultPaint);

        // 很重要的一个半径(最外层环即里程环的半径)
        float sroundRadius = radius + paintWidth / 2 - genPaintWidth / 2;
        Log.d(TAG,"sroundRadius:"+sroundRadius);
        // 比重环（绿色24.00/黄色28.00/红色）
        genPaint.setColor(Color.argb(0xEE, 0xFF, 0x35, 0x9A));
        RectF oval1 = new RectF(centerPoint2.x- sroundRadius, centerPoint2.y
                - sroundRadius, centerPoint2.x + sroundRadius, centerPoint2.y
                + sroundRadius);
//        28.00为上限=360度

        if (Float.parseFloat(curTemperature)>=28.00F){
            text="温度警报";
            sweepAngle=360.00f;
            genPaint.setColor(Color.RED);
        }else if (28.00f>Float.parseFloat(curTemperature)&&Float.parseFloat(curTemperature)>=24.00F){
            text="温度预警";
            genPaint.setColor(Color.YELLOW);

        }
        else {
            text="温度正常";
            genPaint.setColor(Color.GREEN);
        }
        sweepAngle=360/28.00F*Float.parseFloat(curTemperature);
        canvas.drawArc(oval1, -90, sweepAngle, false, genPaint);
//画个指示圆
        float temp = sroundRadius;
        float relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint2.x + relativePoint, centerPoint2.y
                + relativePoint, radius / 12, flagPaint);
//
        // 连线
        float[] pts1 = new float[8];
        pts1[0] = centerPoint2.x + radius / 24;
//         pts1[0] = centerPoint.x + relativePoint + radius / 24;
        pts1[1] = centerPoint2.y + radius + radius / 24;

        pts1[2] = pts1[0] ;
        pts1[3] = pts1[1]+ 100;

//        pts1[4] = pts1[2];
//        pts1[5] = pts1[3];
//        pts1[6] = pts1[4] ;
//        pts1[7] = pts1[5]+80;
        canvas.drawLines(pts1, flagPaint);

        // 环中心进度文本（动态迭加的）
        String curPercent = curTemperature;
        Log.d("环中心进度文本:","环中心进度文本");
        progressTextPaint.setTextSize(45);
//        Paint的measureText()方法取得字符串显示的宽度值
        float ww = progressTextPaint.measureText(curPercent + "℃");
        Log.d(TAG,"ww:"+ww);
//        根据ww计算位置
        canvas.drawText(curPercent + "℃", centerPoint2.x - ww / 2,
                centerPoint2.y, progressTextPaint);

        // 评级提示
        progressTextPaint.setTextSize(25);
        float w = 0;
//        String text = "";
//        text="温度正常";
        w = progressTextPaint.measureText(text);
        canvas.drawText(text, centerPoint2.x - w / 2, centerPoint2.y + 40,
                progressTextPaint);
    }
    public void circular3(Canvas canvas){
        // 底环（灰色）
        canvas.drawCircle(centerPoint3.x,centerPoint3.y, radius, defaultPaint);

        // 很重要的一个半径(最外层环即里程环的半径)
        float sroundRadius = radius + paintWidth / 2 - genPaintWidth / 2;
        Log.d(TAG,"sroundRadius:"+sroundRadius);
        // 比重环（绿色24.00/黄色28.00/红色）
        genPaint.setColor(Color.argb(0xEE, 0xFF, 0x35, 0x9A));
        RectF oval1 = new RectF(centerPoint3.x- sroundRadius, centerPoint3.y
                - sroundRadius, centerPoint3.x + sroundRadius, centerPoint3.y
                + sroundRadius);
//        28.00为上限=360度

        if (Float.parseFloat(curTemperature)>=28.00F){
            text="温度警报";
            sweepAngle=360.00f;
            genPaint.setColor(Color.RED);
        }else if (28.00f>Float.parseFloat(curTemperature)&&Float.parseFloat(curTemperature)>=24.00F){
            text="温度预警";
            genPaint.setColor(Color.YELLOW);

        }
        else {
            text="温度正常";
            genPaint.setColor(Color.GREEN);
        }
        sweepAngle=360/28.00F*Float.parseFloat(curTemperature);
        canvas.drawArc(oval1, -90, sweepAngle, false, genPaint);
//画个指示圆
        float temp = sroundRadius;
        float relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint3.x + relativePoint, centerPoint3.y
                + relativePoint, radius / 12, flagPaint);
//
        // 连线
        float[] pts1 = new float[8];
        pts1[0] = centerPoint3.x + radius / 24;
//         pts1[0] = centerPoint.x + relativePoint + radius / 24;
        pts1[1] = centerPoint3.y + radius + radius / 24;

        pts1[2] = pts1[0] ;
        pts1[3] = pts1[1]+ 100;

//        pts1[4] = pts1[2];
//        pts1[5] = pts1[3];
//        pts1[6] = pts1[4] ;
//        pts1[7] = pts1[5]+80;
        canvas.drawLines(pts1, flagPaint);

        // 环中心进度文本（动态迭加的）
        String curPercent = curTemperature;
        Log.d("环中心进度文本:","环中心进度文本");
        progressTextPaint.setTextSize(45);
//        Paint的measureText()方法取得字符串显示的宽度值
        float ww = progressTextPaint.measureText(curPercent + "℃");
        Log.d(TAG,"ww:"+ww);
//        根据ww计算位置
        canvas.drawText(curPercent + "℃", centerPoint3.x - ww / 2,
                centerPoint3.y, progressTextPaint);

        // 评级提示
        progressTextPaint.setTextSize(25);
        float w = 0;
//        String text = "";
//        text="温度正常";
        w = progressTextPaint.measureText(text);
        canvas.drawText(text, centerPoint3.x - w / 2, centerPoint3.y + 40,
                progressTextPaint);
    }
}

