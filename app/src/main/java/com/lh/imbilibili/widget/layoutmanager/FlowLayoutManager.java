package com.lh.imbilibili.widget.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuhui on 2016/10/27.
 * 流式布局管理器
 */

public class FlowLayoutManager extends RecyclerView.LayoutManager {

    private int mOffsetY;

    private int mTotalHeight;
    private int mViewHeight;

    private SparseArray<Rect> mChildFrames;
    private Rect mDisPlayFrame;
    private Rect mWindowsFrame;
    private SparseBooleanArray mItemsAttach;

    public FlowLayoutManager() {
        setAutoMeasureEnabled(true);
        mChildFrames = new SparseArray<>();
        mDisPlayFrame = new Rect();
        mWindowsFrame = new Rect();
        mItemsAttach = new SparseBooleanArray();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (state.isPreLayout() || state.getItemCount() <= 0) {
            mOffsetY = 0;
            return;
        }
        int currentX = 0;
        int currentY = 0;
        for (int i = 0; i < state.getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            Rect childFrame = mChildFrames.get(i);
            if (childFrame == null) {
                childFrame = new Rect();
            }
            int cWidth = getDecoratedMeasuredWidth(view);
            int cHeight = getDecoratedMeasuredHeight(view);
            if (currentX + cWidth > getWidth()) {
                currentX = cWidth;
                currentY += cHeight;
                childFrame.set(0, currentY, cWidth, currentY + cHeight);
            } else {
                childFrame.set(currentX, currentY, currentX + cWidth, currentY + cHeight);
                currentX += cWidth;
            }
            detachAndScrapView(view, recycler);
            mChildFrames.put(i, childFrame);
            mItemsAttach.put(i, false);
        }
        Rect lastFrame = mChildFrames.get(getItemCount() - 1);
        mTotalHeight = Math.max(lastFrame.bottom, getHeight());
        if (getHeightMode() == View.MeasureSpec.UNSPECIFIED) {//未指定高度，高度无限制
            mViewHeight = mTotalHeight;
        } else {
            mViewHeight = getHeight();
        }
        fillAndRecycler(recycler, state);
    }

    private void fillAndRecycler(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }
        mDisPlayFrame.set(0, mOffsetY, getWidth(), mOffsetY + mViewHeight);
        mWindowsFrame.set(0, 0, getWidth(), mViewHeight);
        Rect childFrame = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            childFrame.left = view.getLeft();
            childFrame.top = getDecoratedTop(view);
            childFrame.right = getDecoratedRight(view);
            childFrame.bottom = getDecoratedBottom(view);
            if (!Rect.intersects(mWindowsFrame, childFrame)) {
                removeAndRecycleView(view, recycler);
                mItemsAttach.put(position, false);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = mChildFrames.get(i);
            if (!mItemsAttach.get(i) && Rect.intersects(mDisPlayFrame, rect)) {
                View view = recycler.getViewForPosition(i);
                measureChildWithMargins(view, 0, 0);
                addView(view);
                mItemsAttach.put(i, true);
                layoutDecoratedWithMargins(view, rect.left, rect.top - mOffsetY,
                        rect.right, rect.bottom - mOffsetY);
            }
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int willScroll = dy;
        if (mOffsetY + dy < 0) {
            willScroll = -mOffsetY;
        } else if (mOffsetY + dy > mTotalHeight - getHeight()) {
            willScroll = mTotalHeight - getHeight() - mOffsetY;
        }
        offsetChildrenVertical(-willScroll);
        mOffsetY += willScroll;
        fillAndRecycler(recycler, state);
        return willScroll;
    }
}
