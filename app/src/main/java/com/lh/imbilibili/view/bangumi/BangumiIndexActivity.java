package com.lh.imbilibili.view.bangumi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.home.BangumiIndexFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/10.
 */
public class BangumiIndexActivity extends BaseActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.container)
    ViewGroup mContainer;
    @BindView(R.id.drawer)
    ViewGroup mDrawer;

    private int mYear;
    private int mMonth;
    private ArrayList<Integer> mYears;

    public static void startActivity(Context context, int year, int month, ArrayList<Integer> years) {
        Intent i = new Intent(context, BangumiIndexActivity.class);
        i.putExtra("year", year);
        i.putExtra("month", month);
        i.putIntegerArrayListExtra("years", years);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_index);
        ButterKnife.bind(this);
        StatusBarUtils.setDrawerToolbarTabLayout(this, mDrawerLayout, mDrawer, mContainer);
        mYear = getIntent().getIntExtra("year", 2016);
        mMonth = getIntent().getIntExtra("month", 3);
        mYears = getIntent().getIntegerArrayListExtra("years");
        initView();
    }

    private void initView() {
        mToolbar.setTitle("全部番剧");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BangumiIndexFragment bangumiIndexFragment = (BangumiIndexFragment) getSupportFragmentManager().findFragmentByTag(BangumiIndexFragment.TAG);
        if (bangumiIndexFragment == null) {
            bangumiIndexFragment = BangumiIndexFragment.newInstance(mYear, mMonth, mYears);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bangumiIndexFragment, BangumiIndexFragment.TAG).commit();
    }
}
