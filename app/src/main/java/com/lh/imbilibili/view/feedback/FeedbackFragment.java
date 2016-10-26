package com.lh.imbilibili.view.feedback;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.feedback.FeedbackData;
import com.lh.imbilibili.model.feedback.ReplyCount;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.feedback.FeedbackAdapter;
import com.lh.imbilibili.view.adapter.feedback.FeedbackItemDecoration;
import com.lh.imbilibili.view.bangumi.EpisodeDialogFragment;
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

    private Subscription mFeedbackSub;
    private Subscription mReplyCountSub;

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
        mReplyCountSub = RetrofitHelper.getInstance()
                .getReplyService()
                .getReplyCount(id, 1)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BilibiliDataResponse<ReplyCount>, Observable<ReplyCount>>() {
                    @Override
                    public Observable<ReplyCount> call(BilibiliDataResponse<ReplyCount> replyCountBilibiliDataResponse) {
                        if (replyCountBilibiliDataResponse.isSuccess()) {
                            return Observable.just(replyCountBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(replyCountBilibiliDataResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ReplyCount>() {
                    @Override
                    public void call(ReplyCount replyCount) {
                        mTvCommentCount.setText(StringUtils.format("%d楼", replyCount.getCount()));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                    }
                });
    }

    private void loadFeedbackData(String id, int page) {
        RetrofitHelper.getInstance()
                .getReplyService()
                .getFeedback(0, id, page, PAGE_SIZE, 0, 1)
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_RETRY);
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
        SubscriptionUtils.unsubscribe(mFeedbackSub, mReplyCountSub);
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
