package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by liuhui on 2016/7/9.
 */
public class CardFrameLayout extends CardView {

    public CardFrameLayout(Context context) {
        super(context);
        init();
    }

    public CardFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setUseCompatPadding(true);
    }
}
