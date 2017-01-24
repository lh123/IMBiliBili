package com.lh.imbilibili.view.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.search.UpSearchResult;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.LoadAnimationUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.search.UpUserSearchAdapter;
import com.lh.imbilibili.view.usercenter.UserCenterActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/6.
 * 搜索界面-Up主
 */

public class SearchUpFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, UpUserSearchAdapter.OnItemClickListener {
    private static final String EXTRA_DATA = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.loading)
    ImageView mIvLoading;

    private String mKeyWord;
    private Disposable mSearchSub;
    private UpSearchResult mSearchResult;
    private UpUserSearchAdapter mAdapter;
    private int mCurrentPage;

    private boolean mIsFirstLoad;

    public static SearchUpFragment newInstance(String keyWord) {
        SearchUpFragment fragment = new SearchUpFragment();
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
        mIsFirstLoad = true;
        mCurrentPage = 1;
        mIvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void fetchData() {
        loadSearchPage();
    }

    private void initRecyclerView() {
        mAdapter = new UpUserSearchAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutItemDecoration itemDecoration = new LinearLayoutItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setShowLoadingView(false);
        mAdapter.setOnItemClickListener(this);
    }

    private void loadSearchPage() {
        if (mIsFirstLoad) {
            LoadAnimationUtils.startLoadAnimate(mIvLoading, R.drawable.anim_search_loading);
        }
        mSearchSub = CommonHelper.getInstance()
                .getSearchService()
                .getUpSearchResult(mKeyWord, mCurrentPage, 20, 2)
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<UpSearchResult>, UpSearchResult>handlerResult())
                .subscribeOn(Schedulers.io())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpSearchResult>() {
                    @Override
                    public void onSuccess(UpSearchResult upSearchResult) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRecyclerView.setLoading(false);
                        if (mIsFirstLoad) {
                            mIsFirstLoad = false;
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, 0);
                            mRecyclerView.setEnableLoadMore(true);
                            mRecyclerView.setShowLoadingView(true);
                        }
                        if (upSearchResult.getPages() == mCurrentPage) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        }
                        mSearchResult = upSearchResult;
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addData(mSearchResult.getItems());
                        mAdapter.notifyItemRangeInserted(startPosition, mSearchResult.getItems().size());
                        mCurrentPage++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToastShort(R.string.load_error);
                        LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mSearchSub);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search_result_list;
    }

    @Override
    public void onLoadMore() {
        loadSearchPage();
    }

    @Override
    public void onUpItemClick(int mid) {
        UserCenterActivity.startActivity(getContext(), mid, 0);
    }
}
