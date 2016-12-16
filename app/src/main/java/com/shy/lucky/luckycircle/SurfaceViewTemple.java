package com.shy.lucky.luckycircle;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by holyca on 16/12/15.
 */

public class SurfaceViewTemple extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private final SurfaceHolder mHolder;
    private boolean isRunning;
    //用于绘制的子线程
    private Thread t;
    private Canvas mCanvas;

    public SurfaceViewTemple(Context context) {
        this(context,null);
    }

    public SurfaceViewTemple(Context context, AttributeSet attrs) {
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


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

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
            draw();
        }

    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();

            if(mCanvas != null){

            }
        }catch(Exception e){

        }finally {
            if(mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }
}
