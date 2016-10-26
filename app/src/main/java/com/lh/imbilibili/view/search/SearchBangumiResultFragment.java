package com.lh.imbilibili.view.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.BangumiSearchResult;
import com.lh.imbilibili.utils.LoadAnimationUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.search.BangumiSearchAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    private Subscription mSearchSub;
    private BangumiSearchResult mSearchResult;
    private BangumiSearchAdapter mAdapter;
    private int mCurrentPage;

    private boolean mIsFirstLoad;

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
        mIsFirstLoad = true;
        mIvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void fetchData() {
        loadSearchPage();
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

    private void loadSearchPage() {
        if (mIsFirstLoad) {
            LoadAnimationUtils.startLoadAnimate(mIvLoading, R.drawable.anim_search_loading);
        }
        mSearchSub = RetrofitHelper.getInstance()
                .getSearchService()
                .getBangumiSearchResult(mKeyWord, mCurrentPage, 20, 1)
                .flatMap(new Func1<BilibiliDataResponse<BangumiSearchResult>, Observable<BangumiSearchResult>>() {
                    @Override
                    public Observable<BangumiSearchResult> call(BilibiliDataResponse<BangumiSearchResult> bangumiSearchResultBilibiliDataResponse) {
                        if (bangumiSearchResultBilibiliDataResponse.isSuccess()) {
                            return Observable.just(bangumiSearchResultBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(bangumiSearchResultBilibiliDataResponse.getCode()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BangumiSearchResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                        LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        mRecyclerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(BangumiSearchResult bangumiSearchResult) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRecyclerView.setLoading(false);
                        if (mIsFirstLoad) {
                            mIsFirstLoad = false;
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, 0);
                            mRecyclerView.setEnableLoadMore(true);
                            mRecyclerView.setShowLoadingView(true);
                        }
                        if (bangumiSearchResult.getPages() == mCurrentPage) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        }
                        mSearchResult = bangumiSearchResult;
                        int startPosition = mAdapter.getItemCount();
                        mAdapter.addData(mSearchResult.getItems());
                        mAdapter.notifyItemRangeInserted(startPosition, mSearchResult.getItems().size());
                        mCurrentPage++;
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mSearchSub);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search_result_list;
    }

    @Override
    public void onLoadMore() {
        loadSearchPage();
    }
}
