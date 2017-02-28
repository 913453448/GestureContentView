package com.leo.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leo.library.R;
import com.leo.library.bean.GesturePoint;
import com.leo.library.bean.PointState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by leo on 17/2/27.
 */

public class GestureContentView extends ViewGroup {
    private static final int NUMBER_ROW = 3;
    private static final int NUMBER_COLUMN = 3;
    private int DEFAULT_PADDING = dp2px(10);
    private final int DEFAULT_STROKE_W = dp2px(4);
    private final int DEFAULT_STROKE_COLOR =Color.rgb(245, 142, 33);
    private final int ERRO_STROKE_COLOR =Color.RED;

    private Drawable mNormalDrawable;
    private Drawable mSelectedDrawable;
    private Drawable mErroDrawable;

    private int mRow = NUMBER_ROW;
    private int mColumn = NUMBER_COLUMN;

    private float mCellWidth;
    private float mCellHeight;

    private boolean isInitialed;

    private List<GesturePoint> points;


    private boolean isDrawEnable = true;
    private int preX, preY;
    private List<Integer> pwds;
    private Paint linePaint;
    private Canvas lineCanvas;
    private Bitmap lineBitmap;
    private int strokeColor=DEFAULT_STROKE_COLOR;
    private int erroStrokeColor=ERRO_STROKE_COLOR;
    private int strokeWidth=DEFAULT_STROKE_W;
    private List<Pair<GesturePoint, GesturePoint>> pointPairs = new ArrayList<Pair<GesturePoint, GesturePoint>>();

    private IGesturePwdCallBack gesturePwdCallBack;

    public void setGesturePwdCallBack(IGesturePwdCallBack gesturePwdCallBack) {
        this.gesturePwdCallBack = gesturePwdCallBack;
    }

    public GestureContentView(Context context) {
        this(context, null);
    }

