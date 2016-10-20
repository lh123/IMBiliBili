package com.lh.imbilibili.view.adapter.usercenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lh.imbilibili.view.BaseFragment;

/**
 * Created by liuhui on 2016/10/16.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private BaseFragment[] mFragments;
    private String[] mTitles;

    public ViewPagerAdapter(FragmentManager fm, BaseFragment[] fragments, String[] titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
