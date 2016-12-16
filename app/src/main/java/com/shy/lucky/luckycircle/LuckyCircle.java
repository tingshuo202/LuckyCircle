package com.shy.lucky.luckycircle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by holyca on 16/12/15.
 */

public class LuckyCircle extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private final SurfaceHolder mHolder;
    private boolean isRunning;
    //用于绘制的子线程
    private Thread t;
    private Canvas mCanvas;

    /**
     * 奖项
     */
    private String[] mStrs = new String[]{"单反相机", "Ipad", "恭喜发财", "Iphone",
            "服装一套", "恭喜发财"};

    /**
     * 奖项的图片
     */
    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.ipad,
            R.drawable.xialian, R.drawable.iphone, R.drawable.meizi,
            R.drawable.xialian};

    /**
     * 与图片相对应的bitmap
     */
    private Bitmap[] mBitmap;

    /**
     * 转盘的颜色
     */
    private int[] mColor = new int[]{0xffffc300, 0xfff17e01, 0xffffc300,
            0xfff17e01, 0xffffc300, 0xfff17e01};

    private int mItemCount = 6;

    /**
     * 整个转盘的矩形范围
     */
    private RectF mRange = new RectF();

    //转盘直径
    private int mRadius;

    //绘制转盘的画笔
    private Paint mArcPaint;

    //绘制文本的画笔
    private Paint mTextPaint;

    //滚动速度
    public double mSpeed = 0;

    //换盘开始的角度,保证不同线程下数据的可见性
    private volatile float mStartAngle = 0;

    //判断是否点击了停止按钮
    private boolean isShouldEnd;

    //转盘的中心位置
    private int mCenter;

    //转盘背景
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);

    //TypedValue.applyDimension是一个将各种单位的值转换为像素(px)的方法
    private float mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 20, getResources()
                    .getDisplayMetrics());
    private int mPadding;


    public LuckyCircle(Context context) {
        this(context,null);
    }

    public LuckyCircle(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        //设置使用键盘可以获取焦点
        setFocusable(true);
        //设置屏幕常亮
        setKeepScreenOn(true);
        //设置触摸能获取焦点
        setFocusableInTouchMode(true);
    }

    public boolean isRunning(){
        isShouldEnd = false;
        return mSpeed != 0;
    }

    public boolean isShouldEnd(){
        return isShouldEnd;
    }

    public void luckyEnd(){
//        mStartAngle = 0;
        isShouldEnd = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());

        mPadding = getPaddingLeft();

        mRadius = width - mPadding * 2;
         mCenter = width / 2;

        setMeasuredDimension(width,width);


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setFilterBitmap(true);

        //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mArcPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFilterBitmap(true);//对位图进行滤波处理

        //初始化圆盘的绘制范围
        mRange = new RectF(mPadding,mPadding,mPadding + mRadius,mPadding+mRadius);

        //初始化图片
        mBitmap = new Bitmap[mItemCount];
        for (int i = 0;i < mItemCount;i++){
            mBitmap[i] = BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }

        //开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        isRunning = false;
    }


    @Override
    public void run() {
        //不断绘制
        while (isRunning){
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();

            if(end - start < 50){
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();

            if(mCanvas != null){
                //绘制背景图
                drawBg();

                //绘制盘块
                float tmpAngle = mStartAngle;
                float sweepAngle = 360/mItemCount;//每个角度
                 for (int i=0;i<mItemCount;i++){
                     mArcPaint.setColor(mColor[i]);
                     /**
                      *   绘制圆弧形
                      * oval :指定圆弧的外轮廓矩形区域。
                      startAngle: 圆弧起始角度，单位为度。
                      sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。
                      useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
                      paint: 绘制圆弧的画板属性，如颜色，是否填充等。
                      */
                     mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mArcPaint);

                     //绘制文本
                     drawText(tmpAngle,sweepAngle,mStrs[i]);

                     //绘制图片
                     drawIcon(tmpAngle,mBitmap[i]);


                     tmpAngle +=sweepAngle;
                 }

//                for(int i=0;i<=mSpeed;i++){
//                    mStartAngle += i;
//                }
                mStartAngle += mSpeed;
                if(isShouldEnd){
                    mSpeed -= 1;
                }

                if(mSpeed <= 0){
                    mSpeed = 0;
                    isShouldEnd = true;
                }

            }
        }catch(Exception e){

        }finally {
            if(mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    /**
     * 绘制奖品图片
     * @param bitmap
     */
    private void drawIcon(float tmpAngle,Bitmap bitmap) {
        int imgWidth =  mRadius/8;
        /**
         * Math.sin(x)      x 的正玄值。返回值在 -1.0 到 1.0 之间；

         Math.cos(x)    x 的余弦值。返回的是 -1.0 到 1.0 之间的数；

         这两个函数中的X 都是指的“弧度”而非“角度”，弧度的计算公式为： 2*PI/360*角度；
         */
        float angle = (float) ((tmpAngle+ 180/mItemCount)*Math.PI /180);
        int x = (int) (mCenter + mRadius/4 * Math.cos(angle));
        int y = (int) (mCenter + mRadius/4 * Math.sin(angle));

        //确定图片的位置
        Rect rect = new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rect,mArcPaint);
    }

    /**
     * 绘制文本
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        //Path主要用于绘制复杂的图形轮廓，比如折线，圆弧以及各种复杂图案
        Path path = new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);

        //设置水平偏移量
        float textWidth = mTextPaint.measureText(mStr);
        int wOffset = (int) ((mRadius * Math.PI/mItemCount - textWidth)/2);

        //垂直偏移量
        int hOffset = mRadius/2/6;

        mCanvas.drawTextOnPath(mStr,path,wOffset,hOffset,mTextPaint);
    }

    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        /**
         * Bitmap bitmap：要绘制的位图对象
         Rect src： 是对图片进行裁截，若是空null则显示整个图片
         RectF dst：是图片在Canvas画布中显示的区域
         Paint paint：画笔，这个不用多说
         */
        mCanvas.drawBitmap(mBgBitmap,null,new Rect(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),mArcPaint);
    }
}
