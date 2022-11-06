package com.example.track.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @name 自定义车辆数据统计比重环
 *    1、比重环由底环（灰色）、里程环（红色）、平均速度环（黄色）、行驶时间环（蓝色）、超速次数环（绿色）、环中间评级、指示器组成
 *    ，其中四个数据统计环和底环是同心圆、圆心处有评分文本，圆环外是四个统计指示器。<br>
 *    2、四个统计环是四个弧线，弧度由外界提供数据，并动态显示在界面上。<br>
 *    3、评分等级分为三种：未评分、正在评分、评分完成，当用户点击中间区域时开启评分，评分结束自动停止。<br>
 *    4、外侧对应的四个指示器上结构上包括：指示器位置的小圆圈、折线连接线、指示文本、文本数据显示具体的数值。<br>
 *
 * @author Freedoman
 * @date 2014-10-27
 */
public class CirStatisticGraph extends View {
    private static final String TAG = "CirStatisticGraph";

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
    private float boundsWidth;//边界宽
    private float boundsHeigh;//边界高
    private CenterPoint centerPoint = new CenterPoint();//
    private float radius;//半径
    private float paintWidth;
    private float genPaintWidth;

    /**
     * 几种不同的画笔
     */
    private Paint defaultPaint;//
    private Paint genPaint;//
    private Paint progressTextPaint;//
    private Paint flagPaint;//

    /**
     * 进度后期需要变的数据
     */
    private int curProgress;//将curProgress加一直到加到targetProgress=88
    private int targetProgress = 88;//平分
    private boolean complete;//

    private int mileage = 128;
    private int averageSpeed = 78;
    private float goTime = 1.5f;
    private int overSpeedCount = 3;

    /**
     * 构造
     *
     * @param context
     */
    public CirStatisticGraph(Context context) {
        this(context, null);
        Log.d(TAG,"CirStatisticGraph(Context context)");
    }

    public CirStatisticGraph(Context context, AttributeSet attrs) {//执行的是这个构造方法
        super(context, attrs, 0);
        Log.d(TAG,"CirStatisticGraph(Context context, AttributeSet attrs)");
    }

    public CirStatisticGraph(Context context, AttributeSet attrs, int defStyle) {//??????
        super(context, attrs, defStyle);
        this.initialize();
        Log.d(TAG,"CirStatisticGraph(Context context, AttributeSet attrs, int defStyle)");
    }

    /**
     * 初始化
     */
    private void initialize() {
        Log.d(TAG,"initialize():");
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

//
    }

    @Override//?????????初始化数据
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        System.out.println("heigth2 : " + dm2.heightPixels);
        System.out.println("width2 : " + dm2.widthPixels);

        Log.d(TAG,"onMeasure(int widthMeasureSpec, int heightMeasureSpec)");
        // 绘制区域的宽高
        boundsWidth = getWidth();
        boundsHeigh = getHeight();
//        centerPoint.x = boundsWidth / 2;//??????????????????????
        centerPoint.x = 700 / 2;
//        centerPoint.y = boundsHeigh / 2;
        centerPoint.y = 300 / 2;

//        radius = boundsHeigh * 1 / 3;
        radius = 300 * 1 / 3;
        paintWidth = 50;//???
        genPaintWidth = paintWidth / 7;
        initialize();
    }

    /**
     * 启动进度动画评分
     */
    public void start() {
        Log.d(TAG,"start()");
        curProgress = 0;
        if (targetProgress == 0) {
            targetProgress = 88;
        }
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {//将curProgress加一直到加到targetProgress=88
            @Override
            public void run() {
                curProgress++;
                Log.d(TAG,"curProgress:"+curProgress);
                if (curProgress == targetProgress) {//
                    timer.cancel();//结束定时器
                }
                postInvalidate();//告诉UITHread有改变;
            }
        };
        timer.schedule(timerTask, 0, 20);//
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {//??????????
        super.onDraw(canvas);
        Log.d(TAG,"onDraw(Canvas canvas)");
        // 底环（灰色）
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, defaultPaint);

        // 很重要的一个半径(最外层环即里程环的半径)
        float sroundRadius = radius + paintWidth / 2 - genPaintWidth / 2;
        Log.d(TAG,"sroundRadius:"+sroundRadius);
        // 里程比重环（红色）
        genPaint.setColor(Color.argb(0xEE, 0xFF, 0x35, 0x9A));
        RectF oval1 = new RectF(centerPoint.x - sroundRadius, centerPoint.y
                - sroundRadius, centerPoint.x + sroundRadius, centerPoint.y
                + sroundRadius);
        canvas.drawArc(oval1, -90, 300, false, genPaint);

        // 里程比重环的指示器位置（勾股定理计算坐标）
        float temp = sroundRadius;
        float relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint.x - relativePoint, centerPoint.y
                - relativePoint, radius / 12, flagPaint);
        // 连线
        float[] pts1 = new float[8];
        pts1[0] = centerPoint.x - relativePoint - radius / 24;
