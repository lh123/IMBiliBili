package com.lh.imbilibili.view.bangumi;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.bangumidetail.BangumiEpAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/11/1.
 */

public class EpisodeChooseFragment extends BaseFragment implements BangumiEpAdapter.OnEpClickListener {

    public static final String TAG = "EpisodeChooseFragment";

    private static final String EXTRA_DATA = "bangumiData";
    private static final String EXTRA_MODE = "mode";
    private static final String EXTRA_CURRENT_SELECT = "position";

    public static final int MODE_PLAY = 1;
    public static final int MODE_FEEDBACK = 2;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;

    private int mMode;

    private BangumiEpAdapter mAdapter;

    private BangumiDetail mBangumiDetail;

    public static EpisodeChooseFragment newInstance(BangumiDetail bangumiDetail, int position, int mode) {
        EpisodeChooseFragment fragment = new EpisodeChooseFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, bangumiDetail);
        bundle.putInt(EXTRA_MODE, mode);
        bundle.putInt(EXTRA_CURRENT_SELECT, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        initStatusBar();
        mBangumiDetail = getArguments().getParcelable(EXTRA_DATA);
        mMode = getArguments().getInt(EXTRA_MODE);
        if (mMode == MODE_PLAY) {
            mToolbar.setTitle(mBangumiDetail.getTitle());
        } else {
            mToolbar.setTitle("评论选集");
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(EpisodeChooseFragment.this).commit();
            }
        });
        int position = getArguments().getInt(EXTRA_CURRENT_SELECT);
        mAdapter = new BangumiEpAdapter();
        mAdapter.setItemMatchWidht(true);
        mAdapter.setSelectPosition(position);
        mAdapter.setEpisodes(mBangumiDetail.getEpisodes());
        GridLayoutManager layoutManager;
        if (TextUtils.isEmpty(mBangumiDetail.getEpisodes().get(0).getIndexTitle())) {
            layoutManager = new GridLayoutManager(getContext(), 4);
        } else {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(getResources().getDimensionPixelSize(R.dimen.item_medium_spacing), true));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnEpClickListener(this);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = mToolbar.getLayoutParams();
            params.height += StatusBarUtils.getStatusBarHeight();
            mToolbar.setLayoutParams(params);
            mToolbar.setPadding(0, StatusBarUtils.getStatusBarHeight(), 0, 0);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_episode_choose;
    }

    @Override
    public void onEpClick(int position) {
        EpisodeClickEvent event = new EpisodeClickEvent();
        event.position = position;
        event.mode = mMode;
        if (mMode == MODE_FEEDBACK) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
        RxBus.getInstance().send(event);
    }

    public class EpisodeClickEvent {
        public int position;
        public int mode;
    }

}
