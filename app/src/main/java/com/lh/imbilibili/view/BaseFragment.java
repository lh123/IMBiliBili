package com.lh.imbilibili.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.IMBilibiliApplication;

/**
 * Created by liuhui on 2016/7/5.
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getContentView(), container, false);
        initView(rootView);
        return rootView;
    }

    protected abstract void initView(View view);

    protected abstract int getContentView();

    @Override
    public void onDestroy() {
        super.onDestroy();
        IMBilibiliApplication.getApplication().getRefWatcher().watch(this);
    }

    public String getTitle() {
        return "";
    }
}
