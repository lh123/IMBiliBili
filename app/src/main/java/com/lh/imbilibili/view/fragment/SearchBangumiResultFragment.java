package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.BangumiSearchResult;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.LoadAnimationUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.activity.BangumiDetailActivity;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.search.BangumiSearchAdapter;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/6.
 * 搜索界面-番剧
 */

public class SearchBangumiResultFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener {

    private static final String EXTRA_DATA = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.loading)
    ImageView mIvLoading;

    private String mKeyWord;
    private Call<BilibiliDataResponse<BangumiSearchResult>> mSearchCall;
    private BangumiSearchResult mSearchResult;
    private BangumiSearchAdapter mAdapter;
    private int mCurrentPage;

    public static SearchBangumiResultFragment newInstance(String keyWord) {
        SearchBangumiResultFragment fragment = new SearchBangumiResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DATA, keyWord);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mKeyWord = getArguments().getString(EXTRA_DATA);
        initRecyclerView();
        mCurrentPage = 1;
        mIvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void fetchData() {
        loadSearchPage(mCurrentPage);
    }

    private void initRecyclerView() {
        mAdapter = new BangumiSearchAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutItemDecoration itemDecoration = new LinearLayoutItemDecoration(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setShowLoadingView(false);
        mAdapter.setOnBangumiItemClickListener(new BangumiSearchAdapter.OnBangumiItemClickListener() {
            @Override
            public void onBangumiClick(String aid) {
                BangumiDetailActivity.startActivity(getContext(), aid);
            }
        });
    }

    private void loadSearchPage(int page) {
        if (mCurrentPage == 1) {
            LoadAnimationUtils.startLoadAnimate(mIvLoading, R.drawable.anim_search_loading);
        }
        mSearchCall = RetrofitHelper.getInstance().getSearchService().getBangumiSearchResult(mKeyWord, page, 20, 1);
        mSearchCall.enqueue(new Callback<BilibiliDataResponse<BangumiSearchResult>>() {
            @Override
            public void onResponse(Call<BilibiliDataResponse<BangumiSearchResult>> call, Response<BilibiliDataResponse<BangumiSearchResult>> response) {
                mRecyclerView.setLoading(false);
                if (response.body().isSuccess()) {
                    if (response.body().getData().getItems() != null && response.body().getData().getItems().size() > 0) {
                        if (response.body().getData().getPages() == 1) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLoadView(R.string.no_data_tips, false);
                        }
                        if (mCurrentPage == 1) {
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, 0);
                            mRecyclerView.setEnableLoadMore(true);
                            mRecyclerView.setShowLoadingView(true);
                        }
                        mSearchResult = response.body().getData();
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addData(mSearchResult.getItems());
                        mAdapter.notifyItemRangeInserted(startPosition, mSearchResult.getItems().size());
                        mCurrentPage++;
                    } else {
                        if (mCurrentPage == 1) {//first
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        } else {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLoadView(R.string.no_data_tips, false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BilibiliDataResponse<BangumiSearchResult>> call, Throwable t) {
                mCurrentPage--;
                mRecyclerView.setLoading(false);
                ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mSearchCall);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search_result_list;
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadSearchPage(mCurrentPage);
    }
}
