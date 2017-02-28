package com.leo.library.bean;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;


/**
 * Created by leo on 17/2/27.
 */

public class GesturePoint {
    private int leftX;
    private int topY;
    private int rightX;
    private int bottomY;
    private int centerX;
    private int centerY;
    private int pointX;
    private int pointY;
    private ImageView imageView;
    private PointState state;
    private int num;
    private Drawable normalDrawable;
    private Drawable erroDrawable;
    private Drawable selectedDrawable;

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public Drawable getNormalDrawable() {
        return normalDrawable;
    }

    public void setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;
    }

    public Drawable getErroDrawable() {
        return erroDrawable;
    }

    public void setErroDrawable(Drawable erroDrawable) {
        this.erroDrawable = erroDrawable;
    }

    public Drawable getSelectedDrawable() {
        return selectedDrawable;
    }

    public void setSelectedDrawable(Drawable selectedDrawable) {
        this.selectedDrawable = selectedDrawable;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getLeftX() {
        return leftX;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public int getTopY() {
        return topY;
    }

    public void setTopY(int topY) {
        this.topY = topY;
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setBottomY(int bottomY) {
        this.bottomY = bottomY;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public PointState getState() {
        return state;
    }

    public void setState(PointState state) {
        if (imageView != null) {
            this.state = state;
            this.state = state;
            if (PointState.POINT_STATE_NORMAL == state) {
                if (normalDrawable != null) imageView.setImageDrawable(normalDrawable);
            } else if (PointState.POINT_STATE_ERRO == state) {
                if (erroDrawable != null) imageView.setImageDrawable(erroDrawable);
            } else if (PointState.POINT_STATE_SELECTED == state) {
                if (selectedDrawable!=null) imageView.setImageDrawable(selectedDrawable);
            }
        }
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void layout() {
        if (this.imageView != null) {
            this.imageView.layout(leftX, topY, rightX, bottomY);
        }
    }
}
