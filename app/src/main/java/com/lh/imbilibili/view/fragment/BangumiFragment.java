package com.lh.imbilibili.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.IndexBangumiRecommend;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.activity.FollowBangumiActivity;
import com.lh.imbilibili.view.activity.SeasonGroupActivity;
import com.lh.imbilibili.view.activity.WebViewActivity;
import com.lh.imbilibili.view.adapter.bangumifragment.BangumiAdapter;
import com.lh.imbilibili.view.adapter.bangumifragment.BangumiIndexItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/7/6.
 * 番剧页面
 */
public class BangumiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.OnLoadMoreLinstener, BangumiAdapter.OnItemClickListener {

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView recyclerView;

    private IndexPage indexData;

    private BangumiAdapter adapter;

    private String mCursor;

    private Call<BiliBiliResultResponse<IndexPage>> indexCall;
    private Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> recommendCall;

    private boolean mNeedRefresh; //是否需要全部刷新

    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_bangumi;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mNeedRefresh = true;
        initRecyclerView();
        loadAllData();
    }

    private void loadAllData() {
        mCursor = "-1";
        loadIndexData();
        loadBangumiRecommendData(mCursor, 10);
    }

    private void initRecyclerView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new BangumiAdapter(getContext());
        adapter.setItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = recyclerView.getItemViewType(position);
                if (type == BangumiAdapter.BANNER ||
                        type == BangumiAdapter.NAV ||
                        type == BangumiAdapter.SERIALIZING_HEAD ||
                        type == BangumiAdapter.SEASON_BANGUMI_HEAD ||
                        type == BangumiAdapter.BANGUMI_RECOMMEND_HEAD ||
                        type == BangumiAdapter.BANGUMI_RECOMMEND_ITEM ||
                        type == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.addItemDecoration(new BangumiIndexItemDecoration(getActivity()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreLinstener(this);
    }

    private void loadIndexData() {
        indexCall = RetrofitHelper.getInstance().getBangumiService()
                .getIndexPage(System.currentTimeMillis());
        indexCall.enqueue(new Callback<BiliBiliResultResponse<IndexPage>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<IndexPage>> call, Response<BiliBiliResultResponse<IndexPage>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body().getCode() == 0) {
                    indexData = response.body().getResult();
                    Collections.sort(indexData.getSerializing());
                    adapter.setmIndexPage(indexData);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<IndexPage>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadBangumiRecommendData(String cursor, int pageSize) {
        recommendCall = RetrofitHelper.getInstance().getBangumiService()
                .getBangumiRecommend(cursor, pageSize, System.currentTimeMillis());
        recommendCall.enqueue(new Callback<BiliBiliResultResponse<List<IndexBangumiRecommend>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> call, Response<BiliBiliResultResponse<List<IndexBangumiRecommend>>> response) {
                recyclerView.setLoading(false);
                if (response.isSuccessful() && response.body().getCode() == 0) {
                    if (response.body().getResult().size() == 0) {
                        recyclerView.setLoadView(R.string.no_data_tips, false);
                        recyclerView.setEnableLoadMore(false);
                        return;
                    }
                    if (mNeedRefresh) {//需要全部刷新
                        mNeedRefresh = false;
                        adapter.clearRecommend();
                        adapter.addBangumis(response.body().getResult());
                        adapter.notifyDataSetChanged();
                    } else {//加载更多
                        int startPosition = adapter.getItemCount();
                        adapter.addBangumis(response.body().getResult());
                        adapter.notifyItemRangeInserted(startPosition, response.body().getResult().size());
                    }
                    mCursor = response.body().getResult().get(response.body().getResult().size() - 1).getCursor();
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> call, Throwable t) {
                recyclerView.setLoading(false);
            }
        });
    }

    @Override
    public String getTitle() {
        return "番剧";
    }

    @Override
    public void onRefresh() {
        mNeedRefresh = true;
        recyclerView.setEnableLoadMore(true);
        recyclerView.setLoadView(R.string.loading, true);
        loadAllData();
    }

    @Override
    public void onLoadMore() {
        loadBangumiRecommendData(mCursor, 10);
    }

    @Override
    public void onClick(int itemType, String data) {
        if (itemType == BangumiAdapter.SERIALIZING_GRID_ITEM) {
            BangumiDetailActivity.startActivity(getContext(), data);
        } else if (itemType == BangumiAdapter.SEASON_BANGUMI_ITEM) {
            BangumiDetailActivity.startActivity(getContext(), data);
        } else if (itemType == BangumiAdapter.BANGUMI_RECOMMEND_ITEM) {
            if (data.contains("anime")) {
                String[] temp = data.split("anime/");
                BangumiDetailActivity.startActivity(getContext(), temp[temp.length - 1]);
            } else {
                WebViewActivity.startActivity(getContext(), data);
            }
        } else if (itemType == BangumiAdapter.SEASON_BANGUMI_HEAD) {
            SeasonGroupActivity.startActivity(getContext());
        } else if (itemType == BangumiAdapter.NAV) {
            if (data.equals(String.valueOf(R.id.follow_bangumi))) {
                FollowBangumiActivity.startActivity(getContext());
            }
        }
    }

    @Override
    public void onDestroy() {
        CallUtils.cancelCall(indexCall, recommendCall);
        super.onDestroy();
    }
}
