package com.lh.imbilibili.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lh.imbilibili.R;

/**
 * Created by home on 2016/8/1.
 */
public class IndicatorTextView extends TextView {

    private Drawable mIndicatorDrawable;
    private boolean mDrawableBoundsChanged;

    public IndicatorTextView(Context context) {
        super(context);
        init(context, null);
    }

    public IndicatorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IndicatorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IndicatorTextView);
            mIndicatorDrawable = array.getDrawable(R.styleable.IndicatorTextView_indicatorDrawable);
            array.recycle();
            setIndicatorDrawable(mIndicatorDrawable);
        }
    }

    public void setIndicatorDrawable(Drawable drawable) {
        if (mIndicatorDrawable != drawable) {
            if (mIndicatorDrawable != null) {
                mIndicatorDrawable.setCallback(null);
                unscheduleDrawable(mIndicatorDrawable);
            }
        }
        mIndicatorDrawable = drawable;
        if (mIndicatorDrawable != null) {
            mDrawableBoundsChanged = true;
            setWillNotDraw(false);
            mIndicatorDrawable.setCallback(this);
            if (mIndicatorDrawable.isStateful()) {
                mIndicatorDrawable.setState(getDrawableState());
            }
        } else {
            setWillNotDraw(true);
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mIndicatorDrawable != null && mIndicatorDrawable.isStateful()) {
            mIndicatorDrawable.setState(getDrawableState());
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (who == mIndicatorDrawable);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mIndicatorDrawable != null) {
            mIndicatorDrawable.jumpToCurrentState();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDrawableBoundsChanged |= changed;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawableBoundsChanged = true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mIndicatorDrawable != null) {
            if (mDrawableBoundsChanged) {
                mDrawableBoundsChanged = false;
                mIndicatorDrawable.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
            }
            mIndicatorDrawable.draw(canvas);
        }
    }
}
