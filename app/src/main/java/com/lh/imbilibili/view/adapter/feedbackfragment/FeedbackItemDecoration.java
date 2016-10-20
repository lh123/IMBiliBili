package com.lh.imbilibili.view.adapter.feedbackfragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by home on 2016/8/2.
 */
public class FeedbackItemDecoration extends RecyclerView.ItemDecoration {

    private int color;
    private Paint paint;

    public FeedbackItemDecoration(int color) {
        this.color = color;
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(1);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getRight() - parent.getPaddingRight();
        final LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (lp.getViewAdapterPosition() != 2 &&
                    lp.getViewAdapterPosition() != 3) {
                c.drawLine(left, child.getBottom() + lp.bottomMargin, right, child.getBottom() + lp.bottomMargin, paint);
            }
        }

    }
}
