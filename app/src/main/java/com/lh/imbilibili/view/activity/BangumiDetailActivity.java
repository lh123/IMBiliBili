package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.model.SeasonRecommend;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.transformation.BlurTransformation;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;
import com.lh.imbilibili.utils.transformation.TopCropTransformation;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.bangumidetailactivity.BangumiEpAdapter;
import com.lh.imbilibili.view.adapter.bangumidetailactivity.BangumiRecommendAdapter;
import com.lh.imbilibili.view.adapter.bangumidetailactivity.SeasonListAdapter;
import com.lh.imbilibili.widget.FeedbackView;
import com.lh.imbilibili.widget.ForegroundLinearLayout;
import com.lh.imbilibili.widget.ScalableImageView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by home on 2016/7/30.
 * 番剧详情界面
 */
public class BangumiDetailActivity extends BaseActivity implements BangumiEpAdapter.onEpClickListener, SeasonListAdapter.OnSeasonItemClickListener, BangumiRecommendAdapter.OnBangumiRecommendItemClickListener, View.OnClickListener {

    @BindView(R.id.scroll_view)
    NestedScrollView nestedScrollView;

    @BindView(R.id.nav_top_bar)
    Toolbar toolbar;

    @BindView(R.id.cover)
    ScalableImageView ivCover;
    @BindView(R.id.background)
    ScalableImageView ivBackground;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.text1)
    TextView tvText1;
    @BindView(R.id.text2)
    TextView tvText2;
    @BindView(R.id.text3)
    TextView tvText3;

    @BindView(R.id.loading)
    ProgressBar pbLoading;

    @BindView(R.id.season_list)
    RecyclerView seasonList;

    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.grid)
    RecyclerView epRecyclerView;
    @BindView(R.id.description)
    TextView tvDescription;

    @BindView(R.id.comments)
    LinearLayout llComments;
    @BindView(R.id.season_comment_title)
    TextView tvSeasonCommentTitle;
    @BindViews({R.id.feedback1, R.id.feedback2, R.id.feedback3})
    List<FeedbackView> feedbackViews;
    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.recommend_bangumi_header)
    TextView tvRecommendBangumiHeader;
    @BindView(R.id.recommend_bangumi_content)
    RecyclerView recommendRecyclerView;
    @BindView(R.id.season_comment_title_layout)
    FrameLayout fmMoreComment;
    @BindView(R.id.more)
    ForegroundLinearLayout llMoreComment;

    private Subscription mBangumiDetailSub;
    private Subscription mFeedbackSub;
    private Subscription mSeasonRecommendSub;

    private BangumiDetail mBangumiDetail;
    private FeedbackData mFeedbackData;
    private SeasonRecommend mRecommendSeasons;

    private SeasonListAdapter seasonListAdapter;
    private BangumiEpAdapter bangumiEpAdapter;
    private BangumiRecommendAdapter recommendAdapter;

    private int selectEpPosition = 0;

    public static void startActivity(Context context, String seasonId) {
        Intent intent = new Intent(context, BangumiDetailActivity.class);
        intent.putExtra(Constant.QUERY_SEASON_ID, seasonId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_detail);
        ButterKnife.bind(this);
        String seasonId = getIntent().getStringExtra(Constant.QUERY_SEASON_ID);
        initToolBar();
        initView();
        loadBangumiDate(seasonId, true);
        loadBangumiRecommendDate(seasonId);
    }

    private void initToolBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            toolbar.setPadding(0, getResources().getDimensionPixelSize(R.dimen.status_bar_height),0,0);
