package com.lh.imbilibili.view.video;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.feedback.FeedbackData;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.feedback.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedback.FeedbackItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

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
 * 视频详情评论页面
 */

public class VideoDetailReplyFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreViewClickListener, LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final int PAGE_SIZE = 20;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private int mCurrentPage;
    private Subscription mFeedbackSub;
    private Subscription mBusSub;

    private FeedbackAdapter mFeedbackAdapter;

    private String mId;

    private boolean mIsFirstLoad;

    public static VideoDetailReplyFragment newInstance() {
        return new VideoDetailReplyFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mCurrentPage = 1;
        mIsFirstLoad = true;
        mFeedbackAdapter = new FeedbackAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        FeedbackItemDecoration itemDecoration = new FeedbackItemDecoration(ContextCompat.getColor(getContext(), R.color.theme_color_dividing_line));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mFeedbackAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBusSub = RxBus.getInstance()
                .toObserverable(VideoStateChangeEvent.class)
                .subscribe(new Action1<VideoStateChangeEvent>() {
                    @Override
                    public void call(VideoStateChangeEvent videoStateChangeEvent) {
                        switch (videoStateChangeEvent.state) {
                            case VideoStateChangeEvent.STATE_LOAD_FINISH:
                                mId = videoStateChangeEvent.videoDetail.getAid();
                                if (mIsFirstLoad) {
                                    loadFeedbackData();
                                }
                                break;
                            case VideoStateChangeEvent.STATE_PLAY:
                                mRecyclerView.setNestedScrollingEnabled(false);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionUtils.unsubscribe(mBusSub);
    }

    private void loadFeedbackData() {
        mFeedbackSub = RetrofitHelper.getInstance()
                .getReplyService()
                .getFeedback(0, mId, mCurrentPage, PAGE_SIZE, 0, 1)
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
                        if (feedbackData.getReplies().isEmpty()) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLoadView(R.string.no_data_tips, false);
                        }
                        if (mIsFirstLoad) {
                            mIsFirstLoad = false;
                            mFeedbackAdapter.clear();
                            mFeedbackAdapter.addFeedbackData(feedbackData);
                            mFeedbackAdapter.notifyDataSetChanged();
                        } else {
                            int startPosition = mFeedbackAdapter.getItemCount();
                            mFeedbackAdapter.addFeedbackData(feedbackData);
                            mFeedbackAdapter.notifyItemRangeInserted(startPosition, feedbackData.getReplies().size());
                        }
                        mCurrentPage++;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.load_failed_with_click, false);
                        mRecyclerView.setOnLoadMoreViewClickListener(VideoDetailReplyFragment.this);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mFeedbackSub);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_video_detail_reply;
    }

    @Override
    public String getTitle() {
        return "评论";
    }

    @Override
    public void onLoadMoreViewClick() {
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLoadView(R.string.loading, true);
        loadFeedbackData();
    }

    @Override
    public void onLoadMore() {
        loadFeedbackData();
    }
}
