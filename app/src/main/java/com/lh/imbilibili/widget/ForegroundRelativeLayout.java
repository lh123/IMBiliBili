package com.lh.imbilibili.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lh.imbilibili.R;

/**
 * Created by home on 2016/8/1.
 */
public class ForegroundRelativeLayout extends RelativeLayout {

    private Drawable mForeground;

    private boolean mForegroundBoundChange;

    public ForegroundRelativeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ForegroundView);
            mForeground = array.getDrawable(R.styleable.ForegroundView_android_foreground);
            array.recycle();
            setForeground(mForeground);
        }
    }

    @Override
    public void setForeground(Drawable foreground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setForeground(foreground);
        } else {
            if (mForeground != foreground) {
                if (mForeground != null) {
                    mForeground.setCallback(null);
                    unscheduleDrawable(mForeground);
                }
            }
            mForeground = foreground;
            if (mForeground != null) {
                setWillNotDraw(false);
                mForeground.setCallback(this);
                if (mForeground.isStateful()) {
                    mForeground.setState(getDrawableState());
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.verifyDrawable(who);
        } else {
            return super.verifyDrawable(who) || (who == mForeground);
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mForeground != null) {
                mForeground.jumpToCurrentState();
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mForeground != null && mForeground.isStateful()) {
                mForeground.setState(getDrawableState());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mForegroundBoundChange |= changed;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mForegroundBoundChange = true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mForeground != null) {
                if (mForegroundBoundChange) {
                    mForegroundBoundChange = false;
                    mForeground.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
                }
                mForeground.draw(canvas);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mForeground != null) {
                mForeground.setHotspot(x, y);
            }
        }
    }
}
