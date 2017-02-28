package com.leo.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.leo.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 17/2/24.
 */

public class IndicatorView extends View {
    private static final int NUMBER_ROW = 3;
    private static final int NUMBER_COLUMN = 3;
    private int DEFAULT_PADDING = dp2px(10);
    private final int DEFAULT_SIZE = dp2px(40);

    private Bitmap mNormalBitmap;
    private Bitmap mSelectedBitmap;
    private int mRow = NUMBER_ROW;
    private int mColumn = NUMBER_COLUMN;

    private float mCellWidth;
    private float mCellHeight;

    private List<Integer> pwds = new ArrayList<>();
    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.IndicatorView, defStyleAttr, 0);
        mNormalBitmap = drawableToBitmap(a.getDrawable(R.styleable.IndicatorView_normalDrawable));
        mSelectedBitmap = drawableToBitmap(a.getDrawable(R.styleable.IndicatorView_selectedDrawable));
        if (a.hasValue(R.styleable.IndicatorView_row)) {
            mRow = a.getInt(R.styleable.IndicatorView_row, NUMBER_ROW);
        }
        if (a.hasValue(R.styleable.IndicatorView_column)) {
            mColumn = a.getInt(R.styleable.IndicatorView_row, NUMBER_COLUMN);
        }
        if (a.hasValue(R.styleable.IndicatorView_padding)) {
            DEFAULT_PADDING = a.getDimensionPixelSize(R.styleable.IndicatorView_padding, DEFAULT_PADDING);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        float result=Math.min(width,height);
        height = getHeightValue(result, heightMode);
        width = getWidthValue(result, widthMode);
        setMeasuredDimension((int) width, (int) height);
        //重新修改图片的size
        resizeBitmap(mCellWidth, mCellHeight);
    }

    private float getHeightValue(float height, int heightMode) {
        //当size为确定的大小的话
        //每个点的高度等于（控件的高度－（行数＋1）＊padding值）／行数
        if (heightMode == MeasureSpec.EXACTLY) {
            mCellHeight = (height - (mRow + 1) * DEFAULT_PADDING) / mRow;
        } else {
            //高度不确定的话，我们就取选中的图片跟未选中图片中的高度的最小值
            mCellHeight = Math.min(mNormalBitmap.getHeight(), mSelectedBitmap.getHeight());
            //此时控件的高度＝点的高度＊行数＋（行数＋1）＊默认padding值
            height = mCellHeight * mRow + (mRow + 1) * DEFAULT_PADDING;
        }
        return height;
    }

    private float getWidthValue(float width, int widthMode) {
        if (widthMode == MeasureSpec.EXACTLY) {
            mCellWidth = (width - (mColumn + 1) * DEFAULT_PADDING) / mColumn;
        } else {
            mCellWidth = Math.min(mNormalBitmap.getWidth(), mSelectedBitmap.getWidth());
            width = mCellWidth * mColumn + (mColumn + 1) * DEFAULT_PADDING;
        }
        return width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //遍历行数
        for (int i = 0; i < mRow; i++) {
            //遍历列数
            for (int j = 0; j < mColumn; j++) {
                float left = (j + 1) * DEFAULT_PADDING + j * mCellWidth;
                float top = (i + 1) * DEFAULT_PADDING + i * mCellHeight;
                //每个点代表的密码值＝点对应的行数值*列数＋对应的列数
                //比如3*3的表格，然后第二排的第一个＝1*3+0=3
                int num=i * mColumn + j;
                //此点是不是在传入的密码集合中？
                if (pwds!=null&&pwds.contains(num)) {
                    //这个点在传入的密码集合中的话就画一个选中的bitmap
                    canvas.drawBitmap(mSelectedBitmap, left, top, null);
                } else {
                    canvas.drawBitmap(mNormalBitmap, left, top, null);
                }
            }
        }
    }

    private void resizeBitmap(float width, float height) {
        if (width > 0 && height > 0) {
            if (mNormalBitmap.getWidth() != width || mNormalBitmap.getHeight() !=height) {
                if (mNormalBitmap.getWidth() > 0 && mNormalBitmap.getHeight() > 0) {
                    mNormalBitmap = Bitmap.createScaledBitmap(mNormalBitmap, (int) width, (int) height, false);
                }
            }
            if (mSelectedBitmap.getWidth()!=width || mSelectedBitmap.getHeight() !=height) {
                if (mSelectedBitmap.getWidth() > 0 && mSelectedBitmap.getHeight() > 0) {
                    mSelectedBitmap = Bitmap.createScaledBitmap(mSelectedBitmap, (int) width, (int) height, false);
                }
            }
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int drawableW = drawable.getIntrinsicWidth();
            int drawableH = drawable.getIntrinsicHeight();
            if (drawableW <= 0 || drawableH <= 0) {
                drawableW = DEFAULT_SIZE;
                drawableH = DEFAULT_SIZE;
            }
            bitmap = Bitmap.createBitmap(drawableW, drawableH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawableW, drawableH);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }

    public void setPwds(List<Integer> pwds) {
        if(pwds!=null)this.pwds=pwds;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}
