package com.lh.imbilibili.view.usercenter;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.GameRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by liuhui on 2016/10/17.
 * 用户中心游戏页面
 */

public class UserCenterGameFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private UserCenter mUserCenter;

    private GameRecyclerViewAdapter mAdapter;

//    private int mCurrentPage;

    private Subscription mBusSub;

    private UserCenterDataProvider mUserCenterProvider;

    private boolean mIsInitData;

    public static UserCenterGameFragment newInstance() {
        return new UserCenterGameFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
//        mCurrentPage = 2;
        mIsInitData = false;
        if (getActivity() instanceof UserCenterDataProvider) {
            mUserCenterProvider = (UserCenterDataProvider) getActivity();
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new GameRecyclerViewAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBusSub = RxBus.getInstance()
                .toObserverable(UserCenter.class)
                .subscribe(new Action1<UserCenter>() {
                    @Override
                    public void call(UserCenter userCenter) {
                        if (mUserCenter == null) {
                            mUserCenter = userCenter;
                            initData();
                        }
                    }
                });
        if (mUserCenterProvider != null && mUserCenter == null) {
            mUserCenter = mUserCenterProvider.getUserCenter();
            initData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionUtils.unsubscribe(mBusSub);
    }

    public void initData() {
        if (mUserCenter == null || mIsInitData) {
            return;
        }
        mIsInitData = true;
        if (mUserCenter.getSetting().getPlayedGame() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (mUserCenter.getGame().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setShowLoadingView(true);
            if (mUserCenter.getGame().getCount() <= PAGE_SIZE) {
                mRecyclerView.setEnableLoadMore(false);
                mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mEmptyView.setVisibility(View.GONE);
            mAdapter.addGames(mUserCenter.getGame().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    //接口错误
//    private void loadGame() {
//        mGameCall = RetrofitHelper.getInstance().getUserService().getUserGame(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
//        mGameCall.enqueue(new Callback<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Game>>>() {
//            @Override
//            public void onResponse(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Game>>> call, Response<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Game>>> response) {
//                mRecyclerView.setLoading(false);
//                if (response.body().isSuccess()) {
//                    if (response.body().getData().getCount() < PAGE_SIZE) {
//                        mRecyclerView.setEnableLoadMore(false);
//                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
//                    } else {
//                        int startPosition = mAdapter.getItemCount();
//                        mAdapter.addGames(response.body().getData().getItem());
//                        mAdapter.notifyItemRangeInserted(startPosition, response.body().getData().getItem().size());
//                        mCurrentPage++;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Game>>> call, Throwable t) {
//                mRecyclerView.setLoading(false);
//                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
//            }
//        });
//    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoadMore() {
//        loadGame();
    }
}
