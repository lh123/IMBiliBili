package com.lh.imbilibili.view.usercenter;

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
import com.lh.imbilibili.view.adapter.usercenter.CommunityRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/17.
 * 兴趣圈界面
 */

public class UserCenterCommunityFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private UserCenter mUserCenter;

    private CommunityRecyclerViewAdapter mAdapter;

    private int mCurrentPage;
    private Disposable mUserCommunitySub;

    private UserCenterDataProvider mUserCenterProvider;

    private boolean mIsInitData;

    public static UserCenterCommunityFragment newInstance() {
        return new UserCenterCommunityFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mCurrentPage = 2;
        mIsInitData = false;
        if (getActivity() instanceof UserCenterDataProvider) {
            mUserCenterProvider = (UserCenterDataProvider) getActivity();
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new CommunityRecyclerViewAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
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

    public void initData() {
        if (mUserCenter == null || mIsInitData) {
            return;
        }
        mIsInitData = true;
        if (mUserCenter.getSetting().getGroups() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (mUserCenter.getCommunity() == null || mUserCenter.getCommunity().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setShowLoadingView(true);
            if (mUserCenter.getCommunity().getCount() <= PAGE_SIZE) {
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mEmptyView.setVisibility(View.GONE);
            mAdapter.addCommunities(mUserCenter.getCommunity().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadCommunity() {
        mUserCommunitySub = CommonHelper.getInstance()
                .getUserService()
                .getUserCommunity(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()))
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<UserCenter.CenterList<UserCenter.Community>>, UserCenter.CenterList<UserCenter.Community>>handlerResult())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserCenter.CenterList<UserCenter.Community>>() {
                    @Override
                    public void onSuccess(UserCenter.CenterList<UserCenter.Community> communityCenterList) {
                        mRecyclerView.setLoading(false);
                        if (communityCenterList.getCount() < PAGE_SIZE) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addCommunities(communityCenterList.getItem());
                            mAdapter.notifyItemRangeInserted(startPosition, communityCenterList.getItem().size());
                            mCurrentPage++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mUserCommunitySub);
    }

    @Override
    public void onLoadMore() {
        loadCommunity();
    }
}
