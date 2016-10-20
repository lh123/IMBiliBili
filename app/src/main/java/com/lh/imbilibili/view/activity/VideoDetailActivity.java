package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.VideoDetail;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.DisableableAppBarLayoutBehavior;
import com.lh.imbilibili.utils.HistoryUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.videodetailactivity.ViewPagerAdapter;
import com.lh.imbilibili.view.fragment.VideoDetailInfoFragment;
import com.lh.imbilibili.view.fragment.VideoDetailReplyFragment;
import com.lh.imbilibili.view.fragment.VideoFragment;
import com.lh.imbilibili.widget.EmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/2.
 */

public class VideoDetailActivity extends BaseActivity implements VideoFragment.OnVideoFragmentStateListener {

    private static final String EXTRA_AID = "aid";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.video_pre_view_layout)
    ViewGroup mPreViewLayout;
    @BindView(R.id.video_pre_view)
    ImageView mIvPreView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.title_layout)
    ViewGroup mTitleLayout;
    @BindView(R.id.tv_player)
    TextView mTvPlayer;
    @BindView(R.id.video_view_container)
    FrameLayout mVideoContainer;

    private ViewPagerAdapter mAdapter;

    private String mAid;
    private VideoDetail mVideoDetail;
    private List<BaseFragment> mFragments;
    private VideoFragment mVideoFragment;

    private boolean mIsFabShow;
    private boolean mIsFullScreen;
    private boolean mIsInitLayout;
    private int mVideoViewHeight;
    private int mCurrentSelectVideoPage;

    private Call<BilibiliDataResponse<VideoDetail>> mLoadVideoDetailCall;

    public static void startActivity(Context context, String aid) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(EXTRA_AID, aid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mAid = getIntent().getStringExtra(EXTRA_AID);
        ButterKnife.bind(this);
        StatusBarUtils.setCollapsingToolbarLayout(this, mToolbar, mAppBarLayout, mCollapsingToolbarLayout);
        mIsFullScreen = false;
        mIsFabShow = true;
        mIsInitLayout = false;
        mCurrentSelectVideoPage = 0;
        initView();
        loadVideoDetail();
    }

    private void initView() {
        mToolbar.setTitle(StringUtils.format("av%s", mAid));
        mCollapsingToolbarLayout.setTitleEnabled(false);
        mFloatingActionButton.setEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initVideoView(mCurrentSelectVideoPage);
            }
        });
        mTvPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initVideoView(mCurrentSelectVideoPage);
            }
        });
        mTvPlayer.setEnabled(false);
        mFragments = new ArrayList<>();
        mFragments.add(VideoDetailInfoFragment.newInstance());
        mFragments.add(VideoDetailReplyFragment.newInstance());
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void loadVideoDetail() {
        mLoadVideoDetailCall = RetrofitHelper
                .getInstance()
                .getVideService()
                .getVideoDetail(mAid, Constant.PLAT, System.currentTimeMillis());
        mLoadVideoDetailCall.enqueue(new Callback<BilibiliDataResponse<VideoDetail>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<VideoDetail>> call, Response<BilibiliDataResponse<VideoDetail>> response) {
                if (response.body().getCode() == 0) {
                    mVideoDetail = response.body().getData();
                    BusUtils.getBus().post(new VideoStateChangeEvent(VideoStateChangeEvent.STATE_LOAD_FINISH, mVideoDetail));
                    bindViewData();
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setImgResource(R.drawable.img_tips_error_not_foud);
                    mEmptyView.setShowRetryButton(false);
                    mEmptyView.setText(R.string.video_load_error_404);
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<VideoDetail>> call, Throwable t) {
                ToastUtils.showToast(VideoDetailActivity.this, R.string.load_error, Toast.LENGTH_SHORT);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setImgResource(R.drawable.img_tips_error_load_error);
                mEmptyView.setText(R.string.video_load_error_failed);
                mEmptyView.setShowRetryButton(true);
                mEmptyView.setOnRetryListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEmptyView.setVisibility(View.GONE);
                        loadVideoDetail();
                    }
                });
            }
        });
    }

    private void bindViewData() {
        mFloatingActionButton.setEnabled(true);
        mTvPlayer.setEnabled(true);
        Glide.with(this).load(mVideoDetail.getPic()).centerCrop().into(mIvPreView);
    }

    private void hidFab() {
        if (mIsFabShow) {
            mIsFabShow = false;
            mFloatingActionButton.setClickable(false);
            mFloatingActionButton.animate().scaleX(0).scaleY(0).setDuration(500).start();
        }
    }

    public void changeVideoPage(int page) {
        if (mCurrentSelectVideoPage != page || mVideoFragment == null) {
            mCurrentSelectVideoPage = page;
            if (mVideoFragment == null) {
                initVideoView(mCurrentSelectVideoPage);
            } else {
                mVideoFragment.changeVideo(mAid, mVideoDetail.getPages().get(page).getCid() + "", mVideoDetail.getTitle());
            }
        }
    }

    private void initVideoView(final int page) {
        initPlayerLayout();
        HistoryUtils.addHistory(mAid);
        mVideoFragment = VideoFragment.newInstance(mAid, mVideoDetail.getPages().get(page).getCid() + "", mVideoDetail.getTitle());
        mVideoContainer.setVisibility(View.VISIBLE);
        mVideoFragment.setOnFullScreemButtonClick(VideoDetailActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_view_container, mVideoFragment).commit();
    }

    private void initPlayerLayout() {
        if (mIsInitLayout) {
            return;
        }
        mIsInitLayout = true;
        mAppBarLayout.setExpanded(true);
        hidFab();
        mPreViewLayout.setVisibility(View.INVISIBLE);
        mToolbar.setVisibility(View.INVISIBLE);
        BusUtils.getBus().post(new VideoStateChangeEvent(VideoStateChangeEvent.STATE_PLAY, mVideoDetail));
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        DisableableAppBarLayoutBehavior behavior = (DisableableAppBarLayoutBehavior) params.getBehavior();
        if (behavior != null) {
            behavior.setEnableScroll(false);
        }
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                CoordinatorLayout.LayoutParams vparams = (CoordinatorLayout.LayoutParams) mViewPager.getLayoutParams();
                vparams.setBehavior(null);
                vparams.topMargin = mAppBarLayout.getHeight();
                mViewPager.setLayoutParams(vparams);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mLoadVideoDetailCall);
    }

    @Override
    public void onFullScreenClick() {
        if (!mIsFullScreen) {
            mIsFullScreen = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mIsFullScreen = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onMediaControlBarVisibleChanged(boolean isShow) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (isShow && !mIsFullScreen) {
            mToolbar.setVisibility(View.VISIBLE);
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mToolbar.setVisibility(View.GONE);
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsFullScreen) {
            fullScreen();
        } else {
            exitFullScreen();
        }
    }

    private void fullScreen() {
        mViewPager.setVisibility(View.GONE);
        mAppBarLayout.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mVideoContainer.getLayoutParams();
        mVideoViewHeight = params.height;
        params.height = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        mVideoContainer.setLayoutParams(params);
        mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void exitFullScreen() {
        mViewPager.setVisibility(View.VISIBLE);
        mAppBarLayout.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mVideoContainer.getLayoutParams();
        params.height = mVideoViewHeight;
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        mVideoContainer.setLayoutParams(params);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen) {
            mIsFullScreen = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            exitFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    public static class VideoStateChangeEvent {

        public static final int STATE_PLAY = 1;
        public static final int STATE_STOP = 2;
        public static final int STATE_LOAD_FINISH = 3;

        public int state;
        public VideoDetail videoDetail;

        VideoStateChangeEvent(int state, VideoDetail videoDetail) {
            this.state = state;
            this.videoDetail = videoDetail;
        }
    }
}
