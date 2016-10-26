package com.lh.imbilibili.view.home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.cache.CacheTransformer;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.attention.DynamicVideo;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.model.attention.FollowBangumiResponse;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.attention.AttentionItemDecoration;
import com.lh.imbilibili.view.adapter.attention.AttentionRecyclerViewAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.bangumi.FollowBangumiActivity;
import com.lh.imbilibili.view.common.LoginActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
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
 * Created by liuhui on 2016/10/10.
 * 关注页面
 */

public class AttentionFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SwipeRefreshLayout.OnRefreshListener, AttentionRecyclerViewAdapter.OnItemClickListener {
    private static final int PAGE_SIZE = 20;

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private List<FollowBangumi> mFollowBangumis;
    private DynamicVideo mDynamicVideo;

    private AttentionRecyclerViewAdapter mAdapter;
    private int mCurrentPage;
    private Subscription mAllDataSub;
    private Subscription mDynamicDataSub;

    private boolean mNeedLoadData;
    private boolean mNeedForceFresh;

    public static AttentionFragment newInstance() {
        return new AttentionFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        initRecyclerView();
        mCurrentPage = 1;
        mNeedLoadData = false;
        mNeedForceFresh = false;
        if (UserManagerUtils.getInstance().getCurrentUser() == null) {
            mBtnLogin.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNeedLoadData = true;
                    LoginActivity.startActivity(getContext());
                }
            });
        } else {
            mBtnLogin.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            loadAllData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mNeedLoadData && UserManagerUtils.getInstance().getCurrentUser() != null) {
            mNeedLoadData = false;
            mBtnLogin.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            loadAllData();
        }
    }

    private void initRecyclerView() {
        mAdapter = new AttentionRecyclerViewAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getItemViewType(position);
                if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_HEAD
                        || type == AttentionRecyclerViewAdapter.TYPE_DYNAMIC_HEAD
                        || type == AttentionRecyclerViewAdapter.TYPE_DYNAMIC_ITEM
                        || type == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.addItemDecoration(new AttentionItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_attention;
    }

    private void loadAllData() {
        mAllDataSub = Observable.mergeDelayError(loadAttentionBangumiData(), loadDynamicVideoData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        finishTask();
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        mRecyclerView.setEnableLoadMore(false);
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    private void finishTask() {
        mNeedForceFresh = false;
        mSwipeRefreshLayout.setRefreshing(false);
        if (mFollowBangumis != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.setFollowBangumiData(mFollowBangumis);
        }
        if (mDynamicVideo != null) {
            mAdapter.clearFeeds();
            mAdapter.addFeeds(mDynamicVideo.getFeeds());
            mCurrentPage++;
        }
        if (mFollowBangumis != null || mDynamicVideo != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadDynamicData() {
        mDynamicDataSub = loadDynamicVideoData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DynamicVideo>() {
                    @Override
                    public void call(DynamicVideo dynamicVideo) {
                        mRecyclerView.setLoading(false);
                        if (dynamicVideo.getFeeds().size() == 0) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            List<DynamicVideo.Feed> feeds = dynamicVideo.getFeeds();
                            mAdapter.addFeeds(feeds);
                            mAdapter.notifyItemRangeInserted(startPosition, feeds.size());
                            mCurrentPage++;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRecyclerView.setLoading(false);
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                    }
                });
    }

    private Observable<List<FollowBangumi>> loadAttentionBangumiData() {
        return RetrofitHelper.getInstance()
                .getAttentionService()
                .getFollowBangumi(UserManagerUtils.getInstance().getCurrentUser().getMid(), System.currentTimeMillis())
                .compose(new CacheTransformer<FollowBangumiResponse<List<FollowBangumi>>>("follow_bangumi", mNeedForceFresh) {
                })
                .flatMap(new Func1<FollowBangumiResponse<List<FollowBangumi>>, Observable<List<FollowBangumi>>>() {
                    @Override
                    public Observable<List<FollowBangumi>> call(FollowBangumiResponse<List<FollowBangumi>> listFollowBangumiResponse) {
                        if (listFollowBangumiResponse.isSuccess()) {
                            mFollowBangumis = listFollowBangumiResponse.getResult();
                            return Observable.just(listFollowBangumiResponse.getResult());
                        } else {
                            return Observable.error(new ApiException(listFollowBangumiResponse.getCode()));
                        }
                    }
                });
    }

    private Observable<DynamicVideo> loadDynamicVideoData() {
        return RetrofitHelper.getInstance()
                .getAttentionService()
                .getDynamicVideo(mCurrentPage, PAGE_SIZE, 0)
                .compose(new CacheTransformer<BilibiliDataResponse<DynamicVideo>>("attention_dynamic") {
                    @Override
                    protected boolean canCache() {
                        return mCurrentPage == 1;
                    }
                })
                .flatMap(new Func1<BilibiliDataResponse<DynamicVideo>, Observable<DynamicVideo>>() {
                    @Override
                    public Observable<DynamicVideo> call(BilibiliDataResponse<DynamicVideo> dynamicVideoBilibiliDataResponse) {
                        if (dynamicVideoBilibiliDataResponse.isSuccess()) {
                            mDynamicVideo = dynamicVideoBilibiliDataResponse.getData();
                            return Observable.just(dynamicVideoBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(dynamicVideoBilibiliDataResponse.getCode()));
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mAllDataSub, mDynamicDataSub);
    }

    @Override
    public String getTitle() {
        return "关注";
    }

    @Override
    public void onLoadMore() {
        loadDynamicData();
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        mNeedForceFresh = true;
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_REFRESHING);
        loadAllData();
    }

    @Override
    public void onItemClick(String id, int type) {
        if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_ITEM) {
            BangumiDetailActivity.startActivity(getContext(), id);
        } else if (type == AttentionRecyclerViewAdapter.TYPE_DYNAMIC_ITEM) {
            VideoDetailActivity.startActivity(getContext(), id);
        } else if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_HEAD) {
            FollowBangumiActivity.startActivity(getContext());
        }
    }
}
