package com.lh.imbilibili.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/17.
 */

public class EmptyView extends LinearLayout {

    private ImageView mIv;
    private LinearLayout mContainer;

    private TextView mTv;
    private Button mBtn;

    public EmptyView(Context context) {
        super(context);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        mIv = new ImageView(context);
        mContainer = new LinearLayout(context);
        mContainer.setOrientation(VERTICAL);
        LayoutParams cParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTv = new TextView(context);
        mBtn = new Button(context);
        LayoutParams ivParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams tvParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams btnParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.gravity = Gravity.CENTER_HORIZONTAL;
        int margin = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ivParams.setMargins(margin, margin, margin, margin);
        tvParams.bottomMargin = margin / 2;
        btnParams.topMargin = margin / 2;
        mTv.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
        mBtn.setBackgroundResource(R.drawable.selector_shape_blue_solid_round_rect_bg);
        mBtn.setText(R.string.reload);
        mBtn.setTextColor(Color.WHITE);
        mBtn.setVisibility(GONE);

        addView(mIv, ivParams);
        addView(mContainer, cParams);

        mContainer.addView(mTv, tvParams);
        mContainer.addView(mBtn, btnParams);
    }

    public void setImgResource(@DrawableRes int resId) {
        mIv.setImageResource(resId);
    }

    public void setText(String msg) {
        mTv.setText(msg);
    }

    public void setText(@StringRes int resId) {
        mTv.setText(resId);
    }

    public void setButtonText(@StringRes int resId) {
        mBtn.setText(resId);
    }

    public void setShowRetryButton(boolean show) {
        if (show) {
            setOrientation(HORIZONTAL);
            mBtn.setVisibility(VISIBLE);
        } else {
            setOrientation(VERTICAL);
            mBtn.setVisibility(GONE);
        }
    }

    public void setOnRetryListener(OnClickListener listener) {
        if (listener != null) {
            setShowRetryButton(true);
            mBtn.setOnClickListener(listener);
        }
    }
}
