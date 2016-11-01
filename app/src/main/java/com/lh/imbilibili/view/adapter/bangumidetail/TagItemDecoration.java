package com.lh.imbilibili.view.adapter.bangumidetail;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/31.
 */

public class TagItemDecoration extends RecyclerView.ItemDecoration {

    private int itemHalfSpace;

    public TagItemDecoration(Context context) {
        itemHalfSpace = context.getResources().getDimensionPixelSize(R.dimen.item_spacing);
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
