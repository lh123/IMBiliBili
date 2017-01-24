package com.lh.imbilibili.view.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.search.Nav;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.LoadAnimationUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.search.SearchViewPagerAdapter;
import com.lh.imbilibili.widget.BiliBiliSearchView;

import java.util.ArrayList;
import java.util.List;

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

public class SearchActivity extends BaseActivity implements BiliBiliSearchView.OnSearchListener, SearchResultFragment.OnSeasonMoreClickListener {
    private static final String EXTRA_DATA = "keyWord";

    @BindView(R.id.back)
    View mBack;
    @BindView(R.id.search_layout)
    View mSearchLayout;
    @BindView(R.id.search_bar)
    TextView mTvSearchBar;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.loading)
    ImageView mIvLoading;
    @BindView(R.id.search_result_container)
    View mContainer;

    private List<BaseFragment> mFragments;

    private String mKeyWord;
    private BiliBiliSearchView mSearchView;

    private SearchResult mSearchResult;
    private Disposable mSearchSub;
    private SearchViewPagerAdapter mAdapter;

    public static void startActivity(Context context, String keyWord) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_DATA, keyWord);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        StatusBarUtils.setSearchActivity(this);
        StatusBarUtils.setStatusBarLighMode(this, true);
        mFragments = new ArrayList<>();
        mSearchView = BiliBiliSearchView.newInstance();
        mSearchView.setOnSearchListener(this);
        mKeyWord = getIntent().getStringExtra(EXTRA_DATA);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setKeyWord(mKeyWord);
                mSearchView.show(getSupportFragmentManager());
            }
        });
        mTvSearchBar.setText(mKeyWord);
        mAdapter = new SearchViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        search(mKeyWord);
    }

    private void search(String keyWord) {
        mContainer.setVisibility(View.GONE);
        LoadAnimationUtils.startLoadAnimate(mIvLoading, R.drawable.anim_search_loading);
        mSearchSub = CommonHelper.getInstance()
                .getSearchService()
                .getSearchResult(0, keyWord, 1, 20)
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<SearchResult>, SearchResult>handlerResult())
                .subscribeOn(Schedulers.io())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SearchResult>() {
                    @Override
                    public void onSuccess(SearchResult searchResult) {
                        mSearchResult = searchResult;
                        if (mSearchResult.getItems().getArchive().isEmpty()
                                && mSearchResult.getItems().getMovie().isEmpty()
                                && mSearchResult.getItems().getSeason().isEmpty()) {
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        } else {
                            mContainer.setVisibility(View.VISIBLE);
                            LoadAnimationUtils.stopLoadAnimate(mIvLoading, 0);
                            bindViewData();
                            mIvLoading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadAnimationUtils.stopLoadAnimate(mIvLoading, R.drawable.search_failed);
                        mContainer.setVisibility(View.GONE);
                    }
                });
    }

    private void bindViewData() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : mFragments) {
            transaction.remove(fragment);
        }
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();//立即提交commit
        mFragments.clear();
        List<Nav> navs = mSearchResult.getNav();
        mFragments.add(SearchResultFragment.newInstance(mKeyWord, mSearchResult));
        String[] titles = new String[navs.size()];
        titles[0] = "综合";
        int index = 1;
        for (int i = 0; i < navs.size(); i++) {
            Nav nav = navs.get(i);
            if (i == 0 && nav.getTotal() != 0) {
                mFragments.add(SearchBangumiResultFragment.newInstance(mKeyWord));
                titles[index++] = StringUtils.format("%s(%s)", nav.getName(), checkNum(nav.getTotal()));
            } else if (i == 1 && nav.getTotal() != 0) {
                mFragments.add(SearchUpFragment.newInstance(mKeyWord));
                titles[index++] = StringUtils.format("%s(%s)", nav.getName(), checkNum(nav.getTotal()));
            }
        }
        mAdapter.setData(mFragments, titles);
        mTabLayout.setupWithViewPager(mViewPager);
        mAdapter.notifyDataSetChanged();
    }

    private String checkNum(int num) {
        return num > 100 ? "99+" : num + "";
    }

    @Override
    public void onSearch(String keyWord) {
        mKeyWord = keyWord;
        mTvSearchBar.setText(mKeyWord);
        search(keyWord);
    }

    @Override
    public void onSeasonMoreClick() {
        mViewPager.setCurrentItem(1, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mSearchSub);
    }
}
