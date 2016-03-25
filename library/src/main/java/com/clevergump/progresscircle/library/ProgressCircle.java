package com.clevergump.progresscircle.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.clevergump.progresscircle.library.utils.DensityUtils;


/**
 * 能显示加载进度的圆.
 */
public class ProgressCircle extends View {

    /*---------------------------- 常量 -------------------------------*/

    private static final String TAG = ProgressCircle.class.getSimpleName();
    // 默认宽高的数值, 单位dp.
    public static final int DEF_SIZE_IN_DP = 50;
    // 圆边框的默认宽度, 单位dp.
    public static final int DEF_CIRCLE_BORDER_WIDTH_IN_DP = 3;
    // 圆边框的默认颜色
    private static final int DEF_CIRCLE_BORDER_COLOR = Color.parseColor("#33B5E5");
    // 圆内扇形的默认颜色
    private static final int DEF_INNER_PIE_COLOR = Color.parseColor("#33B5E5");
    ;
    // 扇形的最大进度, 进度达到最大进度时, 这个扇形就是一个圆.
    private static final int DEF_MAX_PROGRESS = 100;
    // 扇形的默认绘制进度.
    private static final int DEF_PROGRESS = 0;
    // 绘制扇形的默认起始角度.
    private static final float DEF_PIE_STARGING_ANGLE = -90;

    /*--------------------------- 普通字段 -------------------------------*/

    // 绘制圆的画笔
    private Paint mBorderPaint;
    // 绘制圆内扇形的画笔
    private Paint mContentPaint;
    // 绘制进度百分比文字描述的画笔
    private Paint mProgressTextPaint;

    // 默认宽高的变量
    private float mDefSize;
    // 圆内画弧线时的矩形外框.
    private RectF mInnerArcRectF;

    // 绘制圆时, 调用 Canvas.drawCircle()方法时需要传入的半径值.
    private float mCircleRadius;

    /*--------------------------- 代表自定义属性的字段 ----------------------*/

    // 圆边框的颜色
    private int mCircleBorderColor;
    // 圆边框的宽度
    private float mCircleBorderWidth;
    // 圆的外边框的半径
    private float mCircleOuterRadius;
    // 圆的内边框的半径
    private float mCircleInnerRadius;
    // 内部扇形的颜色
    private int mInnerPieColor;
    // 内部扇形的最大进度. 达到最大进度时, 内部的扇形其实是一个圆形.
    private int mInnerPieMaxProgress;
    // 内部扇形当前的绘制进度.
    private int mInnerPieProgress;
    // 绘制扇形的起始角度
    private float mInnerPieStartingAngle;
    // 实际宽高的一半, 通常用来和用户设置的圆的外边框的半径进行比较, 然后选择二者中的较小者作为圆的外边框半径的实际值.
    private int mHalfSize;

    /*---------------------------------------------------------------------*/