    public GestureContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        obtainStyledAttr(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        pwds = new ArrayList<>();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setColor(strokeColor);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    private void obtainStyledAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.IndicatorView, defStyleAttr, 0);
        mNormalDrawable = a.getDrawable(R.styleable.IndicatorView_normalDrawable);
        mSelectedDrawable = a.getDrawable(R.styleable.IndicatorView_selectedDrawable);
        mErroDrawable = a.getDrawable(R.styleable.IndicatorView_erroDrawable);
        checkDrawable();
        if (a.hasValue(R.styleable.IndicatorView_row)) {
            mRow = a.getInt(R.styleable.IndicatorView_row, NUMBER_ROW);
        }
        if (a.hasValue(R.styleable.IndicatorView_column)) {
            mColumn = a.getInt(R.styleable.IndicatorView_row, NUMBER_COLUMN);
        }
        if (a.hasValue(R.styleable.IndicatorView_padding)) {
            DEFAULT_PADDING = a.getDimensionPixelSize(R.styleable.IndicatorView_padding, DEFAULT_PADDING);
        }
        strokeColor=a.getColor(R.styleable.IndicatorView_normalStrokeColor,DEFAULT_STROKE_COLOR);
        erroStrokeColor=a.getColor(R.styleable.IndicatorView_erroStrokeColor,ERRO_STROKE_COLOR);
        strokeWidth=a.getDimensionPixelSize(R.styleable.IndicatorView_strokeWidth,DEFAULT_STROKE_W);
    }

    private void checkDrawable() {
        if(mNormalDrawable==null){
            mNormalDrawable=getResources().getDrawable(R.drawable.gesture_node_normal);
        }
        if(mErroDrawable==null){
            mErroDrawable=getResources().getDrawable(R.drawable.gesture_node_wrong);
        }
        if(mSelectedDrawable==null){
            mSelectedDrawable=getResources().getDrawable(R.drawable.gesture_node_pressed);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        float result = Math.min(width, height);
        height = getHeightValue(result, heightMode);
        width = getWidthValue(result, widthMode);
        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isInitialed && getChildCount() == 0) {
            isInitialed = true;
            points = new ArrayList<>();
            addChildViews();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lineBitmap == null) {
            lineBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            lineCanvas = new Canvas(lineBitmap);
        }
        canvas.drawBitmap(lineBitmap, 0, 0, null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (points != null && points.size() > 0) {
            for (GesturePoint point : points) {
                point.layout();
            }
        }
    }

    private GesturePoint currPoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawEnable) return super.onTouchEvent(event);
        linePaint.setColor(strokeColor);
        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            changeState(PointState.POINT_STATE_NORMAL);
            preX = (int) event.getX();
            preY = (int) event.getY();
            currPoint = getPointByPosition(preX, preY);
            if (currPoint != null) {
                currPoint.setState(PointState.POINT_STATE_SELECTED);
                pwds.add(currPoint.getNum());
            }
        } else if (MotionEvent.ACTION_MOVE == action) {
            clearScreenAndDrawLine();
            GesturePoint point = getPointByPosition((int) event.getX(), (int) event.getY());
            //但没有在点的范围内的话直接返回
            if (point == null && currPoint == null) {
                return super.onTouchEvent(event);
            } else {
                if (currPoint == null) {
                    currPoint = point;
                    currPoint.setState(PointState.POINT_STATE_SELECTED);
                    pwds.add(currPoint.getNum());
                }
            }
            //当移动的不在点范围内、一直在同一个点中移动、选中了某个点后再次选中的时候不让选中（也就是不让出现重复密码）
            if (point == null || currPoint.getNum() == point.getNum() || point.getState() == PointState.POINT_STATE_SELECTED) {
                lineCanvas.drawLine(currPoint.getCenterX(), currPoint.getCenterY(), event.getX(), event.getY(), linePaint);
            } else {
                point.setState(PointState.POINT_STATE_SELECTED);
                lineCanvas.drawLine(currPoint.getCenterX(), currPoint.getCenterY(), point.getCenterX(), point.getCenterY(), linePaint);
                List<Pair<GesturePoint, GesturePoint>> betweenPoints = getBetweenPoints(currPoint, point);
                if (betweenPoints != null && betweenPoints.size() > 0) {
                    pointPairs.addAll(betweenPoints);
                    currPoint = point;
                    pwds.add(point.getNum());
                } else {
                    pointPairs.add(new Pair(currPoint, point));
                    pwds.add(point.getNum());
                    currPoint = point;
                }
            }
            invalidate();
        } else if (MotionEvent.ACTION_UP == action) {
            if (gesturePwdCallBack != null) {
                List<Integer> datas=new ArrayList<>(pwds.size());
                datas.addAll(pwds);
                gesturePwdCallBack.callBack(datas);
            }
        }
        return true;

    }

    private List<Pair<GesturePoint, GesturePoint>> getBetweenPoints(GesturePoint currPoint, GesturePoint point) {
        List<GesturePoint> points1 = new ArrayList<>();
        points1.add(currPoint);
        points1.add(point);
        Collections.sort(points1, new Comparator<GesturePoint>() {
            @Override
            public int compare(GesturePoint o1, GesturePoint o2) {
                return o1.getNum() - o2.getNum();
            }
        });
        GesturePoint maxPoint = points1.get(1);
        GesturePoint minPoint = points1.get(0);
        points1.clear();
        if (((maxPoint.getNum() - minPoint.getNum()) % Math.max(maxPoint.getPointX(), maxPoint.getPointY()) == 0)
                &&
                ((maxPoint.getPointX() - minPoint.getPointX()) > 1 ||
                        maxPoint.getPointY() - minPoint.getPointY() > 1
                )) {
            int duration = (maxPoint.getNum() - minPoint.getNum()) / Math.max(maxPoint.getPointX(), maxPoint.getPointY());
            int count = maxPoint.getPointX() - minPoint.getPointX() - 1;
            count = Math.max(count, maxPoint.getPointY() - minPoint.getPointY() - 1);
            for (int i = 0; i < count; i++) {
                int num = minPoint.getNum() + (i + 1) * duration;
                for (GesturePoint p : this.points) {
                    if (p.getNum() == num && p.getState() != PointState.POINT_STATE_SELECTED) {
                        pwds.add(p.getNum());
                        p.setState(PointState.POINT_STATE_SELECTED);
                        points1.add(p);
                    }
                }
            }
        }

        List<Pair<GesturePoint, GesturePoint>> pairs = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            GesturePoint p = points1.get(i);
            if (i == 0) {
                pairs.add(new Pair(minPoint, p));
            } else if (pairs.size() > 0) {
                pairs.add(new Pair(pairs.get(0).second, p));
            }
            if (i == points1.size() - 1) {
                pairs.add(new Pair(p, maxPoint));
            }
        }
        return pairs;
    }

    private void clearScreenAndDrawLine() {
        lineCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Pair<GesturePoint, GesturePoint> p : pointPairs) {
            lineCanvas.drawLine(p.first.getCenterX(), p.first.getCenterY(),
                    p.second.getCenterX(), p.second.getCenterY(), linePaint);
        }
    }

    private GesturePoint getPointByPosition(int preX, int preY) {
        for (GesturePoint point : points) {
            if (preX >= point.getLeftX() && preX <= point.getRightX()
                    && preY >= point.getTopY() && preY <= point.getBottomY()) {
                return point;
            }
        }
        return null;
    }

    private void addChildViews() {
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mColumn; j++) {
                GesturePoint point = new GesturePoint();
                ImageView image = new ImageView(getContext());
                point.setImageView(image);
                int left = (int) ((j + 1) * DEFAULT_PADDING + j * mCellWidth);
                int top = (int) ((i + 1) * DEFAULT_PADDING + i * mCellHeight);
                int right = (int) (left + mCellWidth);
                int bottom = (int) (top + mCellHeight);
                point.setLeftX(left);
                point.setRightX(right);
                point.setTopY(top);
                point.setBottomY(bottom);
                point.setCenterX((int) (left + mCellWidth / 2));
                point.setCenterY((int) (top + mCellHeight / 2));
                point.setNormalDrawable(mNormalDrawable);
                point.setErroDrawable(mErroDrawable);
                point.setSelectedDrawable(mSelectedDrawable);
                point.setState(PointState.POINT_STATE_NORMAL);
                point.setNum(Integer.parseInt(String.valueOf(mRow * i + j)));
                point.setPointX(i);
                point.setPointY(j);
                this.addView(image, (int) mCellWidth, (int) mCellHeight);
                points.add(point);
            }
        }
    }

    public void changePwdState(final PointState state, long delay) {
        if (delay > 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeState(state);
                }
            },delay);
        } else {
            changeState(state);
        }
    }

    private void changeState(PointState state) {
        if (state != null) {
            for (Pair<GesturePoint, GesturePoint> pair : pointPairs) {
                pair.first.setState(state);
                pair.second.setState(state);
            }
            if(PointState.POINT_STATE_NORMAL==state){
                isDrawEnable=true;
                linePaint.setColor(strokeColor);
                lineCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                pointPairs.clear();
                currPoint=null;
                pwds.clear();
            }else if(PointState.POINT_STATE_ERRO== state){
                isDrawEnable=false;
                linePaint.setColor(erroStrokeColor);
                clearScreenAndDrawLine();
            }
        }
    }

    private float getHeightValue(float height, int heightMode) {
        if (heightMode == MeasureSpec.EXACTLY) {
            mCellHeight = (height - (mColumn + 1) * DEFAULT_PADDING) / mColumn;
        } else {
            mCellHeight = Math.min(mNormalDrawable.getIntrinsicHeight(), mSelectedDrawable.getIntrinsicHeight());
            height = mCellHeight * mColumn + (mColumn + 1) * DEFAULT_PADDING;
        }
        return height;
    }

    private float getWidthValue(float width, int widthMode) {
        if (widthMode == MeasureSpec.EXACTLY) {
            mCellWidth = (width - (mRow + 1) * DEFAULT_PADDING) / mRow;
        } else {
            mCellWidth = Math.min(mNormalDrawable.getIntrinsicWidth(), mSelectedDrawable.getIntrinsicWidth());
            width = mCellWidth * mRow + (mRow + 1) * DEFAULT_PADDING;
        }
        return width;
    }

    public int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }
}
