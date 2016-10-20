package com.lh.imbilibili.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuhui on 2016/7/5.
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getContentView(), container, false);
            initView(mRootView);
        }
        return mRootView;
    }

    protected abstract void initView(View view);

    protected abstract int getContentView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootView != null && mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
    }

    public String getTitle() {
        return "";
    }
}
