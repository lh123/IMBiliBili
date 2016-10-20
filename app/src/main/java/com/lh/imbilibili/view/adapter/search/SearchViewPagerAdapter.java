package com.lh.imbilibili.view.adapter.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lh.imbilibili.view.BaseFragment;

import java.util.List;

/**
 * Created by liuhui on 2016/10/5.
 */

public class SearchViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;
    private String[] mTitles;

    public SearchViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<BaseFragment> fragments, String[] titles) {
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("get" + position);
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        if (mFragments == null) {
            return 0;
        }
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles == null) {
            return "";
        }
        return mTitles[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
}
