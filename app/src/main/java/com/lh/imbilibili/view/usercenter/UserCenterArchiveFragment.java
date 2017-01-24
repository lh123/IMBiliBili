package com.lh.imbilibili.view.usercenter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.ArchiveRecyclerViewAdapter;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/17.
 * 用户中心投稿or投币界面
 */

public class UserCenterArchiveFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, ArchiveRecyclerViewAdapter.OnVideoItemClickListener {

    private static final String EXTRA_DATA = "data";
    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private ArchiveRecyclerViewAdapter mAdapter;
    private boolean mIsCoin;
    private UserCenter mUserCenter;

    private int mCurrentPage;
    private Disposable mArchiveSub;
    private UserCenterDataProvider mUserCenterProvider;

    private boolean mIsInitData;

    public static UserCenterArchiveFragment newInstance(boolean isCoin) {
        UserCenterArchiveFragment fragment = new UserCenterArchiveFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_DATA, isCoin);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        RxBus.getInstance().register(this);
        if (mUserCenterProvider != null) {
            mUserCenter = mUserCenterProvider.getUserCenter();
            initData();
        }
    }

    @Subscribe(scheduler = EventThread.UI)
    public void OnUserCenterInfoReceiver(UserCenter userCenter){
        mUserCenter = userCenter;
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        RxBus.getInstance().unRegister(this);
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mIsCoin = getArguments().getBoolean(EXTRA_DATA);
        mCurrentPage = 2;
        mIsInitData = false;
        if (getActivity() instanceof UserCenterDataProvider) {
            mUserCenterProvider = (UserCenterDataProvider) getActivity();
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new ArchiveRecyclerViewAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnVideoItemClickListener(this);
    }

    public void initData() {
        if (mUserCenter == null || mIsInitData) {
            return;
        }
        mIsInitData = true;
        if (mIsCoin && mUserCenter.getSetting().getCoinsVideo() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        int count = !mIsCoin ? mUserCenter.getArchive().getCount() : mUserCenter.getCoinArchive().getCount();
        if (count == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setShowLoadingView(true);
            if (count <= PAGE_SIZE) {
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mEmptyView.setVisibility(View.GONE);
            if (mIsCoin) {
                mAdapter.addVideos(mUserCenter.getCoinArchive().getItem());
            } else {
                mAdapter.addVideos(mUserCenter.getArchive().getItem());
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadArchiveData() {
        mArchiveSub = Observable.just(mIsCoin)
                .flatMap(new Function<Boolean, ObservableSource<BiliBiliResponse<UserCenter.CenterList<UserCenter.Archive>>>>() {
                    @Override
                    public ObservableSource<BiliBiliResponse<UserCenter.CenterList<UserCenter.Archive>>> apply(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            return CommonHelper.getInstance().getUserService().getUserCoinArchive(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
                        } else {
                            return CommonHelper.getInstance().getUserService().getUserArchive(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<UserCenter.CenterList<UserCenter.Archive>>, UserCenter.CenterList<UserCenter.Archive>>handlerResult())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<UserCenter.CenterList<UserCenter.Archive>>() {
                    @Override
                    public void onNext(UserCenter.CenterList<UserCenter.Archive> archiveCenterList) {
                        if (archiveCenterList.getCount() < PAGE_SIZE) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addVideos(archiveCenterList.getItem());
                            mAdapter.notifyItemRangeInserted(startPosition, archiveCenterList.getItem().size());
                            mCurrentPage++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToastShort(R.string.load_error);
                    }

                    @Override
                    public void onComplete() {
                        mRecyclerView.setLoading(false);
                    }
                });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }

    @Override
    public void onLoadMore() {
        loadArchiveData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mArchiveSub);
    }

    @Override
    public void onVideoClick(String aid) {
        VideoDetailActivity.startActivity(getContext(), aid);
    }
}