//        }
        StatusBarUtils.setImageTransparent(this, toolbar);
        toolbar.getBackground().mutate().setAlpha(0);
        toolbar.setTitle("番剧详情");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollHeight = (int) (ivBackground.getMeasuredHeight() - toolbar.getMeasuredHeight() * 1.5);
                float percent = (float) scrollY / scrollHeight;
                int iAlpha = (int) (percent * 255);
                if (iAlpha < 0) {
                    iAlpha = 0;
                } else if (iAlpha > 255) {
                    iAlpha = 255;
                }
                toolbar.getBackground().mutate().setAlpha(iAlpha);
            }
        });
    }

    private void initView() {
        fmMoreComment.setOnClickListener(this);
        llMoreComment.setOnClickListener(this);
    }

    private void loadBangumiDate(String seasonId, final boolean loadSeasons) {
        pbLoading.setVisibility(View.VISIBLE);
        seasonList.setVisibility(loadSeasons ? View.GONE : View.VISIBLE);
        llComments.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        mBangumiDetailSub = RetrofitHelper
                .getInstance()
                .getBangumiService()
                .getBangumiDetail(seasonId, System.currentTimeMillis(), Constant.TYPE_BANGUMI)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BiliBiliResultResponse<BangumiDetail>, Observable<BangumiDetail>>() {
                    @Override
                    public Observable<BangumiDetail> call(BiliBiliResultResponse<BangumiDetail> bangumiDetailBiliBiliResultResponse) {
                        if (bangumiDetailBiliBiliResultResponse.isSuccess()) {
                            return Observable.just(bangumiDetailBiliBiliResultResponse.getResult());
                        } else {
                            return Observable.error(new ApiException(bangumiDetailBiliBiliResultResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BangumiDetail>() {
                    @Override
                    public void call(BangumiDetail bangumiDetail) {
                        mBangumiDetail = bangumiDetail;
                        setBangumiDetailData();
                        if (bangumiDetail.getEpisodes().size() > 0) {
                            loadFeedbackDate(bangumiDetail.getEpisodes().get(0).getAvId(),
                                    bangumiDetail.getEpisodes().get(0).getIndex());
                        }
                        if (bangumiDetail.getSeasons().size() > 1 && loadSeasons) {
                            initSeasonsList();
                        }
                    }
                });
    }

    private void loadFeedbackDate(String avId, final String index) {
        mFeedbackSub = RetrofitHelper
                .getInstance()
                .getReplyService()
                .getFeedback(1, avId, 1, 3, 2, 1)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BilibiliDataResponse<FeedbackData>, Observable<FeedbackData>>() {
                    @Override
                    public Observable<FeedbackData> call(BilibiliDataResponse<FeedbackData> feedbackDataBilibiliDataResponse) {
                        if (feedbackDataBilibiliDataResponse.isSuccess()) {
                            return Observable.just(feedbackDataBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(feedbackDataBilibiliDataResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FeedbackData>() {
                    @Override
                    public void call(FeedbackData feedbackData) {
                        mFeedbackData = feedbackData;
                        setFeedbackData(index);
                    }
                });
    }

    private void loadBangumiRecommendDate(String seasonId) {
        tvRecommendBangumiHeader.setVisibility(View.GONE);
        recommendRecyclerView.setVisibility(View.GONE);
        mSeasonRecommendSub = RetrofitHelper
                .getInstance()
                .getBangumiService()
                .getSeasonRecommend(seasonId, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BiliBiliResultResponse<SeasonRecommend>, Observable<SeasonRecommend>>() {
                    @Override
                    public Observable<SeasonRecommend> call(BiliBiliResultResponse<SeasonRecommend> seasonRecommendBiliBiliResultResponse) {
                        if (seasonRecommendBiliBiliResultResponse.isSuccess()) {
                            return Observable.just(seasonRecommendBiliBiliResultResponse.getResult());
                        } else {
                            return Observable.error(new ApiException(seasonRecommendBiliBiliResultResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SeasonRecommend>() {
                    @Override
                    public void call(SeasonRecommend seasonRecommend) {
                        mRecommendSeasons = seasonRecommend;
                        if (mRecommendSeasons.getList().size() > 0) {
                            setBangumiRecommendDate();
                        }
                    }
                });
    }

    private void setBangumiRecommendDate() {
        tvRecommendBangumiHeader.setVisibility(View.VISIBLE);
        recommendRecyclerView.setVisibility(View.VISIBLE);
        if (recommendAdapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            recommendAdapter = new BangumiRecommendAdapter(this, mRecommendSeasons.getList());
            recommendRecyclerView.setLayoutManager(gridLayoutManager);
            recommendRecyclerView.addItemDecoration(new GridLayoutItemDecoration(this, false));
            recommendRecyclerView.setHasFixedSize(true);
            recommendRecyclerView.setNestedScrollingEnabled(false);
            recommendRecyclerView.setAdapter(recommendAdapter);
            recommendAdapter.setOnBangumiRecommendItemClickListener(this);
        } else {
            recommendAdapter.setBangumis(mRecommendSeasons.getList());
            recommendAdapter.notifyDataSetChanged();
        }
    }

    public void setFeedbackData(String index) {
        String txt = StringUtils.format("第%s话评论(%d)", index,
                mFeedbackData.getPage().getAcount());
        SpannableString spannableString = new SpannableString(txt);
        int start = txt.indexOf("(");
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), start, txt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSeasonCommentTitle.setText(spannableString);
        if (mFeedbackData.getReplies().size() >= 3) {
            llComments.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            for (int i = 0; i < 3; i++) {
                feedbackViews.get(i).setData(mFeedbackData.getReplies().get(i));
            }
        }
    }

    private void setBangumiDetailData() {
        pbLoading.setVisibility(View.GONE);
        Glide.with(BangumiDetailActivity.this).load(mBangumiDetail.getCover()).transform(new RoundedCornersTransformation(this, 5)).into(ivCover);
        Glide.with(BangumiDetailActivity.this).load(mBangumiDetail.getCover())
                .transform(new TopCropTransformation(this), new BlurTransformation(BangumiDetailActivity.this, 20))
                .into(ivBackground);
        tvTitle.setText(mBangumiDetail.getTitle());
        if ("0".equals(mBangumiDetail.getIsFinish())) {
            tvText1.setText(StringUtils.format("连载中，每周%s更新", StringUtils.str2Weekday(mBangumiDetail.getWeekday())));
        } else {
            tvText1.setText(StringUtils.format("已完结，%s话全", mBangumiDetail.getTotalCount()));
        }
        tvText2.setText(StringUtils.format("播放：%s", StringUtils.formateNumber(mBangumiDetail.getPlayCount())));
        tvText3.setText(StringUtils.format("追番：%s", StringUtils.formateNumber(mBangumiDetail.getFavorites())));
        tvDescription.setText(mBangumiDetail.getEvaluate());
        setBangumiEpRecyclerView();
    }

    private void initSeasonsList() {
        seasonList.setVisibility(View.VISIBLE);
        if (seasonListAdapter == null) {
            seasonListAdapter = new SeasonListAdapter(getApplicationContext(), mBangumiDetail.getSeasons());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            seasonList.setLayoutManager(linearLayoutManager);
            seasonList.setAdapter(seasonListAdapter);
            seasonListAdapter.selectItem(mBangumiDetail.getSeasonId());
            linearLayoutManager.scrollToPosition(seasonListAdapter.getSelectPosition());
            seasonListAdapter.setOnSeasonItemClickListener(this);
        } else {
            seasonListAdapter.setSeasons(mBangumiDetail.getSeasons());
            seasonListAdapter.notifyDataSetChanged();
        }
    }

    private void setBangumiEpRecyclerView() {
        contentLayout.setVisibility(View.VISIBLE);
        if ("1".equals(mBangumiDetail.getIsFinish())) {
            Collections.reverse(mBangumiDetail.getEpisodes());
        }
        if (bangumiEpAdapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            bangumiEpAdapter = new BangumiEpAdapter(mBangumiDetail.getEpisodes());
            epRecyclerView.addItemDecoration(new GridLayoutItemDecoration(this, false));
            epRecyclerView.setLayoutManager(gridLayoutManager);
            epRecyclerView.setAdapter(bangumiEpAdapter);
            epRecyclerView.setNestedScrollingEnabled(false);
            bangumiEpAdapter.setOnEpClickListener(this);
        } else {
            bangumiEpAdapter.setEpisodes(mBangumiDetail.getEpisodes());
            bangumiEpAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        SubscriptionUtils.unsubscribe(mBangumiDetailSub, mFeedbackSub, mSeasonRecommendSub);
        super.onDestroy();
    }

    @Override
    public void onEpClick(int position) {
        if (selectEpPosition != position) {
            selectEpPosition = position;
            loadFeedbackDate(mBangumiDetail.getEpisodes().get(position).getAvId(),
                    mBangumiDetail.getEpisodes().get(position).getIndex());
        }
        VideoPlayActivity.startVideoActivity(this, mBangumiDetail.getEpisodes().get(position).getEpisodeId(), null, mBangumiDetail.getTitle());
    }

    @Override
    public void onSeasonItemClick(int position) {
        loadBangumiDate(mBangumiDetail.getSeasons().get(position).getSeasonId(), false);
    }

    @Override
    public void onBangumiRecommendItemClick(int position) {
        BangumiDetailActivity.startActivity(this, mRecommendSeasons.getList().get(position).getSeasonId());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.season_comment_title_layout ||
                v.getId() == R.id.more) {
            FeedbackActivity.startActivity(this, selectEpPosition, mBangumiDetail);
        }
    }
}
