package com.lh.imbilibili.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.search.UpSearchResult;
import com.lh.imbilibili.utils.LoadAnimationUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.activity.UserCenterActivity;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.search.UpUserSearchAdapter;
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
 * 搜索界面-Up主
 */

public class SearchUpFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, UpUserSearchAdapter.OnItemClickListener {
    private static final String EXTRA_DATA = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.loading)
    ImageView mIvLoading;

    private String mKeyWord;
    private Subscription mSearchSub;
    private UpSearchResult mSearchResult;
    private UpUserSearchAdapter mAdapter;
    private int mCurrentPage;

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
        LinearLayoutItemDecoration itemDecoration = new LinearLayoutItemDecoration(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mRecyclerView.setEnableLoadMore(false);
        mRecyclerView.setShowLoadingView(false);
        mAdapter.setOnItemClickListener(this);
    }

    private void loadSearchPage() {
        if (mCurrentPage == 1) {
            LoadAnimationUtils.startLoadAnimate(mIvLoading, R.drawable.anim_search_loading);
        }
        mSearchSub = RetrofitHelper.getInstance()
                .getSearchService()
                .getUpSearchResult(mKeyWord, mCurrentPage, 20, 2)
                .flatMap(new Func1<BilibiliDataResponse<UpSearchResult>, Observable<UpSearchResult>>() {
                    @Override
                    public Observable<UpSearchResult> call(BilibiliDataResponse<UpSearchResult> upSearchResultBilibiliDataResponse) {
                        if (upSearchResultBilibiliDataResponse.isSuccess() && upSearchResultBilibiliDataResponse.getData().getItems() != null) {
                            return Observable.just(upSearchResultBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(upSearchResultBilibiliDataResponse.getCode()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UpSearchResult>() {
                    @Override
                    public void onCompleted() {
                        mRecyclerView.setLoading(false);
                        if (mCurrentPage == 2) {
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, 0);
                            mRecyclerView.setEnableLoadMore(true);
                            mRecyclerView.setShowLoadingView(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToast(getContext(), R.string.load_error, Toast.LENGTH_SHORT);
                        LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        mRecyclerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(UpSearchResult upSearchResult) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        if (upSearchResult.getPages() == mCurrentPage) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLoadView(R.string.no_data_tips, false);
                        } else {
                            mSearchResult = upSearchResult;
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addData(mSearchResult.getItems());
                            mAdapter.notifyItemRangeInserted(startPosition, mSearchResult.getItems().size());
                            mCurrentPage++;
                        }
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

    @Override
    public void onUpItemClick(int mid) {
        UserCenterActivity.startActivity(getContext(), mid, 0);
    }
}
