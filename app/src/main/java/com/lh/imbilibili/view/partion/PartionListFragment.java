package com.lh.imbilibili.view.partion;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.cache.CacheTransformer;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.partion.PartionHome;
import com.lh.imbilibili.model.partion.PartionVideo;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.partion.PartionChildRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.partion.PartionListItemDecoration;
import com.lh.imbilibili.view.adapter.partion.model.PartionModel;
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

    private Subscription mPartionAllDataCall;
    private Subscription mNewVideoDataCall;

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
        SubscriptionUtils.unsubscribe(mPartionAllDataCall, mNewVideoDataCall);
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
        return CommonHelper.getInstance()
                .getPartionService()
                .getPartionChildList(mPartion.getId(), mCurrentPage, PAGE_SIZE, "senddate")
                .compose(new CacheTransformer<BilibiliDataResponse<List<PartionVideo>>>("partion_child_new_" + mPartion.getId()) {
                    @Override
                    protected boolean canCache() {
                        return mCurrentPage == 1;
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BilibiliDataResponse<List<PartionVideo>>, Observable<List<PartionVideo>>>() {
                    @Override
                    public Observable<List<PartionVideo>> call(BilibiliDataResponse<List<PartionVideo>> listBilibiliDataResponse) {
                        if (listBilibiliDataResponse.isSuccess()) {
                            mPartionVideos = listBilibiliDataResponse.getData();
                            return Observable.just(listBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(listBilibiliDataResponse.getCode()));
                        }
                    }
                });
    }

    private void loadData() {
        mPartionAllDataCall = Observable.mergeDelayError(CommonHelper.getInstance()
                .getPartionService()
                .getPartionChild(mPartion.getId(), "*")
                .compose(new CacheTransformer<BilibiliDataResponse<PartionHome>>("partion_child_hot_" + mPartion.getId()) {
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BilibiliDataResponse<PartionHome>, Observable<PartionHome>>() {
                    @Override
                    public Observable<PartionHome> call(BilibiliDataResponse<PartionHome> partionHomeBilibiliDataResponse) {
                        if (partionHomeBilibiliDataResponse.isSuccess()) {
                            mPartionHome = partionHomeBilibiliDataResponse.getData();
                            return Observable.just(partionHomeBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(partionHomeBilibiliDataResponse.getCode()));
                        }
                    }
                }), loadNewData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        finishTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_FAIL);
                        finishTask();
                        ToastUtils.showToastShort(R.string.load_error);
                    }

                    @Override
                    public void onNext(Object o) {
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
                .subscribe(new Action1<List<PartionVideo>>() {
                    @Override
                    public void call(List<PartionVideo> partionVideos) {
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
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
