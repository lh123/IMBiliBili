package com.lh.imbilibili.view.home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.lh.cachelibrary.strategy.CacheStrategy;
import com.lh.cachelibrary.strategy.Strategy;
import com.lh.cachelibrary.utils.TypeBuilder;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.attention.DynamicVideo;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.model.attention.FollowBangumiResponse;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.RxCacheUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.attention.AttentionItemDecoration;
import com.lh.imbilibili.view.adapter.attention.AttentionRecyclerViewAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.bangumi.FollowBangumiActivity;
import com.lh.imbilibili.view.common.LoginActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/10.
 * 关注页面
 */

public class AttentionFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SwipeRefreshLayout.OnRefreshListener, AttentionRecyclerViewAdapter.OnItemClickListener {
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
    private Disposable mAllDataSub;
    private Disposable mDynamicDataSub;

    private boolean mNeedLoadData;

    public static AttentionFragment newInstance() {
        return new AttentionFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        initRecyclerView();
        mCurrentPage = 1;
        mNeedLoadData = false;
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
        }
    }

    @Override
    protected void fetchData() {
        if (UserManagerUtils.getInstance().getCurrentUser() != null) {
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
        if (mAdapter == null) {
            mAdapter = new AttentionRecyclerViewAdapter(getContext());
        }
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
                .ignoreElements()
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        finishTask();
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        mRecyclerView.setEnableLoadMore(false);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    private void finishTask() {
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
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<DynamicVideo>() {
                    @Override
                    public void onSuccess(DynamicVideo dynamicVideo) {
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

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    private Observable<List<FollowBangumi>> loadAttentionBangumiData() {
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(FollowBangumi.class)
                .endNestedType()
                .build();
        return CommonHelper
                .getInstance()
                .getAttentionService()
                .getFollowBangumi(UserManagerUtils.getInstance().getCurrentUser().getMid(), System.currentTimeMillis())
                .compose(RxCacheUtils.getInstance().<FollowBangumiResponse<List<FollowBangumi>>>transformer("follow_bangumi", CacheStrategy.priorityRemote(),type ))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<List<FollowBangumi>>, List<FollowBangumi>>handlerResult())
                .map(new Function<List<FollowBangumi>, List<FollowBangumi>>() {
                    @Override
                    public List<FollowBangumi> apply(List<FollowBangumi> followBangumis) throws Exception {
                        mFollowBangumis = followBangumis;
                        return mFollowBangumis;
                    }
                });
    }

    private Observable<DynamicVideo> loadDynamicVideoData() {
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .addParamType(DynamicVideo.class)
                .build();
        Strategy strategy = mCurrentPage == 1?CacheStrategy.priorityCache():CacheStrategy.noneCache();
        return CommonHelper.getInstance()
                .getAttentionService()
                .getDynamicVideo(mCurrentPage, PAGE_SIZE, 0)
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<DynamicVideo>>transformer("attention_dynamic",strategy,type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<DynamicVideo>, DynamicVideo>handlerResult())
                .map(new Function<DynamicVideo, DynamicVideo>() {
                    @Override
                    public DynamicVideo apply(DynamicVideo dynamicVideo) throws Exception {
                        mDynamicVideo = dynamicVideo;
                        return mDynamicVideo;
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mAllDataSub, mDynamicDataSub);
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
