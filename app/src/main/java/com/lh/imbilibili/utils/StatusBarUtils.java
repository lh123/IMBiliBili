package com.lh.imbilibili.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/9/3.
 * StatusCompact
 */
public class StatusBarUtils {

    /**
     * @param drawerLayout 侧边栏
     * @param drawer       抽屉
     * @param container    内部Fragment的容器
     */
    public static void setDrawerToolbarTabLayout(Activity activity, DrawerLayout drawerLayout, NavigationView drawer, ViewGroup container) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            drawerLayout.setStatusBarBackground(R.color.colorPrimary);
            drawer.getHeaderView(0).setPadding(0, getStatusBarHeight(), 0, 0);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            drawerLayout.setStatusBarBackground(R.color.colorPrimary);
            drawer.getHeaderView(0).setPadding(0, getStatusBarHeight(), 0, 0);
            container.setPadding(0, getStatusBarHeight(), 0, 0);
            setKKStatusBar(activity, R.color.colorPrimary);
        }
    }

    public static void setDrawerToolbarTabLayout(Activity activity, DrawerLayout drawerLayout, ViewGroup drawer, ViewGroup container) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.statusBar));
            drawerLayout.setStatusBarBackground(R.color.colorPrimary);
            drawer.getChildAt(0).setPadding(0, getStatusBarHeight(), 0, 0);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            drawerLayout.setStatusBarBackground(R.color.colorPrimary);
            drawer.getChildAt(0).setPadding(0, getStatusBarHeight(), 0, 0);
            container.setPadding(0, getStatusBarHeight(), 0, 0);
            setKKStatusBar(activity, R.color.colorPrimary);
        }
    }

    public static void setImageTransparent(Activity activity, Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.statusBar));
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            params.height += getStatusBarHeight();
            toolbar.setLayoutParams(params);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            params.height += getStatusBarHeight();
            toolbar.setLayoutParams(params);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            setKKStatusBar(activity, R.color.statusBar);
        }
    }

    public static void setCoordinatorToolbarTabLayout(Activity activity, CoordinatorLayout coordinatorLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            coordinatorLayout.setPadding(0, getStatusBarHeight(), 0, 0);
            setKKStatusBar(activity, R.color.colorPrimary);
        }
    }

    public static void setCollapsingToolbarLayout(Activity activity, Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = getStatusBarHeight();
            toolbar.setLayoutParams(params);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = getStatusBarHeight();
            toolbar.setLayoutParams(params);
        }
    }

    public static void setSimpleToolbarLayout(Activity activity, Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            params.height += getStatusBarHeight();
            toolbar.setLayoutParams(params);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            setKKStatusBar(activity, R.color.colorPrimaryDark);
        }
    }

    public static void setSearchActivity(Activity activity) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewGroup contentLayout = (ViewGroup) activity.findViewById(android.R.id.content);
            contentLayout.setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }

    public static void setStatusBarLighMode(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                int pre = activity.getWindow().getDecorView().getSystemUiVisibility();
                activity.getWindow().getDecorView().setSystemUiVisibility(pre | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                int pre = activity.getWindow().getDecorView().getSystemUiVisibility();
                activity.getWindow().getDecorView().setSystemUiVisibility(pre & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private static void setKKStatusBar(Activity activity, int statusBarColor) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View mStatusBarView = decorView.findViewById(R.id.status_bar_view);
        //改变颜色时避免重复添加statusBarView
        if (mStatusBarView != null) {
            mStatusBarView.setBackgroundColor(ContextCompat.getColor(activity, statusBarColor));
            return;
        }
        mStatusBarView = new View(activity);
        mStatusBarView.setId(R.id.status_bar_view);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight());
        mStatusBarView.setBackgroundColor(ContextCompat.getColor(activity, statusBarColor));
        decorView.addView(mStatusBarView, lp);
    }

    public static int getStatusBarHeight() {
        int resourceId = IMBilibiliApplication.getApplication().getResources().getIdentifier("status_bar_height", "dimen", "android");
        return IMBilibiliApplication.getApplication().getResources().getDimensionPixelSize(resourceId);
    }
}
