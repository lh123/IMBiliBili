package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.ArchiveRecyclerViewAdapter;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> mArchiveCall;

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
        BusUtils.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtils.getBus().unregister(this);
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mIsCoin = getArguments().getBoolean(EXTRA_DATA);
        mCurrentPage = 2;
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new ArchiveRecyclerViewAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnVideoItemClickListener(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUserCenterDataLoadFinish(UserCenter userCenter) {
        if (mUserCenter != null) {
            return;
        }
        mUserCenter = userCenter;
        if (mIsCoin && userCenter.getSetting().getCoinsVideo() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        int count = !mIsCoin ? userCenter.getArchive().getCount() : userCenter.getCoinArchive().getCount();
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
                mRecyclerView.setLoadView(R.string.no_data_tips, false);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mEmptyView.setVisibility(View.GONE);
            if (mIsCoin) {
                mAdapter.addVideos(userCenter.getCoinArchive().getItem());
            } else {
                mAdapter.addVideos(userCenter.getArchive().getItem());
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadArchiveData() {
        if (mIsCoin) {
            mArchiveCall = RetrofitHelper.getInstance().getUserService().getUserCoinArchive(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
        } else {
            mArchiveCall = RetrofitHelper.getInstance().getUserService().getUserArchive(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
        }
        mArchiveCall.enqueue(new Callback<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> call, Response<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getData().getCount() < PAGE_SIZE) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    } else {
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addVideos(response.body().getData().getItem());
                        mAdapter.notifyItemRangeInserted(startPosition, response.body().getData().getItem().size());
                        mCurrentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> call, Throwable t) {
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
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
        CallUtils.cancelCall(mArchiveCall);
    }

    @Override
    public void onVideoClick(String aid) {
        VideoDetailActivity.startActivity(getContext(), aid);
    }
}
