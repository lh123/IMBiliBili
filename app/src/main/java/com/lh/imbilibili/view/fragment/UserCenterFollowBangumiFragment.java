package com.lh.imbilibili.view.fragment;

import android.support.v7.widget.GridLayoutManager;
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
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.FollowBangumiRecyclerViewAdapter;
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
 * 用户中心-追番界面
 */

public class UserCenterFollowBangumiFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, FollowBangumiRecyclerViewAdapter.OnItemClickListener {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private UserCenter mUserCenter;
    private int mCurrentPage;

    private FollowBangumiRecyclerViewAdapter mAdapter;

    private Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>> mBangumiCall;

    public static UserCenterFollowBangumiFragment newInstance() {
        return new UserCenterFollowBangumiFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mCurrentPage = 2;
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new FollowBangumiRecyclerViewAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRecyclerView.getItemViewType(position) == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(getContext(), true));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnItemClickListener(this);
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

    @SuppressWarnings("unused")
    @Subscribe
    public void onUserCenterDataLoadFinish(UserCenter userCenter) {
        if (mUserCenter != null) {
            return;
        }
        mUserCenter = userCenter;
        if (userCenter.getSetting().getBangumi() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (userCenter.getSeason().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setShowLoadingView(true);
            if (userCenter.getSeason().getCount() <= PAGE_SIZE) {
                mRecyclerView.setLoadView(R.string.no_data_tips, false);
                mRecyclerView.setEnableLoadMore(false);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mAdapter.addSeasons(userCenter.getSeason().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadBangumiData() {
        mBangumiCall = RetrofitHelper.getInstance().getUserService().getUserBangumi(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()));
        mBangumiCall.enqueue(new Callback<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>> call, Response<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getData().getCount() < PAGE_SIZE) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    } else {
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addSeasons(response.body().getData().getItem());
                        mAdapter.notifyItemRangeInserted(startPosition, response.body().getData().getItem().size());
                        mCurrentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>> call, Throwable t) {
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
        loadBangumiData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mBangumiCall);
    }

    @Override
    public void onItemClick(String seasonId) {
        BangumiDetailActivity.startActivity(getContext(), seasonId);
    }
}
