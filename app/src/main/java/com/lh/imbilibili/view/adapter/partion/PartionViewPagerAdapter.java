package com.lh.imbilibili.view.adapter.partion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.partion.model.PartionModel;

import java.util.List;

/**
 * Created by liuhui on 2016/9/29.
 */

public class PartionViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;
    private PartionModel mPartionModel;

    public PartionViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragments, PartionModel partionModel) {
        super(fm);
        mFragments = fragments;
        mPartionModel = partionModel;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "推荐";
        } else {
            return mPartionModel.getPartions().get(position - 1).getName();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
