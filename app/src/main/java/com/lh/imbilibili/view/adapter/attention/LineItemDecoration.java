package com.lh.imbilibili.view.adapter.attention;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.widget.LoadMoreRecyclerView;

/**
 * Created by liuhui on 2016/10/15.
 */

public class LineItemDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;

    public LineItemDecoration(int color) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(1);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getRight() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (parent.getChildViewHolder(child).getItemViewType() != LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                c.drawLine(left, child.getBottom() + lp.bottomMargin, right, child.getBottom() + lp.bottomMargin, paint);
            }
        }

    }
}
