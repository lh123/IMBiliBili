package com.lh.imbilibili.view.adapter.seasongroupactivity;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by home on 2016/8/8.
 */
public class SeasonGroupItemDecoration extends RecyclerView.ItemDecoration {
    private int itemHalfSpace;
    private int itemSpace;

    public SeasonGroupItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        itemSpace = itemHalfSpace * 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        SeasonGroupAdapter adapter = (SeasonGroupAdapter) parent.getAdapter();
        RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        int spanCount = gridLayoutManager.getSpanCount();
        int position = viewHolder.getAdapterPosition();
        outRect.top = itemHalfSpace;
        outRect.bottom = itemHalfSpace;
        if (adapter.getItemViewType(position) == SeasonGroupAdapter.SEASON_ITEM) {
//            outRect.left = itemSpace - itemSpace * spanIndex / spanCount;
            outRect.left = itemSpace * (spanCount - spanIndex) / spanCount;
            outRect.right = itemSpace * (spanIndex + 1) / spanCount;
        } else {
            outRect.left = itemSpace;
            outRect.right = itemSpace;
        }
    }
}
