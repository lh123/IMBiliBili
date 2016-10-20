package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.model.ReplyCount;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedbackfragment.FeedbackItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by home on 2016/8/2.
 * 评论界面
 */
public class FeedbackFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, LoadMoreRecyclerView.OnLoadMoreViewClickListener, View.OnClickListener {

    public static final String TAG = "FeedbackFragment";
    private static final int PAGE_SIZE = 20;
    @BindView(R.id.title)
    TextView mTvTitle;
    @BindView(R.id.comment_count)
    TextView mTvCommentCount;
    @BindView(R.id.choose_episode)
    TextView mTvChooseEpisode;
    @BindView(R.id.feedback_list)
    LoadMoreRecyclerView mRecyclerView;
    private BangumiDetail mBangumiDetail;
    private int mCurrentPage;
    private FeedbackAdapter mAdapter;

    private Call<BilibiliDataResponse<FeedbackData>> mFeedbackCall;
    private Call<BilibiliDataResponse<ReplyCount>> mReplyCountCall;

    private int mSelectPosition;
    private boolean mNeedRefresh;

    private EpisodeDialogFragment mEpisodeDialogFragment;

    public static FeedbackFragment newInstance(Bundle bundle) {
        FeedbackFragment feedbackFragment = new FeedbackFragment();
        feedbackFragment.setArguments(bundle);
        return feedbackFragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mBangumiDetail = getArguments().getParcelable("data");
        mSelectPosition = getArguments().getInt("position", 0);
        mCurrentPage = 1;
        mNeedRefresh = true;
        initView();
        mTvTitle.setText(StringUtils.format("第%s话", mBangumiDetail.getEpisodes().get(mSelectPosition).getIndex()));
        loadFeedbackData(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId(), 0);
        loadReplyCount(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId());
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_feedback_list;
    }

    private void loadReplyCount(String id) {
        mReplyCountCall = RetrofitHelper.getInstance().getReplyService().getReplyCount(id, 1);
        mReplyCountCall.enqueue(new Callback<BilibiliDataResponse<ReplyCount>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<ReplyCount>> call, Response<BilibiliDataResponse<ReplyCount>> response) {
                if (response.body().getCode() == 0) {
                    mTvCommentCount.setText(StringUtils.format("%d楼", response.body().getData().getCount()));
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<ReplyCount>> call, Throwable t) {
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
            }
        });
    }

    private void loadFeedbackData(String id, int page) {
        mFeedbackCall = RetrofitHelper.getInstance().getReplyService().getFeedback(0, id, page, PAGE_SIZE, 0, 1);
        mFeedbackCall.enqueue(new Callback<BilibiliDataResponse<FeedbackData>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<FeedbackData>> call, Response<BilibiliDataResponse<FeedbackData>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (mNeedRefresh) {
                        mNeedRefresh = false;
                        mAdapter.clear();
                        mAdapter.addFeedbackData(response.body().getData());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        int startPosition = mAdapter.getItemCount();
                        FeedbackData feedbackData = response.body().getData();
                        mAdapter.addFeedbackData(feedbackData);
                        mAdapter.notifyItemRangeInserted(startPosition, feedbackData.getReplies().size());
                    }
                    mCurrentPage++;
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<FeedbackData>> call, Throwable t) {
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLoadView(R.string.load_failed_with_click, false);
                mRecyclerView.setOnLoadMoreViewClickListener(FeedbackFragment.this);
            }
        });
    }

    private void initView() {
        mAdapter = new FeedbackAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        FeedbackItemDecoration itemDecoration = new FeedbackItemDecoration(ContextCompat.getColor(getContext(), R.color.theme_color_dividing_line));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mTvChooseEpisode.setOnClickListener(this);
    }

    private void showEpChooseDialog() {
        if (mEpisodeDialogFragment == null) {
            mEpisodeDialogFragment = EpisodeDialogFragment.newInstance(mBangumiDetail, mSelectPosition);
        }
        mEpisodeDialogFragment.show(getFragmentManager(), EpisodeDialogFragment.TAG);
    }

    @Override
    public void onDestroy() {
        CallUtils.cancelCall(mFeedbackCall, mReplyCountCall);
        super.onDestroy();
    }

    @Override
    public void onLoadMore() {
        loadFeedbackData(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId(), mCurrentPage);
    }

    @Override
    public void onLoadMoreViewClick() {
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLoadView(R.string.loading, true);
        loadFeedbackData(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId(), mCurrentPage);
    }

    @Override
    public void onClick(View v) {
        showEpChooseDialog();
    }

    public void onEpisodeSelect(int position) {
        mSelectPosition = position;
        mCurrentPage = 1;
        mNeedRefresh = true;
        mTvTitle.setText(StringUtils.format("第%s话", mBangumiDetail.getEpisodes().get(position).getIndex()));
        loadFeedbackData(mBangumiDetail.getEpisodes().get(position).getAvId(), mCurrentPage);
        loadReplyCount(mBangumiDetail.getEpisodes().get(position).getAvId());
        if (mEpisodeDialogFragment != null) {
            mEpisodeDialogFragment.dismiss();
        }
    }
}
