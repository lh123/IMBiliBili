package com.lh.imbilibili.view.home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.cachelibrary.strategy.CacheStrategy;
import com.lh.cachelibrary.strategy.Strategy;
import com.lh.cachelibrary.utils.TypeBuilder;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.home.IndexBangumiRecommend;
import com.lh.imbilibili.model.home.IndexPage;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.RxCacheUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.bangumi.BangumiAdapter;
import com.lh.imbilibili.view.adapter.bangumi.BangumiIndexItemDecoration;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.bangumi.FollowBangumiActivity;
import com.lh.imbilibili.view.bangumi.SeasonGroupActivity;
import com.lh.imbilibili.view.common.WebViewActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.lang.reflect.Type;
import java.util.Collections;
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
 * Created by liuhui on 2016/7/6.
 * 番剧页面
 */
public class BangumiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.OnLoadMoreLinstener, BangumiAdapter.OnItemClickListener {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView recyclerView;

    private IndexPage mIndexData;
    private List<IndexBangumiRecommend> mBangumiRecommends;

    private BangumiAdapter adapter;

    private String mCursor;
    private boolean mNeedForeRefresh;

    private Disposable mAllDataSub;
    private Disposable mRecommendSub;

    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_bangumi;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mNeedForeRefresh = false;
        initRecyclerView();
        loadAllData();
    }

    private void loadAllData() {
        mCursor = "-1";
        mAllDataSub = Observable.mergeDelayError(loadIndexData(), loadBangumiRecommendData(mCursor, PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .ignoreElements()
                .subscribeWith(new DisposableCompletableObserver(){
                    @Override
                    public void onComplete() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        finishTask();
                        recyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        recyclerView.setEnableLoadMore(false);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    private void finishTask() {
        swipeRefreshLayout.setRefreshing(false);
        mNeedForeRefresh = false;
        if (mIndexData != null) {
            Collections.sort(mIndexData.getSerializing());
            adapter.setmIndexPage(mIndexData);
        }
        if (mBangumiRecommends != null) {
            adapter.clearRecommend();
            adapter.addBangumis(mBangumiRecommends);
            mCursor = mBangumiRecommends.get(mBangumiRecommends.size() - 1).getCursor();
        }
        if (mIndexData != null || mBangumiRecommends != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new BangumiAdapter(getContext());
        adapter.setItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = recyclerView.getItemViewType(position);
                if (type == BangumiAdapter.BANNER ||
                        type == BangumiAdapter.NAV ||
                        type == BangumiAdapter.SERIALIZING_HEAD ||
                        type == BangumiAdapter.SEASON_BANGUMI_HEAD ||
                        type == BangumiAdapter.BANGUMI_RECOMMEND_HEAD ||
                        type == BangumiAdapter.BANGUMI_RECOMMEND_ITEM ||
                        type == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.addItemDecoration(new BangumiIndexItemDecoration(getActivity()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreLinstener(this);
    }

    //加载主页数据
    private Observable<IndexPage> loadIndexData() {
        Strategy strategy = mNeedForeRefresh ? CacheStrategy.onlyRemote() : CacheStrategy.priorityCache();
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .addParamType(IndexPage.class)
                .build();
        return CommonHelper.getInstance()
                .getBangumiService()
                .getIndexPage(System.currentTimeMillis())
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<IndexPage>>transformer("index_page", strategy, type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<IndexPage>, IndexPage>handlerResult())
                .map(new Function<IndexPage, IndexPage>() {
                    @Override
                    public IndexPage apply(IndexPage indexPage) throws Exception {
                        mIndexData = indexPage;
                        return mIndexData;
                    }
                });

    }

    private Observable<List<IndexBangumiRecommend>> loadBangumiRecommendData(final String cursor, int pageSize) {
        Strategy strategy = "-1".equals(cursor)?CacheStrategy.priorityRemote():CacheStrategy.noneCache();
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(IndexBangumiRecommend.class)
                .endNestedType()
                .build();
        return CommonHelper.getInstance()
                .getBangumiService()
                .getBangumiRecommend(cursor, pageSize, System.currentTimeMillis())
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<List<IndexBangumiRecommend>>>transformer("index_page_"+cursor,strategy,type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<List<IndexBangumiRecommend>>, List<IndexBangumiRecommend>>handlerResult())
                .map(new Function<List<IndexBangumiRecommend>, List<IndexBangumiRecommend>>() {
                    @Override
                    public List<IndexBangumiRecommend> apply(List<IndexBangumiRecommend> indexBangumiRecommends) throws Exception {
                        mBangumiRecommends = indexBangumiRecommends;
                        return mBangumiRecommends;
                    }
                });
    }

    @Override
    public String getTitle() {
        return "番剧";
    }

    @Override
    public void onRefresh() {
        recyclerView.setEnableLoadMore(true);
        recyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_REFRESHING);
        mNeedForeRefresh = true;
        loadAllData();
    }

    @Override
    public void onLoadMore() {
        mRecommendSub = loadBangumiRecommendData(mCursor, 10)
                .subscribeOn(Schedulers.io())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<IndexBangumiRecommend>>(){
                    @Override
                    public void onSuccess(List<IndexBangumiRecommend> indexBangumiRecommends) {
                        recyclerView.setLoading(false);
                        if (indexBangumiRecommends.size() == 0) {
                            recyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                            recyclerView.setEnableLoadMore(false);
                        } else {
                            int startPosition = adapter.getItemCount();
                            adapter.addBangumis(indexBangumiRecommends);
                            adapter.notifyItemRangeInserted(startPosition, indexBangumiRecommends.size());
                            mCursor = indexBangumiRecommends.get(indexBangumiRecommends.size() - 1).getCursor();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        recyclerView.setLoading(false);
                        recyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    @Override
    public void onClick(int itemType, String data) {
        if (itemType == BangumiAdapter.SERIALIZING_GRID_ITEM) {
            BangumiDetailActivity.startActivity(getContext(), data);
        } else if (itemType == BangumiAdapter.SEASON_BANGUMI_ITEM) {
            BangumiDetailActivity.startActivity(getContext(), data);
        } else if (itemType == BangumiAdapter.BANGUMI_RECOMMEND_ITEM) {
            if (data.contains("anime")) {
                String[] temp = data.split("anime/");
                BangumiDetailActivity.startActivity(getContext(), temp[temp.length - 1]);
            } else {
                WebViewActivity.startActivity(getContext(), data);
            }
        } else if (itemType == BangumiAdapter.SEASON_BANGUMI_HEAD) {
            SeasonGroupActivity.startActivity(getContext());
        } else if (itemType == BangumiAdapter.NAV) {
            if (data.equals(String.valueOf(R.id.follow_bangumi))) {
                FollowBangumiActivity.startActivity(getContext());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mAllDataSub, mRecommendSub);
    }
}