//         pts1[0] = centerPoint.x + relativePoint + radius / 24;
        pts1[1] = centerPoint.y - relativePoint - radius / 24;
        pts1[2] = centerPoint.x - relativePoint - 80;
        pts1[3] = centerPoint.y - relativePoint - 40;

        pts1[4] = pts1[2];
        pts1[5] = pts1[3];
        pts1[6] = pts1[4] - 50;
        pts1[7] = pts1[5];
        canvas.drawLines(pts1, flagPaint);

        // 文本
        progressTextPaint.setTextSize(30);
        String txt = "里程";
        float wt = progressTextPaint.measureText(txt);
        canvas.drawText(txt, pts1[6] - wt - 10, pts1[7] + 15, progressTextPaint);
        if (true) {//true-->complete//里程后期要换
            canvas.drawText(mileage + "km", pts1[6] - wt - 10, pts1[7] + 50,
                    progressTextPaint);
        }

        // 平均速度环（黄色）
        genPaint.setColor(Color.argb(0xEE, 0xF7, 0x50, 0x00));
        RectF oval2 = new RectF(centerPoint.x - sroundRadius + 2
                * genPaintWidth, centerPoint.y - sroundRadius + 2
                * genPaintWidth, centerPoint.x + sroundRadius - 2
                * genPaintWidth, centerPoint.y + sroundRadius - 2
                * genPaintWidth);
        canvas.drawArc(oval2, 0, 280, false, genPaint);

        // 平均速度环的指示器位置
        temp = sroundRadius - 2 * genPaintWidth;
        relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint.x + relativePoint, centerPoint.y
                - relativePoint, radius / 12, flagPaint);

        // 连接线
        pts1 = new float[8];
        pts1[0] = centerPoint.x + relativePoint + radius / 24;
        pts1[1] = centerPoint.y - relativePoint - radius / 24;
        pts1[2] = centerPoint.x + relativePoint + 80;
        pts1[3] = centerPoint.y - relativePoint - 40;

        pts1[4] = pts1[2];
        pts1[5] = pts1[3];
        pts1[6] = pts1[4] + 50;
        pts1[7] = pts1[5];
        canvas.drawLines(pts1, flagPaint);

        // 文本
        txt = "平均速度";
        wt = progressTextPaint.measureText(txt);
        canvas.drawText(txt, pts1[6] + 10, pts1[7] + 15, progressTextPaint);
        if (true) {//true-->complete
            canvas.drawText(averageSpeed + "km/h", pts1[6] + 10, pts1[7] + 50,
                    progressTextPaint);
        }

        // 行驶时间环（蓝色）和指示
        genPaint.setColor(Color.argb(0xEE, 0x00, 0x72, 0xE3));
        RectF oval3 = new RectF(centerPoint.x - sroundRadius + 4
                * genPaintWidth, centerPoint.y - sroundRadius + 4
                * genPaintWidth, centerPoint.x + sroundRadius - 4
                * genPaintWidth, centerPoint.y + sroundRadius - 4
                * genPaintWidth);
        canvas.drawArc(oval3, 90, 270, false, genPaint);

        // 行驶时间环指示器的位置
        temp = sroundRadius - 4 * genPaintWidth;
        relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint.x - relativePoint, centerPoint.y
                + relativePoint, radius / 12, flagPaint);

        // 连接线和文本
        pts1 = new float[8];
        pts1[0] = centerPoint.x - relativePoint - radius / 24;
        pts1[1] = centerPoint.y + relativePoint + radius / 24;
        pts1[2] = centerPoint.x - relativePoint - 80;
        pts1[3] = centerPoint.y + relativePoint + 40;

        pts1[4] = pts1[2];
        pts1[5] = pts1[3];
        pts1[6] = pts1[4] - 50;
        pts1[7] = pts1[5];
        canvas.drawLines(pts1, flagPaint);

        txt = "行驶时间";
        wt = progressTextPaint.measureText(txt);
        canvas.drawText(txt, pts1[6] - wt - 10, pts1[7] + 15, progressTextPaint);
        if (true) {//true-->complete
            canvas.drawText(goTime + "h", pts1[6] - wt - 10, pts1[7] - 20,
                    progressTextPaint);
        }

        // 超速次数环（绿色）
        genPaint.setColor(Color.argb(0xEE, 0x00, 0xEC, 0x00));
        RectF oval4 = new RectF(centerPoint.x - sroundRadius + 6
                * genPaintWidth, centerPoint.y - sroundRadius + 6
                * genPaintWidth, centerPoint.x + sroundRadius - 6
                * genPaintWidth, centerPoint.y + sroundRadius - 6
                * genPaintWidth);
        canvas.drawArc(oval4, 0, 290, false, genPaint);

        // 超速次数环指示器的位置
        temp = sroundRadius - 6 * genPaintWidth;
        relativePoint = (float) Math.sqrt(temp * temp / 2);
        canvas.drawCircle(centerPoint.x + relativePoint, centerPoint.y
                + relativePoint, radius / 12, flagPaint);

        // 连接线
        pts1 = new float[8];
        pts1[0] = centerPoint.x + relativePoint + radius / 24;
        pts1[1] = centerPoint.y + relativePoint + radius / 24;
        pts1[2] = centerPoint.x + relativePoint + 80;
        pts1[3] = centerPoint.y + relativePoint + 40;

        pts1[4] = pts1[2];
        pts1[5] = pts1[3];
        pts1[6] = pts1[4] + 50;
        pts1[7] = pts1[5];
        canvas.drawLines(pts1, flagPaint);

        // 文本
        txt = "超速次数";
        wt = progressTextPaint.measureText(txt);
        canvas.drawText(txt, pts1[6] + 10, pts1[7] + 15, progressTextPaint);
        if (true) {//true-->complete
            canvas.drawText(overSpeedCount + "次", pts1[6] + 10, pts1[7] - 20,
                    progressTextPaint);
        }

        // 环中心进度文本（动态迭加的）
        int curPercent = curProgress;
        Log.d("环中心进度文本:","环中心进度文本");
        progressTextPaint.setTextSize(60);
