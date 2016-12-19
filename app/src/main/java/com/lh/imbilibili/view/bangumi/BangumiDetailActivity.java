package com.lh.imbilibili.view.bangumi;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.data.helper.PlusHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.bangumi.Bangumi;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.bangumi.SeasonRecommend;
import com.lh.imbilibili.model.feedback.Feedback;
import com.lh.imbilibili.model.feedback.FeedbackData;
import com.lh.imbilibili.model.feedback.ReplyCount;
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.adapter.bangumidetail.BangumiDetailAdapter;
import com.lh.imbilibili.view.video.VideoPlayActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by home on 2016/7/30.
 * 番剧详情界面
 */
public class BangumiDetailActivity extends BaseActivity implements LoadMoreRecyclerView.OnLoadMoreLinstener, LoadMoreRecyclerView.OnLoadMoreViewClickListener, BangumiDetailAdapter.OnItemClickListener {

    private static final int PAGE_SIZE = 20;

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    @BindView(R.id.stickly_layout)
    ViewGroup mSticklyLayout;
    @BindView(R.id.head_container)
    ViewGroup mHeadContainer;
    @BindView(R.id.title)
    TextView mTvTitle;
    @BindView(R.id.addition_info)
    TextView mTvAdditionInfo;
    @BindView(R.id.sub_title)
    TextView mTvSubTitle;
    @BindView(R.id.sub_title_ico)
    ImageView mIvSubTitleIco;
    @BindView(R.id.fab_btn)
    FloatingActionButton mFab;
    @BindView(R.id.loading_view)
    ProgressBar mLoadingView;
    @BindView(R.id.bottom_tips)
    TextView mBottomTip;

    private int mHeadHeight;

    private String mSeasonId;
    private int mCurrentPage;
    private int mReplyCount;
    private boolean mFabShow;

    private BangumiDetailAdapter mAdapter;

    private Subscription mAllDataSub;
    private Subscription mFeedbackSub;
    private Subscription mLoadMoreFeedbackSub;
    private Subscription mBusSub;

    private BangumiDetail mBangumiDetail;
    private List<Bangumi> mSeasonsRecommends;
    private List<Feedback> mHotFeedback;
    private List<Feedback> mNormalFeedback;

