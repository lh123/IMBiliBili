package com.lh.imbilibili.view.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.LazyLoadFragment;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.search.SearchRecyclerViewAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/5.
 * 搜索界面
 */

public class SearchResultFragment extends LazyLoadFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, SearchRecyclerViewAdapter.OnSearchItemClickListener {

    private static final int PAGE_SIZE = 20;

    private static final String EXTRA_DATA = "searchData";
    private static final String EXTRA_KEY = "keyWord";

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    private SearchRecyclerViewAdapter mAdapter;
    private int mCurrentPage;
    private Disposable mSearchSub;
    private SearchResult mSearchResult;
    private String mKeyWord;

    public static SearchResultFragment newInstance(String keyWord, SearchResult searchResult) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_KEY, keyWord);
        bundle.putParcelable(EXTRA_DATA, searchResult);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mKeyWord = getArguments().getString(EXTRA_KEY);
        SearchResult searchResult = getArguments().getParcelable(EXTRA_DATA);
        mCurrentPage = 2;
        mAdapter = new SearchRecyclerViewAdapter(getContext(), searchResult);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutItemDecoration itemDecoration = new LinearLayoutItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnSearchItemClickListener(this);
    }

    @Override
    protected void fetchData() {
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search_result_list;
    }

    private void loadSearchPage() {
        mSearchSub = CommonHelper.getInstance()
                .getSearchService()
                .getSearchResult(0, mKeyWord, mCurrentPage, PAGE_SIZE)
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<SearchResult>, SearchResult>handlerResult())
                .subscribeOn(Schedulers.io())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SearchResult>() {
                    @Override
                    public void onSuccess(SearchResult searchResult) {
                        mRecyclerView.setLoading(false);
                        if (searchResult.getItems().getArchive().size() != 0) {
                            mSearchResult = searchResult;
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addData(mSearchResult.getItems().getArchive());
                            mAdapter.notifyItemRangeInserted(startPosition, mSearchResult.getItems().getArchive().size());
                            mCurrentPage++;
                        } else {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    @Override
    public void onLoadMore() {
        loadSearchPage();
    }

    @Override
    public void onSearchItemClick(String param, int type) {
        switch (type) {
            case SearchRecyclerViewAdapter.TYPE_SEASON:
                BangumiDetailActivity.startActivity(getContext(), param);
                break;
            case SearchRecyclerViewAdapter.TYPE_VIDEO:
                VideoDetailActivity.startActivity(getContext(), param);
                break;
            case SearchRecyclerViewAdapter.TYPE_SEASON_MORE:
                if (getActivity() instanceof OnSeasonMoreClickListener) {
                    ((OnSeasonMoreClickListener) getActivity()).onSeasonMoreClick();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mSearchSub);
    }

    public interface OnSeasonMoreClickListener {
        void onSeasonMoreClick();
    }
}
