package com.lh.imbilibili.view.partion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.partion.PartionViewPagerAdapter;
import com.lh.imbilibili.view.adapter.partion.model.PartionModel;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/9/29.
 * 分区Activity
 */

public class PartitionActivity extends BaseActivity {

    private static final String EXTRA_DATA = "partionModel";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private PartionModel mPartionModel;

    public static void startActivity(Context context, PartionModel partionModel) {
        Intent intent = new Intent(context, PartitionActivity.class);
        intent.putExtra(EXTRA_DATA, partionModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partionmore);
        ButterKnife.bind(this);
        StatusBarUtils.setCoordinatorToolbarTabLayout(this, mCoordinatorLayout);
        mPartionModel = getIntent().getParcelableExtra(EXTRA_DATA);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        RxBus.getInstance().register(this);
    }

    @Subscribe(scheduler = EventThread.UI)
    public void SubPartionClick(SubPartionClickEvent subPartionClickEvent){
        mViewPager.setCurrentItem(subPartionClickEvent.position + 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxBus.getInstance().unRegister(this);
    }

    private void initView() {
        List<BaseFragment> mFragments = new ArrayList<>();
        mFragments.add(PartionHomeFragment.newInstance(mPartionModel));
        for (int i = 0; i < mPartionModel.getPartions().size(); i++) {
            PartionModel.Partion partion = mPartionModel.getPartions().get(i);
            mFragments.add(PartionListFragment.newInstance(partion));
        }
        PartionViewPagerAdapter adapter = new PartionViewPagerAdapter(getSupportFragmentManager(), mFragments, mPartionModel);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mToolbar.setTitle(mPartionModel.getName());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
