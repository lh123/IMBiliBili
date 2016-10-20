package com.lh.imbilibili.view.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/2.
 * 视频详情评论页面
 */

public class VideoDetailReplyFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreViewClickListener, LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final int PAGE_SIZE = 20;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private int mCurrentPage;
    private Call<BilibiliDataResponse<FeedbackData>> mFeedbackCall;
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
        BusUtils.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtils.getBus().unregister(this);
    }

    private void loadFeedbackData() {
        mFeedbackCall = RetrofitHelper.getInstance().getReplyService().getFeedback(0, mId, mCurrentPage, PAGE_SIZE, 0, 1);
        mFeedbackCall.enqueue(new Callback<BilibiliDataResponse<FeedbackData>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<FeedbackData>> call, Response<BilibiliDataResponse<FeedbackData>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().getCode() == 0) {
                    if (response.body().getData().getReplies().isEmpty()) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    }
                    FeedbackData feedbackData = response.body().getData();
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
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<FeedbackData>> call, Throwable t) {
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLoadView(R.string.load_failed_with_click, false);
                mRecyclerView.setOnLoadMoreViewClickListener(VideoDetailReplyFragment.this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mFeedbackCall);
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

    @Subscribe
    public void onVideoDetailLoadFinish(VideoDetailActivity.VideoStateChangeEvent event) {
        switch (event.state) {
            case VideoDetailActivity.VideoStateChangeEvent.STATE_LOAD_FINISH:
                mId = event.videoDetail.getAid();
                if (mIsFirstLoad) {
                    loadFeedbackData();
                }
                break;
            case VideoDetailActivity.VideoStateChangeEvent.STATE_PLAY:
                mRecyclerView.setNestedScrollingEnabled(false);
                break;
        }
    }
}
