package com.lh.imbilibili.view.usercenter;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.FavouriteRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by liuhui on 2016/10/17.
 * 用户收藏界面
 */

public class UserCenterFavouriteFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private FavouriteRecyclerViewAdapter mAdapter;

    private UserCenter mUserCenter;
    private boolean mIsInitData;

    private UserCenterDataProvider mUserCenterProvider;

    public static UserCenterFavouriteFragment newInstance() {
        return new UserCenterFavouriteFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        System.out.println("initView");
        mIsInitData = false;
        if (getActivity() instanceof UserCenterDataProvider) {
            mUserCenterProvider = (UserCenterDataProvider) getActivity();
        }
        initRecyclerView();
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


    private void initRecyclerView() {
        mAdapter = new FavouriteRecyclerViewAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRecyclerView.getItemViewType(position) == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(getContext(), true));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initData() {
        if (mUserCenter == null || mIsInitData) {
            return;
        }
        mIsInitData = true;
        if (mUserCenter.getSetting().getFavVideo() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (mUserCenter.getFavourite().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mAdapter.addFavourites(mUserCenter.getFavourite().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }
}
