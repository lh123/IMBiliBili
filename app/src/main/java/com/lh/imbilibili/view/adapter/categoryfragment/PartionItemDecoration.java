package com.lh.imbilibili.view.adapter.categoryfragment;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/1.
 */

public class PartionItemDecoration extends RecyclerView.ItemDecoration {
    private int itemHalfSpace;
    private int itemSpace;

    public PartionItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        itemSpace = itemHalfSpace * 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        int position = holder.getLayoutPosition();
        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        int type = adapter.getItemViewType(position);
        if (type == PartionHomeRecyclerViewAdapter.TYPE_BANNER) {
            outRect.top = 0;
            outRect.bottom = 0;
            outRect.left = 0;
            outRect.right = 0;
        } else if (type == PartionHomeRecyclerViewAdapter.TYPE_SUB_PARTION) {
            outRect.top = 0;
            outRect.bottom = itemHalfSpace;
            outRect.left = 0;
            outRect.right = 0;
        } else if (type == PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_HEAD ||
                type == PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_HEAD ||
                type == PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_HEAD) {
            outRect.top = itemHalfSpace;
            outRect.bottom = itemHalfSpace;
            outRect.left = itemSpace;
            outRect.right = itemSpace;
        } else if (type == PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_ITEM ||
                type == PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_ITEM ||
                type == PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_ITME) {
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
        }
    }
}