    public static void startActivity(Context context, String seasonId) {
        Intent intent = new Intent(context, BangumiDetailActivity.class);
        intent.putExtra(Constant.QUERY_SEASON_ID, seasonId);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBusSub = RxBus.getInstance()
                .toObserverable(EpisodeChooseFragment.EpisodeClickEvent.class)
                .subscribe(new Action1<EpisodeChooseFragment.EpisodeClickEvent>() {
                    @Override
                    public void call(EpisodeChooseFragment.EpisodeClickEvent episodeClickEvent) {
                        mAdapter.setEpSelectPosition(episodeClickEvent.position);
                        onEposideSelect(episodeClickEvent.position
                                , episodeClickEvent.mode == EpisodeChooseFragment.MODE_PLAY
                                , episodeClickEvent.mode == EpisodeChooseFragment.MODE_FEEDBACK);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionUtils.unsubscribe(mBusSub);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_detail);
        ButterKnife.bind(this);
        StatusBarUtils.setImageTransparent(this, mToolbar);
        mSeasonId = getIntent().getStringExtra(Constant.QUERY_SEASON_ID);
        mCurrentPage = 1;
        mHeadHeight = DisplayUtils.dip2px(getApplicationContext(), 178);
        initToolBar();
        initFloatView();
        initRecyclerView();
        loadAllData();
    }

    private void initFloatView() {
        mSticklyLayout.setVisibility(View.GONE);
        mHeadContainer.setBackgroundColor(Color.WHITE);
        mTvTitle.setText("评论");
        mTvSubTitle.setText("选集");
        mHeadContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEpFragment(EpisodeChooseFragment.MODE_FEEDBACK);
            }
        });
        mFab.setScaleX(0);
        mFab.setScaleY(0);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                if (position > 5) {//太远直接移动
                    mRecyclerView.scrollToPosition(5);
                }
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollToPosition(0);//延迟执行
                    }
                });
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new BangumiDetailAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mRecyclerView.setOnLoadMoreViewClickListener(this);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setShowLoadingView(false);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View firstView = recyclerView.findChildViewUnder(mHeadContainer.getWidth() / 2, mToolbar.getBottom());
                    if (firstView == null) {
                        return;
                    }
                    int position = recyclerView.getChildAdapterPosition(firstView);
                    if (position < 5) {
                        hideFab();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                View firstView = recyclerView.findChildViewUnder(mHeadContainer.getWidth() / 2, mToolbar.getBottom());
                if (firstView != null) {
                    int type = recyclerView.getChildViewHolder(firstView).getItemViewType();
                    if (type >= BangumiDetailAdapter.TYPE_FEEDBACK_HEAD) {//type 是顺序排列的
                        if (firstView.getTop() < mToolbar.getBottom()) {
                            mSticklyLayout.setVisibility(View.VISIBLE);
                        }
                    } else if (type < BangumiDetailAdapter.TYPE_FEEDBACK_HEAD) {
                        if (firstView.getBottom() > mToolbar.getBottom()) {
                            mSticklyLayout.setVisibility(View.GONE);
                        }
                    }
                    if (type == BangumiDetailAdapter.TYPE_HEADER) {
                        int scrollHeight = (int) (mHeadHeight - mToolbar.getMeasuredHeight() * 1.5);
                        int scrolledY = -firstView.getTop();
                        float percent = (float) scrolledY / scrollHeight;
                        int iAlpha = (int) (percent * 255);
                        if (iAlpha < 0) {
                            iAlpha = 0;
                        } else if (iAlpha > 255) {
                            iAlpha = 255;
                        }
                        if (scrolledY < 5) {
                            hideFab();
                        }
                        if (scrolledY > firstView.getMeasuredHeight() / 4) {
                            mToolbar.setTitle(mBangumiDetail.getTitle());
                        } else {
                            mToolbar.setTitle("番剧详情");
                        }
                        mToolbar.getBackground().setAlpha(iAlpha);
                    } else {
                        mToolbar.setTitle(mBangumiDetail.getTitle());
                        mToolbar.getBackground().setAlpha(255);
                    }
                }
            }
        });
    }


    private void showFab() {
        if (mFabShow) {
            return;
        }
        mFabShow = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                if (scale == 0) {
                    mFab.setVisibility(View.VISIBLE);
                }
                mFab.setScaleX(scale);
                mFab.setScaleY(scale);
            }
        });
        animator.start();
    }

    private void hideFab() {
        if (!mFabShow) {
            return;
        }
        mFabShow = false;
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                mFab.setScaleX(scale);
                mFab.setScaleY(scale);
                if (scale < 0.1) {
                    mFab.setVisibility(View.GONE);
                }
            }
        });
        animator.start();
    }

    private void initToolBar() {
        mToolbar.getBackground().mutate().setAlpha(0);
        mToolbar.setTitle("番剧详情");
        mToolbar.getBackground().mutate();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadAllData() {
        mAllDataSub = Observable.mergeDelayError(loadBangumiAndFeedbackData(), loadBangumiRecommendData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showToastShort(R.string.load_error);
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setShowLoadingView(false);
                        finishTask();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    private Observable<Object> loadBangumiAndFeedbackData() {
        return CommonHelper
                .getInstance()
                .getBangumiService()
                .getBangumiDetail(mSeasonId, System.currentTimeMillis(), Constant.TYPE_BANGUMI)
                .flatMap(new Func1<BiliBiliResultResponse<BangumiDetail>, Observable<BiliBiliResultResponse<BangumiDetail>>>() {
                    @Override
                    public Observable<BiliBiliResultResponse<BangumiDetail>> call(BiliBiliResultResponse<BangumiDetail> bangumiDetailBiliBiliResultResponse) {
                        if (!bangumiDetailBiliBiliResultResponse.isSuccess() || bangumiDetailBiliBiliResultResponse.getResult().getEpisodes().isEmpty()) {
                            return PlusHelper.getInstance().getPlusService().getBangumiDetailFromPlus(mSeasonId)
                                    .map(new Func1<BiliBiliResultResponse<BangumiDetail>, BiliBiliResultResponse<BangumiDetail>>() {
                                        @Override
                                        public BiliBiliResultResponse<BangumiDetail> call(BiliBiliResultResponse<BangumiDetail> bangumiDetailBiliBiliResultResponse) {
                                            if (bangumiDetailBiliBiliResultResponse.isSuccess()) {
                                                bangumiDetailBiliBiliResultResponse.getResult().setCover("http:" + bangumiDetailBiliBiliResultResponse.getResult().getCover());
                                            }
                                            return bangumiDetailBiliBiliResultResponse;
                                        }
                                    });
                        } else {
                            return Observable.just(bangumiDetailBiliBiliResultResponse);
                        }
                    }
                })
                .flatMap(new Func1<BiliBiliResultResponse<BangumiDetail>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(BiliBiliResultResponse<BangumiDetail> bangumiDetailBiliBiliResultResponse) {
                        if (bangumiDetailBiliBiliResultResponse.isSuccess()) {
                            mBangumiDetail = bangumiDetailBiliBiliResultResponse.getResult();
                            if (!mBangumiDetail.getEpisodes().isEmpty()) {
                                String avId = mBangumiDetail.getEpisodes().get(0).getAvId();
                                return Observable.mergeDelayError(loadReplyCount(avId),
                                        loadFeedbackDate(avId, false), loadFeedbackDate(avId, true));
                            } else {
                                return Observable.empty();
                            }
                        } else {
                            return Observable.error(new ApiException(bangumiDetailBiliBiliResultResponse.getCode(),
                                    bangumiDetailBiliBiliResultResponse.getMessage()));
                        }
                    }
                });
    }

    private Observable<List<Bangumi>> loadBangumiRecommendData() {
        return CommonHelper
                .getInstance()
                .getBangumiService()
                .getSeasonRecommend(mSeasonId, System.currentTimeMillis())
                .flatMap(new Func1<BiliBiliResultResponse<SeasonRecommend>, Observable<List<Bangumi>>>() {
                    @Override
                    public Observable<List<Bangumi>> call(BiliBiliResultResponse<SeasonRecommend> seasonRecommendBiliBiliResultResponse) {
                        if (seasonRecommendBiliBiliResultResponse.isSuccess()) {
                            mSeasonsRecommends = seasonRecommendBiliBiliResultResponse.getResult().getList();
                            return Observable.just(mSeasonsRecommends);
                        } else {
                            return Observable.error(new ApiException(seasonRecommendBiliBiliResultResponse.getCode(),
                                    seasonRecommendBiliBiliResultResponse.getMessage()));
                        }
                    }
                });
    }


    private void finishTask() {
        mLoadingView.setVisibility(View.GONE);
        if (mSeasonsRecommends != null) {
            mAdapter.setSeasonRecommend(mSeasonsRecommends);
        }
        if (mHotFeedback != null) {
            mAdapter.setHotFeedbacks(mHotFeedback);
        }
        if (mNormalFeedback != null) {
            mAdapter.addFeedBack(mNormalFeedback);
            mCurrentPage++;
        }
        if (mBangumiDetail != null) {
            mTvAdditionInfo.setVisibility(View.VISIBLE);
            int position;
            for (position = 0; position < mBangumiDetail.getSeasons().size(); position++) {
                if (mBangumiDetail.getSeasons().get(position).getSeasonId().equals(mSeasonId)) {
                    break;
                }
            }
            mAdapter.setBangumiDetail(mBangumiDetail);
            if (!mBangumiDetail.getEpisodes().isEmpty()) {
                mTvAdditionInfo.setText(StringUtils.format("(%d)", mReplyCount));
                mAdapter.setReplyCount(mReplyCount);
                mTvTitle.setText(StringUtils.format("评论 第%s话", mBangumiDetail.getEpisodes().get(0).getIndex()));
                mRecyclerView.setEnableLoadMore(true);
                mRecyclerView.setShowLoadingView(true);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private Observable<List<Feedback>> loadFeedbackDate(String avId, final boolean noHot) {
        if (noHot) {
            mNormalFeedback = null;
        } else {
            mHotFeedback = null;
        }
        return CommonHelper
                .getInstance()
                .getReplyService()
                .getFeedback(noHot ? 1 : 0, avId, mCurrentPage, noHot ? PAGE_SIZE : 3, noHot ? 0 : 2, 1)
                .flatMap(new Func1<BilibiliDataResponse<FeedbackData>, Observable<List<Feedback>>>() {
                    @Override
                    public Observable<List<Feedback>> call(BilibiliDataResponse<FeedbackData> feedbackDataBilibiliDataResponse) {
                        if (feedbackDataBilibiliDataResponse.isSuccess()) {
                            if (!noHot) {
                                mHotFeedback = feedbackDataBilibiliDataResponse.getData().getHots();
                                return Observable.just(mHotFeedback);
                            } else {
                                mNormalFeedback = feedbackDataBilibiliDataResponse.getData().getReplies();
                                return Observable.just(mNormalFeedback);
                            }
                        } else {
                            return Observable.error(new ApiException(feedbackDataBilibiliDataResponse.getCode(),
                                    feedbackDataBilibiliDataResponse.getMessage()));
                        }
                    }
                });
    }

    private Observable<Integer> loadReplyCount(String id) {
        return CommonHelper.getInstance()
                .getReplyService()
                .getReplyCount(id, 1)
                .flatMap(new Func1<BilibiliDataResponse<ReplyCount>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BilibiliDataResponse<ReplyCount> replyCountBilibiliDataResponse) {
                        if (replyCountBilibiliDataResponse.isSuccess()) {
                            mReplyCount = replyCountBilibiliDataResponse.getData().getCount();
                            return Observable.just(mReplyCount);
                        } else {
                            return Observable.error(new ApiException(replyCountBilibiliDataResponse.getCode(),
                                    replyCountBilibiliDataResponse.getMessage()));
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        SubscriptionUtils.unsubscribe(mAllDataSub, mLoadMoreFeedbackSub, mFeedbackSub);
        super.onDestroy();
    }

    @Override
    public void onLoadMore() {
        if (mCurrentPage > 1) {
            showFab();
        }
        mLoadMoreFeedbackSub = loadFeedbackDate(mBangumiDetail.getEpisodes().get(mAdapter.getEpSelectPosition()).getAvId(), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Feedback>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_RETRY);
                    }

                    @Override
                    public void onNext(List<Feedback> feedbacks) {
                        mRecyclerView.setLoading(false);
                        if (feedbacks.isEmpty()) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int preCount = mAdapter.getItemCount();
                            mAdapter.addFeedBack(feedbacks);
                            mAdapter.notifyItemRangeInserted(preCount, feedbacks.size());
                            mCurrentPage++;
                        }
                    }
                });
    }

    private void onEposideSelect(final int position, final boolean playVideo, final boolean needScroll) {
        SubscriptionUtils.unsubscribe(mAllDataSub, mLoadMoreFeedbackSub, mFeedbackSub);
        BangumiDetail.Episode episode = mBangumiDetail.getEpisodes().get(position);
        if (playVideo) {
            VideoPlayActivity.startVideoActivity(this, episode);
        }
        mCurrentPage = 1;
        mFeedbackSub = Observable.mergeDelayError(loadReplyCount(episode.getAvId()),
                loadFeedbackDate(episode.getAvId(), false),
                loadFeedbackDate(episode.getAvId(), true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        mTvTitle.setText(StringUtils.format("评论 第%s话", mBangumiDetail.getEpisodes().get(position).getIndex()));
                        mTvAdditionInfo.setText(StringUtils.format("(%d)", mReplyCount));
                        mAdapter.clearFeedback();
                        mAdapter.setHotFeedbacks(mHotFeedback);
                        mAdapter.addFeedBack(mNormalFeedback);
                        mAdapter.setReplyCount(mReplyCount);
                        mAdapter.notifyDataSetChanged();
                        if (needScroll) {
                            int headPosition = mAdapter.getItemStartPositionOfType(BangumiDetailAdapter.TYPE_FEEDBACK_HEAD);
                            if (headPosition > 0) {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                                layoutManager.scrollToPositionWithOffset(headPosition, mToolbar.getBottom());
                                mSticklyLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort(R.string.load_error);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @Override
    public void onLoadMoreViewClick() {
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_REFRESHING);
        onLoadMore();
    }

    @Override
    public void onItemClick(int type, final int position) {
        switch (type) {
            case BangumiDetailAdapter.TYPE_SEASON_LIST:
                SubscriptionUtils.unsubscribe(mAllDataSub, mLoadMoreFeedbackSub, mFeedbackSub);
                mSeasonId = mBangumiDetail.getSeasons().get(position).getSeasonId();
                mAdapter.clearAllData();
                mCurrentPage = 1;
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setShowLoadingView(false);
                mLoadingView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                loadAllData();
                break;
            case BangumiDetailAdapter.TYPE_EPOSIDE:
                onEposideSelect(position, true, false);
                break;
            case BangumiDetailAdapter.TYPE_RECOMMEND:
                BangumiDetailActivity.startActivity(this, mSeasonsRecommends.get(position).getSeasonId());
                break;
        }
    }

    @Override
    public void onHeadClick(int type) {
        if (type == BangumiDetailAdapter.TYPE_EPOSIDE) {
            showEpFragment(EpisodeChooseFragment.MODE_PLAY);
        } else if (type == BangumiDetailAdapter.TYPE_FEEDBACK_HEAD) {
            showEpFragment(EpisodeChooseFragment.MODE_FEEDBACK);
        }
    }

    @Override
    public void onHeadActionClick(View view) {
        switch (view.getId()) {
            case R.id.action_subscribe:
                concernOrUnConcernSeason();
                break;
            case R.id.action_download:
                showBottomFragment();
                break;
        }
    }

    private void concernOrUnConcernSeason() {
        if (mBangumiDetail.getUserSeason() == null) {
            return;
        }
        Observable.just(mBangumiDetail.getUserSeason().getAttention())
                .flatMap(new Func1<String, Observable<BilibiliDataResponse>>() {
                    @Override
                    public Observable<BilibiliDataResponse> call(String s) {
                        if (s.equals("1")) {
                            return CommonHelper.getInstance().getAttentionService().unConcernSeason(mBangumiDetail.getSeasonId());
                        } else {
                            return CommonHelper.getInstance().getAttentionService().concernSeason(mBangumiDetail.getSeasonId());
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BilibiliDataResponse>() {
                    @Override
                    public void onCompleted() {
                        if (mBangumiDetail.getUserSeason().getAttention().equals("1")) {
                            mBangumiDetail.getUserSeason().setAttention("0");
                            showBottomTip(R.string.bangumi_unsubscribe_success);
                        } else {
                            mBangumiDetail.getUserSeason().setAttention("1");
                            showBottomTip(R.string.bangumi_subscribe_success);
                        }
                        mAdapter.notifyItemChanged(0);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BilibiliDataResponse o) {

                    }
                });
    }

    private void showBottomTip(@StringRes int msg) {
        mBottomTip.setText(msg);
        AnimatorSet set = new AnimatorSet();
        ValueAnimator showAnimator = ValueAnimator.ofFloat(0, 1);
        showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBottomTip.setAlpha(value);
                if (value == 0) {
                    mBottomTip.setVisibility(View.VISIBLE);
                }
            }
        });
        ValueAnimator hideAnimator = ValueAnimator.ofFloat(1, 0);
        hideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBottomTip.setAlpha(value);
                if (value < 0.1) {
                    mBottomTip.setVisibility(View.GONE);
                }
            }
        });
        set.play(hideAnimator).after(1000).after(showAnimator);
        set.start();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(EpisodeChooseFragment.TAG);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return;
        }
        super.onBackPressed();
    }

    private void showEpFragment(int mode) {
        if (mBangumiDetail == null || mBangumiDetail.getEpisodes().isEmpty()) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.container, EpisodeChooseFragment
                                .newInstance(mBangumiDetail, mAdapter.getEpSelectPosition(), mode),
                        EpisodeChooseFragment.TAG)
                .commit();
    }

    private void showBottomFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, BottomSheetFragment.newInstance(),
                        BottomSheetFragment.TAG)
                .commit();
    }
}
