package com.lh.imbilibili.view.home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.cache.CacheTransformer;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.home.IndexBangumiRecommend;
import com.lh.imbilibili.model.home.IndexPage;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.bangumi.BangumiAdapter;
import com.lh.imbilibili.view.adapter.bangumi.BangumiIndexItemDecoration;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.bangumi.FollowBangumiActivity;
import com.lh.imbilibili.view.bangumi.SeasonGroupActivity;
import com.lh.imbilibili.view.common.WebViewActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.Collections;
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

    private Subscription mAllDataSub;
    private Subscription mRecommendSub;

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
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        finishTask();
                        recyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        recyclerView.setEnableLoadMore(false);
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onNext(Object o) {

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
        return RetrofitHelper.getInstance()
                .getBangumiService()
                .getIndexPage(System.currentTimeMillis())
                .compose(new CacheTransformer<BiliBiliResultResponse<IndexPage>>("index_page", mNeedForeRefresh) {
                })
                .flatMap(new Func1<BiliBiliResultResponse<IndexPage>, Observable<IndexPage>>() {
                    @Override
                    public Observable<IndexPage> call(BiliBiliResultResponse<IndexPage> indexPageBiliBiliResultResponse) {
                        if (indexPageBiliBiliResultResponse.isSuccess()) {
                            mIndexData = indexPageBiliBiliResultResponse.getResult();
                            return Observable.just(mIndexData);
                        } else {
                            return Observable.error(new ApiException(indexPageBiliBiliResultResponse.getCode()));
                        }
                    }
                });
    }

    private Observable<List<IndexBangumiRecommend>> loadBangumiRecommendData(final String cursor, int pageSize) {
        return RetrofitHelper.getInstance()
                .getBangumiService()
                .getBangumiRecommend(cursor, pageSize, System.currentTimeMillis())
                .compose(new CacheTransformer<BiliBiliResultResponse<List<IndexBangumiRecommend>>>("index_page_recommend") {
                    @Override
                    protected boolean canCache() {
                        return "-1".equals(cursor);
                    }
                })
                .flatMap(new Func1<BiliBiliResultResponse<List<IndexBangumiRecommend>>, Observable<List<IndexBangumiRecommend>>>() {
                    @Override
                    public Observable<List<IndexBangumiRecommend>> call(BiliBiliResultResponse<List<IndexBangumiRecommend>> listBiliBiliResultResponse) {
                        if (listBiliBiliResultResponse.isSuccess()) {
                            mBangumiRecommends = listBiliBiliResultResponse.getResult();
                            return Observable.just(listBiliBiliResultResponse.getResult());
                        } else {
                            return Observable.error(new ApiException(listBiliBiliResultResponse.getCode()));
                        }
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<IndexBangumiRecommend>>() {
                    @Override
                    public void call(List<IndexBangumiRecommend> indexBangumiRecommends) {
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        recyclerView.setLoading(false);
                        recyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
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
        SubscriptionUtils.unsubscribe(mAllDataSub, mRecommendSub);
    }
}
