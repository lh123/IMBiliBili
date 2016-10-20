package com.lh.imbilibili.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.attention.DynamicVideo;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.model.attention.FollowBangumiResponse;
import com.lh.imbilibili.model.user.UserResponse;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.activity.FollowBangumiActivity;
import com.lh.imbilibili.view.activity.LoginActivity;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.attention.AttentionItemDecoration;
import com.lh.imbilibili.view.adapter.attention.AttentionRecyclerViewAdapter;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/10.
 * 关注页面
 */

public class AttentionFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SwipeRefreshLayout.OnRefreshListener, AttentionRecyclerViewAdapter.OnItemClickListener {
    private static final int PAGE_SIZE = 20;

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private Call<FollowBangumiResponse<List<FollowBangumi>>> mFollowBangumiCall;
    private Call<BilibiliDataResponse<DynamicVideo>> mDynamicVideoCall;

    private AttentionRecyclerViewAdapter mAdapter;
    private int mCurrentPage;
    private boolean mNeedRefresh;

    public static AttentionFragment newInstance() {
        return new AttentionFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        initRecyclerView();
        mCurrentPage = 1;
        mNeedRefresh = true;
        if (UserManagerUtils.getInstance().getCurrentUser() == null) {
            mBtnLogin.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.startActivity(getContext());
                }
            });
        } else {
            mBtnLogin.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            loadAttentionBangumiData();
            loadDynamicVideoData();
        }
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
        mAdapter = new AttentionRecyclerViewAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getItemViewType(position);
                if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_HEAD
                        || type == AttentionRecyclerViewAdapter.TYPE_DYNAMIC_HEAD
                        || type == AttentionRecyclerViewAdapter.TYPE_DYNAMIC_ITEM
                        || type == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.addItemDecoration(new AttentionItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_attention;
    }

    private void loadAttentionBangumiData() {
        mFollowBangumiCall = RetrofitHelper.getInstance().getAttentionService().getFollowBangumi(UserManagerUtils.getInstance().getCurrentUser().getMid(), System.currentTimeMillis());
        mFollowBangumiCall.enqueue(new Callback<FollowBangumiResponse<List<FollowBangumi>>>() {
            @Override
            public void onResponse(Call<FollowBangumiResponse<List<FollowBangumi>>> call, Response<FollowBangumiResponse<List<FollowBangumi>>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.body().getCode() == 0) {
                    mAdapter.setFollowBangumiData(response.body().getResult());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<FollowBangumiResponse<List<FollowBangumi>>> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
            }
        });
    }

    private void loadDynamicVideoData() {
        mDynamicVideoCall = RetrofitHelper.getInstance().getAttentionService().getDynamicVideo(mCurrentPage, PAGE_SIZE, 0);
        mDynamicVideoCall.enqueue(new Callback<BilibiliDataResponse<DynamicVideo>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<DynamicVideo>> call, Response<BilibiliDataResponse<DynamicVideo>> response) {
                if (response.body().isSuccess()) {
                    mRecyclerView.setLoading(false);
                    if (response.body().getData().getFeeds().size() == 0) {
                        mRecyclerView.setEnableLoadMore(false);
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                    } else {
                        if (mNeedRefresh) {
                            mNeedRefresh = false;
                            mAdapter.clearFeeds();
                            mAdapter.addFeeds(response.body().getData().getFeeds());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            List<DynamicVideo.Feed> feeds = response.body().getData().getFeeds();
                            mAdapter.addFeeds(feeds);
                            mAdapter.notifyItemRangeInserted(startPosition, feeds.size());
                        }
                        mCurrentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<DynamicVideo>> call, Throwable t) {
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onLoginSuccess(UserResponse user) {
        mBtnLogin.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        loadAttentionBangumiData();
        loadDynamicVideoData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mFollowBangumiCall, mDynamicVideoCall);
    }

    @Override
    public String getTitle() {
        return "关注";
    }

    @Override
    public void onLoadMore() {
        loadDynamicVideoData();
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        mNeedRefresh = true;
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLoadView(R.string.loading, true);
        loadAttentionBangumiData();
        loadDynamicVideoData();
    }

    @Override
    public void onItemClick(String id, int type) {
        if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_ITEM) {
            BangumiDetailActivity.startActivity(getContext(), id);
        } else if (type == AttentionRecyclerViewAdapter.TYPE_DYNAMIC_ITEM) {
            VideoDetailActivity.startActivity(getContext(), id);
        } else if (type == AttentionRecyclerViewAdapter.TYPE_BANGUMI_FOLLOW_HEAD) {
            FollowBangumiActivity.startActivity(getContext());
        }
    }
}