//        Paint的measureText()方法取得字符串显示的宽度值
        float ww = progressTextPaint.measureText(curPercent + "%");
        canvas.drawText(curPercent + "%", centerPoint.x - ww / 2,
                centerPoint.y, progressTextPaint);

        // 评级提示
        progressTextPaint.setTextSize(25);
        float w = 0;
        String text = "";
        if (curPercent == 0) {
            // 暂未评级
            text = "暂未评级";
            w = progressTextPaint.measureText(text);
            complete = false;
        } else if (curPercent < targetProgress) {
            // 评级中...
            text = "评级中...";
            w = progressTextPaint.measureText(text);
        } else if (curPercent == targetProgress) {
            // 评级完成
            text = "评级完成";
            w = progressTextPaint.measureText(text);
            complete = true;
//            postInvalidate();//一直刷新Viewinvalidate()是用来刷新View的
        }
        canvas.drawText(text, centerPoint.x - w / 2, centerPoint.y + 40,
                progressTextPaint);

    }

    /**
     * 点击评分区域，进行评分
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {//点击评分区域，进行评分
Log.d(TAG,"onTouchEvent(MotionEvent event)");
        float x = event.getX();
        float y = event.getY();

        if (x > centerPoint.x - radius && x < centerPoint.x + radius
                && y > centerPoint.y - radius && y < centerPoint.y + radius) {
            Log.i(TAG, ">>>");
            start();
        }
        return super.onTouchEvent(event);
    }
//写接口获得需要的数据
}