package com.lh.imbilibili.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/8.
 */

public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private int itemHalfSpace;
    private int itemSpace;

    public LinearLayoutItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        itemSpace = itemHalfSpace * 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
        outRect.left = itemSpace;
        outRect.right = itemSpace;
        if (viewHolder.getAdapterPosition() == 0) {
            outRect.top = itemSpace;
            outRect.bottom = itemHalfSpace;
        } else {
            outRect.top = itemHalfSpace;
            outRect.bottom = itemHalfSpace;
        }
    }
}