    public ProgressCircle(Context context) {
        this(context, null);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 设置当前进度
     *
     * @param progress    当前进度
     * @param maxProgress 最大进度
     */
    public void setProgress(int progress, int maxProgress) {
        if (progress > maxProgress) {
            Log.w(TAG, "Progress can't exceed max progress");
            return;
        }
        if (progress < 0) {
            Log.w(TAG, "Progress can't be less than zero");
            return;
        }
        if (maxProgress < 0) {
            Log.w(TAG, "Max progress can't be less than zero");
            return;
        }
        mInnerPieMaxProgress = maxProgress;
        setProgress(progress);
    }

    /**
     * 设置当前进度.
     *
     * @param progress 当前进度
     */
    public void setProgress(int progress) {
        if (progress < 0) {
            Log.w(TAG, "Max progress can't be less than zero");
            return;
        }
        if (progress > mInnerPieMaxProgress) {
            Log.w(TAG, "Max progress can't be less than zero");
            return;
        }
        mInnerPieProgress = progress;
        if (isMainThread()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    /**
     * 获取当前进度.
     *
     * @return
     */
    public int getProgress() {
        return mInnerPieProgress;
    }

    /**
     * 获取最大进度.
     *
     * @return
     */
    public int getMaxProgress() {
        return mInnerPieMaxProgress;
    }

    /**
     * 将当前进度清零.
     */
    public void resetProgress() {
        mInnerPieProgress = 0;
    }

    /*--------------------------- 私有方法 ---------------------------------*/

    /**
     * 进行所有初始化的操作
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initDefValues(context);
        initCustomAttrs(context, attrs, defStyleAttr);
        initActualValues();
        initPaint();
    }

    /**
     * 初始化各个默认值
     *
     * @param context
     */
    private void initDefValues(Context context) {
        mDefSize = DensityUtils.dip2px(context, DEF_SIZE_IN_DP);

        mCircleBorderColor = DEF_CIRCLE_BORDER_COLOR;
        mCircleBorderWidth = DensityUtils.dip2px(getContext(), DEF_CIRCLE_BORDER_WIDTH_IN_DP);
        mCircleOuterRadius = mDefSize / 2;
        mInnerPieColor = DEF_INNER_PIE_COLOR;
        mInnerPieMaxProgress = DEF_MAX_PROGRESS;
        mInnerPieProgress = DEF_PROGRESS;
        mInnerPieStartingAngle = DEF_PIE_STARGING_ANGLE;
    }

    /**
     * 获取自定义属性的值
     *
     * @param context
     * @param attrs
     */
    private void initCustomAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressCircle, defStyleAttr, 0);
        int indexCount = a.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = a.getIndex(i);

            // Resource IDs cannot be used in a switch statement in Android library modules.
            // Validates using resource IDs in a switch statement in Android library module
            // Resource IDs are non final in the library projects since SDK tools r14,
            // means that the library code cannot treat these IDs as constants.

            // 在 library module 中不能使用 switch 来遍历资源文件中定义的id, 但可以转为使用if来判断.
            // http://stackoverflow.com/questions/8476912/menu-item-ids-in-an-android-library-project

            if (index == R.styleable.ProgressCircle_circleBorderColor) {
                mCircleBorderColor = a.getColor(index, DEF_CIRCLE_BORDER_COLOR);
            }
            if (index == R.styleable.ProgressCircle_circleBorderWidth) {
                // getDimension(), getDimensionPixelSize(), getDimensionPixelOffset()的对比:
                // 不同点:
                //      getDimension()方法返回 float.
                //      getDimensionPixelSize(), getDimensionPixelOffset()方法都返回 int. (一个四舍五入, 一个直接舍弃小数部分).
                // 相同点: 都会将我们设置的dp为单位的数值自动转换为以px为单位的数值, 所以无需我们操心了, 具体看源码.
                mCircleBorderWidth = a.getDimension(index, mCircleBorderWidth);
            }
            if (index == R.styleable.ProgressCircle_circleOuterRadius) {
                mCircleOuterRadius = a.getDimension(index, mCircleOuterRadius);
            }
            if (index == R.styleable.ProgressCircle_innerPieColor) {
                mInnerPieColor = a.getColor(index, DEF_INNER_PIE_COLOR);
            }
            if (index == R.styleable.ProgressCircle_innerPieMaxProgress) {
                // getInt() 和 getInteger()方法的区别:
                //  getInt(): 如果实际设置的不是整数, 那么将会调用 Integer.decode(String) 将设置的数值强制转换为int值, 而不抛异常.
                //  getInteger(): 如果实际设置的不是整数, 将会抛异常.
                mInnerPieMaxProgress = a.getInt(index, DEF_MAX_PROGRESS);
            }
            if (index == R.styleable.ProgressCircle_innerPieProgress) {
                mInnerPieProgress = a.getInt(index, DEF_PROGRESS);
            }
            if (index == R.styleable.ProgressCircle_innerPieStartingAngle) {
                mInnerPieStartingAngle = a.getFloat(index, DEF_PIE_STARGING_ANGLE);
            }
        }

        a.recycle();
    }

    /**
     * 初始化一些实际使用的变量的数值, 这些变量通常是由多个自定义变量经过组合计算得到的.
     */
    private void initActualValues() {
        mCircleRadius = mCircleOuterRadius - mCircleBorderWidth / 2;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        initBorderPaint();
        initContentPaint();
        initProgressTextPaint();
    }

