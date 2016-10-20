package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.activity.VideoDetailActivity;
import com.lh.imbilibili.view.adapter.categoryfragment.PartionHomeRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.categoryfragment.PartionItemDecoration;
import com.lh.imbilibili.view.adapter.categoryfragment.model.PartionModel;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/9/29.
 * 分区主页
 */

public class PartionHomeFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SwipeRefreshLayout.OnRefreshListener, PartionHomeRecyclerViewAdapter.OnItemClickListener {
    private static final String EXTRA_DATA = "partionModel";
    private static final int PAGE_SIZE = 50;

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private PartionHome mPartionHomeData;
    private PartionHomeRecyclerViewAdapter mAdapter;
    private PartionModel mPartionModel;
    private Call<BilibiliDataResponse<PartionHome>> mPartionHomeDataCall;
    private Call<BilibiliDataResponse<List<PartionVideo>>> mPartionDynamicCall;

    private int mCurrentPage = 1;

    private boolean mNeedRefresh;

    public static PartionHomeFragment newInstance(PartionModel partionModel) {
        PartionHomeFragment fragment = new PartionHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, partionModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        mPartionModel = getArguments().getParcelable(EXTRA_DATA);
        ButterKnife.bind(this, view);
        mCurrentPage = 1;
        mNeedRefresh = true;
        initRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_partion_home;
    }

    private void loadData() {
        mPartionHomeDataCall = RetrofitHelper.getInstance().getPartionService().getPartionInfo(mPartionModel.getId(), "*");
        mPartionHomeDataCall.enqueue(new Callback<BilibiliDataResponse<PartionHome>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<PartionHome>> call, Response<BilibiliDataResponse<PartionHome>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.body().getCode() == 0) {
                    mPartionHomeData = response.body().getData();
                    mAdapter.setPartionData(mPartionHomeData);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<PartionHome>> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadDynamicData() {
        mPartionDynamicCall = RetrofitHelper.getInstance().getPartionService().getPartionDynamic(mPartionModel.getId(), mCurrentPage, PAGE_SIZE);
        mPartionDynamicCall.enqueue(new Callback<BilibiliDataResponse<List<PartionVideo>>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<List<PartionVideo>>> call, Response<BilibiliDataResponse<List<PartionVideo>>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getData().size() == 0) {
                        mRecyclerView.setLoadView(R.string.no_data_tips, false);
                        mRecyclerView.setEnableLoadMore(false);
                    }
                    if (mNeedRefresh) {//需要全部刷新
                        mNeedRefresh = false;
                        mAdapter.clearDynamicVideo();
                        mAdapter.addDynamicVideo(response.body().getData());
                        mAdapter.notifyDataSetChanged();
                    } else {//加载更多
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addDynamicVideo(response.body().getData());
                        mAdapter.notifyItemRangeInserted(startPosition, response.body().getData().size());
                    }
                    mCurrentPage++;
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<List<PartionVideo>>> call, Throwable t) {
                mRecyclerView.setLoading(false);
            }
        });
    }

    @Override
    protected void fetchData() {
        loadData();
        loadDynamicData();
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mRecyclerView.getItemViewType(position)) {
                    case PartionHomeRecyclerViewAdapter.TYPE_BANNER:
                    case PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_HEAD:
                    case PartionHomeRecyclerViewAdapter.TYPE_SUB_PARTION:
                    case LoadMoreRecyclerView.TYPE_LOAD_MORE:
                        return 2;
                    case PartionHomeRecyclerViewAdapter.TYPE_HOT_RECOMMEND_ITEM:
                    case PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_ITEM:
                    case PartionHomeRecyclerViewAdapter.TYPE_PARTION_DYNAMIC_ITME:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        PartionItemDecoration itemDecoration = new PartionItemDecoration(getContext());
        if (mAdapter == null) {
            mAdapter = new PartionHomeRecyclerViewAdapter(getContext(), mPartionModel);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onLoadMore() {
        loadDynamicData();
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        mNeedRefresh = true;
        mRecyclerView.setEnableLoadMore(true);
        mRecyclerView.setLoadView(R.string.loading, true);
        loadData();
        loadDynamicData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mPartionDynamicCall, mPartionHomeDataCall);
    }

    @Override
    public void onVideoItemClick(String aid) {
        VideoDetailActivity.startActivity(getContext(), aid);
    }

    @Override
    public void onSubPartionItemClick(int position) {
        BusUtils.getBus().post(new SubPartionClickEvent(position));
    }

    @Override
    public void onHeadItemClick(int type) {
        if (type == PartionHomeRecyclerViewAdapter.TYPE_NEW_VIDEO_HEAD) {
            BusUtils.getBus().post(new SubPartionClickEvent(1));
        }
    }

    public static class SubPartionClickEvent {
        public int position;

        SubPartionClickEvent(int position) {
            this.position = position;
        }
    }
}
