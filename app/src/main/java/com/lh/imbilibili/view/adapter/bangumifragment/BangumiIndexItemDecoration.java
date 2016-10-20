package com.lh.imbilibili.view.adapter.bangumifragment;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BangumiIndexItemDecoration extends RecyclerView.ItemDecoration {
    private int itemHalfSpace;
    private int itemSpace;

    public BangumiIndexItemDecoration(Context context) {
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
        if (adapter.getItemViewType(position) == BangumiAdapter.BANNER) {
            outRect.top = 0;
            outRect.bottom = itemHalfSpace;
            outRect.left = 0;
            outRect.right = 0;
        } else if (adapter.getItemViewType(position) == BangumiAdapter.NAV ||
                adapter.getItemViewType(position) == BangumiAdapter.SERIALIZING_HEAD ||
                adapter.getItemViewType(position) == BangumiAdapter.SEASON_BANGUMI_HEAD ||
                adapter.getItemViewType(position) == BangumiAdapter.BANGUMI_RECOMMEND_HEAD ||
                adapter.getItemViewType(position) == BangumiAdapter.BANGUMI_RECOMMEND_ITEM) {
            outRect.top = itemHalfSpace;
            outRect.bottom = itemHalfSpace;
            outRect.left = itemSpace;
            outRect.right = itemSpace;
        } else if (adapter.getItemViewType(position) == BangumiAdapter.SERIALIZING_GRID_ITEM ||
                adapter.getItemViewType(position) == BangumiAdapter.SEASON_BANGUMI_ITEM) {
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
