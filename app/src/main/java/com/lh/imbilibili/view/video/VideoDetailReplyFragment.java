package com.lh.imbilibili.view.video;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.feedback.FeedbackData;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.feedback.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedback.FeedbackItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/2.
 * 视频详情评论页面
 */

public class VideoDetailReplyFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreViewClickListener, LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final int PAGE_SIZE = 20;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private int mCurrentPage;
    private Disposable mFeedbackSub;

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
        RxBus.getInstance().register(this);
    }

    @Subscribe(scheduler = EventThread.UI)
    public void VideoStateChange(VideoStateChangeEvent event){
        switch (event.state) {
            case VideoStateChangeEvent.STATE_LOAD_FINISH:
                mId = event.videoDetail.getAid();
                if (mIsFirstLoad) {
                    loadFeedbackData();
                }
                break;
            case VideoStateChangeEvent.STATE_PLAY:
//              mRecyclerView.setNestedScrollingEnabled(false);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        RxBus.getInstance().unRegister(this);
    }

    private void loadFeedbackData() {
        mFeedbackSub = CommonHelper.getInstance()
                .getReplyService()
                .getFeedback(0, mId, mCurrentPage, PAGE_SIZE, 0, 1)
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<FeedbackData>, FeedbackData>handlerResult())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<FeedbackData>() {
                    @Override
                    public void onSuccess(FeedbackData feedbackData) {
                        mRecyclerView.setLoading(false);
                        if (feedbackData.getReplies().isEmpty()) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
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

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_RETRY);
                        mRecyclerView.setOnLoadMoreViewClickListener(VideoDetailReplyFragment.this);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mFeedbackSub);
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
        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_REFRESHING);
        loadFeedbackData();
    }

    @Override
    public void onLoadMore() {
        loadFeedbackData();
    }
}
