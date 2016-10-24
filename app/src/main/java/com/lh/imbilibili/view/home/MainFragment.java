package com.lh.imbilibili.view.home;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserDetailInfo;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.MainViewPagerAdapter;
import com.lh.imbilibili.view.search.SearchActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.lh.imbilibili.widget.BiliBiliSearchView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/7/6.
 */
public class MainFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener, BiliBiliSearchView.OnSearchListener {

    public static final String TAG = "MainFragment";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.avatar)
    ImageView mIvAvatar;
    @BindView(R.id.account_badge)
    ImageView mIvAccountBadge;
    @BindView(R.id.notice_badge)
    ImageView mIvNoticeBadge;
    @BindView(R.id.nick_name)
    TextView mTvNickName;
    @BindView(R.id.navigation)
    View mDrawHome;

    private List<BaseFragment> fragments;
    private MainViewPagerAdapter adapter;
    private BiliBiliSearchView mSearchView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusUtils.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtils.getBus().unregister(this);
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mToolbar.inflateMenu(R.menu.main_menu);
        mToolbar.setOnMenuItemClickListener(this);
        StatusBarUtils.setDrawerToolbarTabLayout(getActivity(), mCoordinatorLayout);
        fragments = new ArrayList<>();
        fragments.add(BangumiFragment.newInstance());
        fragments.add(CategoryFragment.newInstance());
        fragments.add(AttentionFragment.newInstance());
        adapter = new MainViewPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);
        initToolbar();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_main_home;
    }


    private void initToolbar() {
        mIvAvatar.setImageResource(R.drawable.bili_default_avatar);
        if (UserManagerUtils.getInstance().getUserDetailInfo() != null) {
            bindUserInfoView(UserManagerUtils.getInstance().getUserDetailInfo());
        } else {
            mTvNickName.setText("未登录");
            mIvAccountBadge.setVisibility(View.GONE);
        }
        mSearchView = BiliBiliSearchView.newInstance();
        mSearchView.setHint(getResources().getString(R.string.search_hint));
        mSearchView.setOnSearchListener(this);
        mDrawHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IDrawerLayoutActivity) {
                    ((IDrawerLayoutActivity) getActivity()).openDrawer();
                }
            }
        });
    }

    @Subscribe
    public void bindUserInfoView(UserDetailInfo detailInfo) {
        Glide.with(this).load(detailInfo.getFace()).transform(new CircleTransformation(getContext().getApplicationContext())).into(mIvAvatar);
        mTvNickName.setText(detailInfo.getUname());
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            mSearchView.show(getChildFragmentManager(), "");
        }
        return true;
    }

    @Override
    public void onSearch(String keyWord) {
        if (keyWord.matches("^av\\d+$")) {
            VideoDetailActivity.startActivity(getContext(), keyWord.replaceAll("av", ""));
        } else {
            SearchActivity.startActivity(getContext(), keyWord);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
