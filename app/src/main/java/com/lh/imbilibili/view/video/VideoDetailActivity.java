package com.lh.imbilibili.view.video;

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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.video.VideoDetail;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.videodetail.ViewPagerAdapter;
import com.lh.imbilibili.widget.EmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/2.
 * 视频详情界面
 */

public class VideoDetailActivity extends BaseActivity implements VideoPlayerFragment.OnVideoFragmentStateListener {

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
    private VideoPlayerFragment mVideoPlayerFragment;

    private boolean mIsFabShow;
    private boolean mIsFullScreen;
    private boolean mIsInitLayout;
    private int mCurrentSelectVideoPage;

    private Subscription mVideoDetailSub;

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
        StatusBarUtils.setCollapsingToolbarLayout(this, mToolbar);
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
        mVideoDetailSub = CommonHelper.getInstance()
                .getVideoService()
                .getVideoDetail(mAid, Constant.PLAT, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BilibiliDataResponse<VideoDetail>, Observable<VideoDetail>>() {
                    @Override
                    public Observable<VideoDetail> call(BilibiliDataResponse<VideoDetail> videoDetailBilibiliDataResponse) {
                        if (videoDetailBilibiliDataResponse.isSuccess()) {
                            return Observable.just(videoDetailBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(videoDetailBilibiliDataResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VideoDetail>() {
                    @Override
                    public void call(VideoDetail videoDetail) {
                        mVideoDetail = videoDetail;
                        RxBus.getInstance().send(new VideoStateChangeEvent(VideoStateChangeEvent.STATE_LOAD_FINISH, mVideoDetail));
                        bindViewData();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof ApiException) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            mEmptyView.setImgResource(R.drawable.img_tips_error_not_foud);
                            mEmptyView.setShowRetryButton(false);
                            mEmptyView.setText(R.string.video_load_error_404);
                        } else {
                            ToastUtils.showToastShort(R.string.load_error);
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
        if (mCurrentSelectVideoPage != page || mVideoPlayerFragment == null) {
            mCurrentSelectVideoPage = page;
            if (mVideoPlayerFragment == null) {
                initVideoView(mCurrentSelectVideoPage);
            } else {
                mVideoPlayerFragment.changeVideo(page);
            }
        }
    }

    private void initVideoView(int page) {
        initPlayerLayout();
        mVideoPlayerFragment = VideoPlayerFragment.newInstance(mVideoDetail, page);
        mVideoContainer.setVisibility(View.VISIBLE);
        mVideoPlayerFragment.setOnFullScreemButtonClick(VideoDetailActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_view_container, mVideoPlayerFragment).commit();
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
        RxBus.getInstance().send(new VideoStateChangeEvent(VideoStateChangeEvent.STATE_PLAY, mVideoDetail));
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVideoContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mVideoDetailSub);
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
        params.height = getResources().getDimensionPixelOffset(R.dimen.appbar_parallax_max_height);
        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP;
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
}
