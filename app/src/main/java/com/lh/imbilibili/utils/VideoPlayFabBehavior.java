package com.lh.imbilibili.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liuhui on 2016/10/8.
 */

public class VideoPlayFabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private boolean mIsFabShow = true;

    public VideoPlayFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            if (mIsFabShow && dependency.getY() <= -((AppBarLayout) dependency).getTotalScrollRange() / 2) {
                hideFab(child);
            } else if (!mIsFabShow && dependency.getY() > -((AppBarLayout) dependency).getTotalScrollRange() / 2) {
                showFab(child);
            }
        }
        return false;
    }

    private void showFab(final FloatingActionButton actionButton) {
        if (!mIsFabShow) {
            mIsFabShow = true;
            actionButton.post(new Runnable() {
                @Override
                public void run() {
                    actionButton.setClickable(true);
                    actionButton.animate().scaleX(1).scaleY(1).setDuration(500).start();
                }
            });
        }
    }

    private void hideFab(final FloatingActionButton actionButton) {
        if (mIsFabShow) {
            mIsFabShow = false;
            actionButton.post(new Runnable() {
                @Override
                public void run() {
                    actionButton.setClickable(false);
                    actionButton.animate().scaleX(0).scaleY(0).setDuration(500).start();
                }
            });
        }
    }
}