    /**
     * 初始化绘制边框的画笔
     */
    private void initBorderPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mCircleBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mCircleBorderWidth);
    }

    /**
     * 初始化绘制圆内扇形的画笔
     */
    private void initContentPaint() {
        mContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContentPaint.setColor(mInnerPieColor);
        mContentPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 初始化绘制进度百分比文字描述的画笔
     */
    private void initProgressTextPaint() {
        mProgressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTextPaint.setColor(Color.BLACK);
        mProgressTextPaint.setTextSize(DensityUtils.sp2px(getContext(), 15));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 为了保证圆的外边框半径不能超过控件本身的1/2尺寸, 并且圆的边框厚度不能超过圆的外边框半径,
        // 需要重新计算相关数值.
        recalcValues();
        drawCircle(canvas);
        drawInnerPie(canvas);
        drawProgressPercentText(canvas);
    }

    /**
     * 绘制扇形外边的圆
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        // 圆心的两个坐标也是相对于该控件自身左上角的点的距离, 不是相对于其父控件左上角的点的距离.
        canvas.drawCircle(mHalfSize, mHalfSize, mCircleRadius, mBorderPaint);
    }

    /**
     * 绘制圆内的扇形
     *
     * @param canvas
     */
    private void drawInnerPie(Canvas canvas) {
        if (mInnerArcRectF == null) {
            float innerCircleOffset = mHalfSize - mCircleInnerRadius;
            mInnerArcRectF = new RectF(innerCircleOffset, innerCircleOffset,
                    getWidth() - innerCircleOffset, getHeight() - innerCircleOffset);
        }
        float sweepAngle = 1.0f * 360 * mInnerPieProgress / mInnerPieMaxProgress;
        // 绘制弧线/扇形时的坐标, 或者外围矩形的坐标是相对于该控件自身左上角的点的距离, 不是相对于其父控件左上角的点的距离.
        canvas.drawArc(mInnerArcRectF, mInnerPieStartingAngle, sweepAngle, true, mContentPaint);
    }

    /**
     * 绘制进度百分比的文字
     *
     * @param canvas
     */
    private void drawProgressPercentText(Canvas canvas) {
        String progressPercentText;
        if (mInnerPieMaxProgress == 100) {
            progressPercentText = mInnerPieProgress + "%";
        } else {
            // 获取以100为最大进度时的当前进度值.
            int convertedProgress = (int) (1.0f * mInnerPieProgress / mInnerPieMaxProgress * 100);
            progressPercentText = convertedProgress + "%";
        }
        // 当文字刚好处于垂直居中时的基准线X坐标值
        // Paint.measureText(String): 获取给定文字的宽度
        float baselineX = canvas.getWidth() / 2 - mProgressTextPaint.measureText(progressPercentText) / 2;
        // 当文字刚好处于垂直居中时的基准线Y坐标值(这个坐标对应的水平线一般都是位于水平居中线的下方, 可以自己推算).
        //      关于文字的 ascent, descent, baseline, top, bottom 的知识以及该计算式请见爱哥的文章:
        //      自定义控件其实很简单1/4 (http://blog.csdn.net/aigestudio/article/details/41447349)
        float baselineY = canvas.getHeight() / 2 - (mProgressTextPaint.ascent() + mProgressTextPaint.descent()) / 2;
        canvas.drawText(progressPercentText, baselineX, baselineY, mProgressTextPaint);
    }

    /**
     * 为了保证圆的外边框半径不能超过控件本身的1/2尺寸, 并且圆的边框厚度不能超过圆的外边框半径, 需要重新计算
     * 相关数值, 例如: 实际的外边框半径, 实际的内边框半径, 实际使用 drawCircle()方法绘制圆时需要传入的半径等.
     */
    private void recalcValues() {
        // 实际宽高的1/2
        mHalfSize = getWidth() >> 1;
        // 重新计算圆的外边框半径, 确保其值不能超过该控件本身的1/2尺寸.
        mCircleOuterRadius = Math.min(mHalfSize, mCircleOuterRadius);
        // 重新计算圆的边框厚度, 确保其值不能超过圆的外边框半径.
        mCircleBorderWidth = Math.min(mCircleOuterRadius, mCircleBorderWidth);

        // 重新计算使用 drawCircle()方法绘制圆时需要传入的半径值
        mCircleRadius = mCircleOuterRadius - mCircleBorderWidth / 2;
        // 重新计算圆的内边框的半径
        mCircleInnerRadius = mCircleRadius - mCircleBorderWidth / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int measuredWidth = widthSpecSize;
        int measuredHeight = heightSpecSize;

        // 如果layout文件中设置layout_width 为 wrap_content, 那么就使用默认的宽度
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            measuredWidth = (int) (mDefSize + 0.5f);
        }
        // 如果layout文件中设置layout_height 为 wrap_content, 那么就使用默认的高度
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            measuredHeight = (int) (mDefSize + 0.5f);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return
     */
    private boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }
}