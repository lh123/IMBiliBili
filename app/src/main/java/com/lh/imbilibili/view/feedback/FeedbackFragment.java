package com.lh.imbilibili.view.feedback;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.feedback.FeedbackData;
import com.lh.imbilibili.model.feedback.ReplyCount;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.feedback.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedback.FeedbackItemDecoration;
import com.lh.imbilibili.view.bangumi.EpisodeDialogFragment;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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

    private Disposable mReplyCountSub;

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
        initRecyclerView();
        mTvTitle.setText(StringUtils.format("第%s话", mBangumiDetail.getEpisodes().get(mSelectPosition).getIndex()));
        loadFeedbackData(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId(), 0);
        loadReplyCount(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId());
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_feedback_list;
    }

    private void loadReplyCount(String id) {
        mReplyCountSub = CommonHelper.getInstance()
                .getReplyService()
                .getReplyCount(id, 1)
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<ReplyCount>, ReplyCount>handlerResult())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ReplyCount>() {
                    @Override
                    public void onSuccess(ReplyCount replyCount) {
                        mTvCommentCount.setText(StringUtils.format("%d楼", replyCount.getCount()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    private void loadFeedbackData(String id, int page) {
        CommonHelper.getInstance()
                .getReplyService()
                .getFeedback(0, id, page, PAGE_SIZE, 0, 1)
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<FeedbackData>, FeedbackData>handlerResult())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<FeedbackData>() {
                    @Override
                    public void onSuccess(FeedbackData feedbackData) {
                        if (mNeedRefresh) {
                            mNeedRefresh = false;
                            mAdapter.clear();
                            mAdapter.addFeedbackData(feedbackData);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addFeedbackData(feedbackData);
                            mAdapter.notifyItemRangeInserted(startPosition, feedbackData.getReplies().size());
                        }
                        mCurrentPage++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_RETRY);
                    }
                });
    }

    private void initRecyclerView() {
        mAdapter = new FeedbackAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        FeedbackItemDecoration itemDecoration = new FeedbackItemDecoration(ContextCompat.getColor(getContext(), R.color.theme_color_dividing_line));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mRecyclerView.setOnLoadMoreViewClickListener(this);
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
        super.onDestroy();
        DisposableUtils.dispose(mReplyCountSub);
    }

    @Override
    public void onLoadMore() {
        loadFeedbackData(mBangumiDetail.getEpisodes().get(mSelectPosition).getAvId(), mCurrentPage);
    }

    @Override
    public void onLoadMoreViewClick() {
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_REFRESHING);
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
