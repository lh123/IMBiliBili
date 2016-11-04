package com.lh.imbilibili.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/31.
 */

public class FlowItemDecoration extends RecyclerView.ItemDecoration {

    private int itemHalfSpace;

    public FlowItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
    }

    public FlowItemDecoration(int space) {
        itemHalfSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = itemHalfSpace;
        outRect.right = itemHalfSpace;
        outRect.top = itemHalfSpace;
        outRect.bottom = itemHalfSpace;
        outRect.top = itemHalfSpace;
        outRect.bottom = itemHalfSpace;
    }
}
