package com.lh.imbilibili.view.partion;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.lh.imbilibili.view.adapter.partion.PartionChildRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.partion.PartionListItemDecoration;
import com.lh.imbilibili.view.adapter.partion.model.PartionModel;
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
 * Created by liuhui on 2016/10/1.
 * 分区列表界面
 */

public class PartionListFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, PartionChildRecyclerViewAdapter.OnVideoItemClickListener {

    private static final String EXTRA_DATA = "partion";
    public static final int PAGE_SIZE = 20;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private PartionModel.Partion mPartion;

    private PartionChildRecyclerViewAdapter mAdapter;

    private int mCurrentPage;

    private Disposable mPartionAllDataCall;
    private Disposable mNewVideoDataCall;

    private List<PartionVideo> mPartionVideos;
    private PartionHome mPartionHome;


    public static PartionListFragment newInstance(PartionModel.Partion partion) {
        PartionListFragment fragment = new PartionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, partion);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_partion_list;
    }

    @Override
    protected void initView(View view) {
        mPartion = getArguments().getParcelable(EXTRA_DATA);
        ButterKnife.bind(this, view);
        mCurrentPage = 1;
        initRecyclerView();
    }

    @Override
    protected void fetchData() {
        loadData();
        loadNewData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mPartionAllDataCall, mNewVideoDataCall);
    }

    private void initRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new PartionChildRecyclerViewAdapter(getContext());
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        PartionListItemDecoration itemDecoration = new PartionListItemDecoration(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnVideoItemClickListener(this);
    }

    private Observable<List<PartionVideo>> loadNewData() {
        Strategy strategy = mCurrentPage == 1? CacheStrategy.priorityRemote():CacheStrategy.noneCache();
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(PartionVideo.class)
                .endNestedType()
                .build();
        return CommonHelper.getInstance()
                .getPartionService()
                .getPartionChildList(mPartion.getId(), mCurrentPage, PAGE_SIZE, "senddate")
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<List<PartionVideo>>>transformer("partion_child_new_" + mPartion.getId(),strategy,type))
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<List<PartionVideo>>, List<PartionVideo>>handlerResult())
                .map(new Function<List<PartionVideo>, List<PartionVideo>>() {
                    @Override
                    public List<PartionVideo> apply(List<PartionVideo> partionVideos) throws Exception {
                        mPartionVideos = partionVideos;
                        return mPartionVideos;
                    }
                });
    }

    private void loadData() {
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(PartionVideo.class)
                .endNestedType()
                .build();
        mPartionAllDataCall = Observable.mergeDelayError(CommonHelper.getInstance()
                .getPartionService()
                .getPartionChild(mPartion.getId(), "*")
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<PartionHome>>transformer("partion_child_hot_" + mPartion.getId(),CacheStrategy.priorityRemote(),type))
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<PartionHome>, PartionHome>handlerResult())
                .map(new Function<PartionHome, PartionHome>() {
                    @Override
                    public PartionHome apply(PartionHome partionHome) throws Exception {
                        mPartionHome = partionHome;
                        return mPartionHome;
                    }
                }), loadNewData())
                .subscribeOn(Schedulers.io())
                .ignoreElements()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        finishTask();
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    private void finishTask() {
        if (mPartionHome != null) {
            mAdapter.setPartionHomeData(mPartionHome);
        }
        if (mPartionVideos != null) {
            mCurrentPage++;
            mAdapter.addNewVideos(mPartionVideos);
        }
        if (mPartionHome != null || mPartionVideos != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore() {
        mNewVideoDataCall = loadNewData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribeWith(new DisposableSingleObserver<List<PartionVideo>>() {
                    @Override
                    public void onSuccess(List<PartionVideo> partionVideos) {
                        mRecyclerView.setLoading(false);
                        if (partionVideos.size() == 0) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addNewVideos(partionVideos);
                            mAdapter.notifyItemRangeInserted(startPosition, partionVideos.size());
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

    @Override
    public void onVideoClick(String aid) {
        VideoDetailActivity.startActivity(getContext(), aid);
    }
}
