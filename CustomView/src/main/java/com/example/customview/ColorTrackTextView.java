package com.example.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author fishinwater-1999
 * @version 2020-02-11
 */
public class ColorTrackTextView extends AppCompatTextView {

    public final String TAG = getClass().getName();
    /**
     * 不变色字体的画笔
     */
    private Paint mOriginPaint;
    /**
     * 变色字体的画笔
     */
    private Paint mChangePaint;

    private float mCurrentProgress = 0.5f;

    public ColorTrackTextView(Context context) {
        this(context, null);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);
    }

    /**
     * 1.1 初始化画笔
     */
    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView);

        int originColor = array.getColor(R.styleable.ColorTrackTextView_origin_color, getTextColors().getDefaultColor());
        int changeColor = array.getColor(R.styleable.ColorTrackTextView_change_color, getTextColors().getDefaultColor());

        mOriginPaint = getPaintByColor(originColor);
        mChangePaint = getPaintByColor(changeColor);

        // 回收
        array.recycle();
    }

    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        // 设置颜色
        paint.setColor(color);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        paint.setDither(true);
        // 设置字体大小
        paint.setTextSize(getTextSize());
        return paint;
    }

    /**
     * 自己画 TextView
     * 利用 clipRect api 可以裁剪
     * 左边用一个画笔去画 右边用另一个画笔去画
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 防止系统直接绘制
        // canvas.clipRect(); 裁剪区域
        // 根据进度把中间值算出来
        int middle = (int) (mCurrentProgress * getWidth());
        drawText(canvas, mOriginPaint, 0, middle);
        drawText(canvas, mChangePaint, middle, getWidth());
    }

    /**
     * 绘制 Text
     * @param canvas
     * @param paint
     * @param start
     * @param end
     */
    private void drawText(Canvas canvas, Paint paint, int start, int end) {
        canvas.save();
        // 1.1 绘制不变色
        Rect rect = new Rect(start, 0, end, getHeight());
        canvas.clipRect(rect);

        String text = getText().toString();
        // 获取字体宽度
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int dx = getWidth() / 2 - bounds.width()/2;
        // baseLine
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top)/2 - fontMetricsInt.bottom;
        int baseLine = getHeight()/2 + dy;
        canvas.drawText(text, dx, baseLine, paint);
        canvas.restore();
    }
}
