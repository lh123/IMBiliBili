package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.SeasonGroup;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.seasongroupactivity.SeasonGroupAdapter;
import com.lh.imbilibili.view.adapter.seasongroupactivity.SeasonGroupItemDecoration;
import com.lh.imbilibili.view.adapter.seasongroupactivity.SeasonYearAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by home on 2016/8/8.
 */
public class SeasonGroupActivity extends BaseActivity implements SeasonYearAdapter.onYearItemClickListener, SeasonGroupAdapter.onItemClickListener {

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.recycler_view_year)
    RecyclerView mRecyclerViewYear;
    @BindView(R.id.exit)
    LinearLayout mExit;
    @BindView(R.id.year)
    TextView mTvYear;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer)
    ViewGroup mDrawer;

    private SeasonGroupAdapter mSeasonGroupAdapter;
    private SeasonYearAdapter mSeasonYearAdapter;

    private List<SeasonGroup> mSeasonGroups;
    private List<Integer> mYears;

    private Call<BiliBiliResultResponse<List<SeasonGroup>>> mSeasonGroupCall;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SeasonGroupActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_group);
        ButterKnife.bind(this);
        StatusBarUtils.setDrawerToolbarLayout(this, mToolbar, mDrawer);
        initView();
        initRecyclerView();
        loadData();
    }

    private void initView() {
        mToolbar.setTitle("分季列表");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mDrawer);
            }
        });
        mTvYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawer);
            }
        });
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 4 == 0) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        mSeasonGroupAdapter = new SeasonGroupAdapter(this, mSeasonGroups);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SeasonGroupItemDecoration(this));
        mRecyclerView.setAdapter(mSeasonGroupAdapter);
        GridLayoutManager yearGridLayoutManager = new GridLayoutManager(this, 5);
        mSeasonYearAdapter = new SeasonYearAdapter(this, mYears);
        mRecyclerViewYear.setLayoutManager(yearGridLayoutManager);
        mRecyclerViewYear.addItemDecoration(new GridLayoutItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_large_spacing), false));
        mRecyclerViewYear.setAdapter(mSeasonYearAdapter);
        mSeasonYearAdapter.setOnYearItemClickListener(this);
        mSeasonGroupAdapter.setOnItemClickListener(this);
    }

    private void loadData() {
        mSeasonGroupCall = RetrofitHelper
                .getInstance()
                .getBangumiService()
                .getSeasonGroup(System.currentTimeMillis());
        mSeasonGroupCall.enqueue(new Callback<BiliBiliResultResponse<List<SeasonGroup>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<SeasonGroup>>> call, Response<BiliBiliResultResponse<List<SeasonGroup>>> response) {
                if (response.body().getCode() == 0) {
                    mSeasonGroups = response.body().getResult();
                    mYears = canculateYear(mSeasonGroups);
                    mTvYear.setText(String.valueOf(mYears.get(0)));
                    mSeasonGroupAdapter.setSeasonGroups(mSeasonGroups);
                    mSeasonGroupAdapter.notifyDataSetChanged();
                    mSeasonYearAdapter.setYears(mYears);
                    mSeasonYearAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<List<SeasonGroup>>> call, Throwable t) {

            }
        });
    }

    private List<Integer> canculateYear(List<SeasonGroup> seasonGroups) {
        List<Integer> years = new ArrayList<>();
        for (int i = 0; i < seasonGroups.size(); i++) {
            int year = seasonGroups.get(i).getYear();
            if (!years.contains(year)) {
                years.add(seasonGroups.get(i).getYear());
            }
        }
        return years;
    }

    private int canculatePositionByYear(int year) {
        int index = 0;
        for (int i = 0; i < mSeasonGroups.size(); i++) {
            if (mSeasonGroups.get(i).getYear() == year) {
                index = i;
                break;
            }
        }
        return index * 4;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mSeasonGroupCall);
    }

    @Override
    public void onYearItemClick(int year) {
        mTvYear.setText(String.valueOf(year));
        mDrawerLayout.closeDrawer(mDrawer);
        ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(canculatePositionByYear(year), 0);
    }

    @Override
    public void onItemClick(int type, RecyclerView.ViewHolder viewHolder) {
        if (type == SeasonGroupAdapter.SEASON_ITEM) {
            SeasonGroupAdapter.SeasonItemHolder seasonItemHolder = (SeasonGroupAdapter.SeasonItemHolder) viewHolder;
            BangumiDetailActivity.startActivity(this, seasonItemHolder.getSeasonId());
        } else if (type == SeasonGroupAdapter.SEASON_HEAD) {
            SeasonGroupAdapter.SeasonHeadHolder seasonHeadHolder = (SeasonGroupAdapter.SeasonHeadHolder) viewHolder;
            BangumiIndexActivity.startActivity(this, seasonHeadHolder.getYear(), seasonHeadHolder.getMonth(), new ArrayList<Integer>(mYears));
        }
    }
}
