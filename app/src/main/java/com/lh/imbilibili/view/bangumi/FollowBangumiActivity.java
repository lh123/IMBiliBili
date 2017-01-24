package com.lh.imbilibili.view.bangumi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.model.attention.FollowBangumiResponse;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.adapter.attention.AttentionBangumiRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.attention.LineItemDecoration;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/14.
 * 显示用户的追番列表
 */

public class FollowBangumiActivity extends BaseActivity implements LoadMoreRecyclerView.OnLoadMoreLinstener, AttentionBangumiRecyclerViewAdapter.OnItemClickListener {

    private static final int PAGE_SIZE = 20;

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private int mCurrentPage;
    private AttentionBangumiRecyclerViewAdapter mAdapter;

    private Disposable mConcernedBangumiSub;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FollowBangumiActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folow_bangumi);
        ButterKnife.bind(this);
        mCurrentPage = 1;
        StatusBarUtils.setSimpleToolbarLayout(this, mToolbar);
        mToolbar.setTitle("追番");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRecyclerView();
        loadConcernedBangumi();
    }

    private void initRecyclerView() {
        mAdapter = new AttentionBangumiRecyclerViewAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new LineItemDecoration(ContextCompat.getColor(this, R.color.gray)));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnItemClickListener(this);
    }

    private void loadConcernedBangumi() {
        mConcernedBangumiSub = CommonHelper.getInstance()
                .getAttentionService()
                .getConcernedBangumi(mCurrentPage, PAGE_SIZE, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<FollowBangumiResponse<List<FollowBangumi>>, List<FollowBangumi>>handlerResult())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribeWith(new DisposableSingleObserver<List<FollowBangumi>>(){
                    @Override
                    public void onSuccess(List<FollowBangumi> followBangumis) {
                        mRecyclerView.setLoading(false);
                        if (followBangumis.isEmpty()) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addBangumi(followBangumis);
                            if (mCurrentPage == 1) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                mAdapter.notifyItemRangeInserted(startPosition, followBangumis.size());
                            }
                            mCurrentPage++;
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
        loadConcernedBangumi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mConcernedBangumiSub);
    }

    @Override
    public void onItemClick(String seasonId) {
        BangumiDetailActivity.startActivity(this, seasonId);
    }
}
