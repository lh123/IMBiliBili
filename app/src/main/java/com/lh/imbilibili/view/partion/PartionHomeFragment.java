package com.lh.imbilibili.view.partion;

import android.os.Bundle;
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
import com.lh.imbilibili.model.partion.PartionHome;
import com.lh.imbilibili.model.partion.PartionVideo;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.RxCacheUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.partion.PartionHomeRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.partion.PartionItemDecoration;
import com.lh.imbilibili.view.adapter.partion.model.PartionModel;
import com.lh.imbilibili.view.common.WebViewActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.lh.rxbuslibrary.RxBus;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Created by liuhui on 2016/9/29.
 * 分区主页
 */

public class PartionHomeFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SwipeRefreshLayout.OnRefreshListener, PartionHomeRecyclerViewAdapter.OnItemClickListener {
    private static final String EXTRA_DATA = "partionModel";
    private static final int PAGE_SIZE = 50;

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private PartionHome mPartionHomeData;
    private List<PartionVideo> mPartionVideos;

    private PartionHomeRecyclerViewAdapter mAdapter;
    private PartionModel mPartionModel;
    private Disposable mPartionAllDataSub;
    private Disposable mPartionLoadMoreSub;

    private int mCurrentPage = 1;
//    private boolean mNeedForeRefresh;

    public static PartionHomeFragment newInstance(PartionModel partionModel) {
        PartionHomeFragment fragment = new PartionHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, partionModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        mPartionModel = getArguments().getParcelable(EXTRA_DATA);
        ButterKnife.bind(this, view);
        mCurrentPage = 1;
//        mNeedForeRefresh = false;
        initRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_partion_home;
    }

    private void loadAllData() {
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .addParamType(PartionHome.class)
                .build();
        mPartionAllDataSub = Observable.mergeDelayError(CommonHelper.getInstance()
                .getPartionService()
                .getPartionInfo(mPartionModel.getId(), "*")
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<PartionHome>>transformer("partion_home_" + mPartionModel.getId(),CacheStrategy.priorityRemote(),type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<PartionHome>, PartionHome>handlerResult()).map(new Function<PartionHome, PartionHome>() {
                    @Override
                    public PartionHome apply(PartionHome partionHome) throws Exception {
                        mPartionHomeData = partionHome;
                        return mPartionHomeData;
                    }
                }), loadDynamicData())
                .subscribeOn(Schedulers.io())
                .ignoreElements()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver(){
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
        if (mPartionHomeData != null) {
            mAdapter.setPartionData(mPartionHomeData);
        }
        if (mPartionVideos != null) {
            mAdapter.clearDynamicVideo();
            mAdapter.addDynamicVideo(mPartionVideos);
            mCurrentPage++;
        }
        if (mPartionHomeData != null || mPartionVideos != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private Observable<List<PartionVideo>> loadDynamicData() {
        Strategy strategy = mCurrentPage == 1?CacheStrategy.priorityRemote():CacheStrategy.noneCache();
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(PartionVideo.class)
                .endNestedType()
                .build();
        return CommonHelper.getInstance()
                .getPartionService()
                .getPartionDynamic(mPartionModel.getId(), mCurrentPage, PAGE_SIZE)
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<List<PartionVideo>>>transformer("partion_home_dynamic" + mPartionModel.getId(),strategy,type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<List<PartionVideo>>, List<PartionVideo>>handlerResult())
                .map(new Function<List<PartionVideo>, List<PartionVideo>>() {
                    @Override
                    public List<PartionVideo> apply(List<PartionVideo> partionVideos) throws Exception {
                        mPartionVideos = partionVideos;
                        return mPartionVideos;
                    }
                });
    }

    @Override
    protected void fetchData() {
        loadAllData();
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mRecyclerView.getItemViewType(position)) {
                    case PartionHomeRecyclerViewAdapter.TYPE_BANNER:
                    case PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_SUB_PARTION:
                    case LoadMoreRecyclerView.TYPE_LOAD_MORE:
                        return 2;
                    case PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_ITEM:
                    case PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_ITEM:
                    case PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_ITME:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        PartionItemDecoration itemDecoration = new PartionItemDecoration(getContext());
        if (mAdapter == null) {
            mAdapter = new PartionHomeRecyclerViewAdapter(getContext(), mPartionModel);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onLoadMore() {
        mPartionLoadMoreSub = loadDynamicData().subscribeOn(Schedulers.io())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PartionVideo>>() {
                    @Override
                    public void onSuccess(List<PartionVideo> partionVideos) {
                        mRecyclerView.setLoading(false);
                        if (partionVideos.size() == 0) {
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                            mRecyclerView.setEnableLoadMore(false);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addDynamicVideo(partionVideos);
                            mAdapter.notifyItemRangeInserted(startPosition, partionVideos.size());
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

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_REFRESHING);
//        mNeedForeRefresh = true;
        loadAllData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mPartionAllDataSub, mPartionLoadMoreSub);
    }

    @Override
    public void onBannerClick(String uri) {
        Pattern p = Pattern.compile("av(\\d+)");
        Matcher m = p.matcher(uri);
        if (m.find()) {
            VideoDetailActivity.startActivity(getContext(), m.group(1));
        } else {
            WebViewActivity.startActivity(getContext(), uri);
        }
    }

    @Override
    public void onVideoItemClick(String aid) {
        VideoDetailActivity.startActivity(getContext(), aid);
    }

    @Override
    public void onSubPartionItemClick(int position) {
        RxBus.getInstance().post(new SubPartionClickEvent(position));
    }

    @Override
    public void onHeadItemClick(int type) {
        if (type == PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_HEAD) {
            RxBus.getInstance().post(new SubPartionClickEvent(1));
        }
    }
}
