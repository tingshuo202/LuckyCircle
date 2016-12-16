# LuckyCircle
使用surfaceview绘制实现转动的抽奖转盘。
view是在ui线程中进行绘制，surfaceview是在子线程中进行绘制，避免造成ui线程的阻塞。surfaceview中，通过getHolder方法获取SurfaceHolder，再获取canvas，绘制抽奖转盘。生命周期：
surfaceCreated（初始化画笔，开启子线程进行绘制）、surfaceChanged、surfaceDestroyed。绘制过程中，需要对圆盘背景、不同的盘块、奖品文字和奖品图片分别进行绘制。
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
