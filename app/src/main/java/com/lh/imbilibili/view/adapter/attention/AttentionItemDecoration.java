package com.lh.imbilibili.view.adapter.attention;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/14.
 */

public class AttentionItemDecoration extends RecyclerView.ItemDecoration {
    private int itemHalfSpace;
    private int itemSpace;

    public AttentionItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        itemSpace = itemHalfSpace * 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        int type = holder.getItemViewType();
        if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_HEAD) {
            outRect.top = 0;
            outRect.bottom = itemHalfSpace;
            outRect.left = itemSpace;
            outRect.right = itemSpace;
        } else if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_ITEM) {
            outRect.top = itemHalfSpace;
            outRect.bottom = itemHalfSpace;
            int peerItemSpace = (spanCount + 1) * itemSpace / spanCount;
            if (spanIndex == 0) {
                outRect.left = itemSpace;
                outRect.right = peerItemSpace - itemSpace;
            } else if (spanIndex == spanCount - 1) {
                outRect.left = peerItemSpace - itemSpace;
                outRect.right = itemSpace;
            } else {
                outRect.left = peerItemSpace / 2;
                outRect.right = peerItemSpace / 2;
            }
        } else {
            outRect.top = itemHalfSpace;
            outRect.bottom = itemHalfSpace;
            outRect.left = itemSpace;
            outRect.right = itemSpace;
        }
    }
}
