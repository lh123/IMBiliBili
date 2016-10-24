package com.lh.imbilibili.view.usercenter;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.FavouriteRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/17.
 */

public class UserCenterFavouriteFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private FavouriteRecyclerViewAdapter mAdapter;
    private boolean mHaveReceiverEvent;

    public static UserCenterFavouriteFragment newInstance() {
        return new UserCenterFavouriteFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mHaveReceiverEvent = false;
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusUtils.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtils.getBus().unregister(this);
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

    @SuppressWarnings("unused")
    @Subscribe
    public void onUserCenterDataLoadFinish(UserCenter userCenter) {
        if (mHaveReceiverEvent) {
            return;
        }
        mHaveReceiverEvent = true;
        if (userCenter.getSetting().getFavVideo() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (userCenter.getFavourite().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mAdapter.addFavourites(userCenter.getFavourite().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }
}
