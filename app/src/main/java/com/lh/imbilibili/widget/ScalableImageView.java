package com.lh.imbilibili.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/7/9.
 */
public class ScalableImageView extends ImageView {

    private int widthRatio = 0;
    private int heightRatio = 0;

    public ScalableImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScalableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public int getWidthRatio() {
        return widthRatio;
    }

    public void setWidthRatio(int widthRatio) {
        this.widthRatio = widthRatio;
    }

    public int getHeightRatio() {
        return heightRatio;
    }

    public void setHeightRatio(int heightRatio) {
        this.heightRatio = heightRatio;
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScalableImageView);
            heightRatio = typedArray.getInt(R.styleable.ScalableImageView_aspectRadioHeight, 0);
            widthRatio = typedArray.getInt(R.styleable.ScalableImageView_aspectRadioWidth, 0);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (heightRatio != 0 && widthRatio != 0) {
            int width = getMeasuredWidth();
            int height = (int) ((float) width * heightRatio / widthRatio);
            setMeasuredDimension(width, height);
        }
    }
}
