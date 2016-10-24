package com.lh.imbilibili.view.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.bangumi.BangumiIndex;
import com.lh.imbilibili.model.bangumi.BangumiIndexCond;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.bangumiindex.BangumiIndexAdapter;
import com.lh.imbilibili.view.adapter.bangumiindex.GridMenuAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.widget.FlowLayout;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by home on 2016/8/11.
 */
public class BangumiIndexFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, BangumiIndexAdapter.OnBangumiItemClickListener, View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String TAG = "BangumiIndexFragment";

    RelativeLayout mNavView;
    DrawerLayout mDrawerLayout;

    private DrawerViewHolder mDrawerViewHolder;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mLoadMoreRecyclerView;
    @BindView(R.id.iv_hit_up)
    ImageView mIvHitUp;
    @BindView(R.id.iv_hit_down)
    ImageView mIvHitDown;
    @BindView(R.id.btn_sort_hit)
    TextView mTvSortHit;
    @BindView(R.id.iv_recent_up)
    ImageView mIvRecentUp;
    @BindView(R.id.iv_recent_down)
    ImageView mIvRecentDown;
    @BindView(R.id.btn_sort_recent)
    TextView mTvSortRecent;
    @BindView(R.id.iv_day_up)
    ImageView mIvDayUp;
    @BindView(R.id.iv_day_down)
    ImageView mIvDayDown;
    @BindView(R.id.btn_sort_day)
    TextView mTvSortDay;

    @BindView(R.id.btn_filter_type)
    TextView mTvFilterType;
    @BindView(R.id.btn_filter_style)
    TextView mTvFilterStyle;
    @BindView(R.id.btn_filter_status)
    TextView mTvFilterStatus;
    @BindView(R.id.btn_filter_drawer)
    TextView mTvFilterDrawer;

    @BindView(R.id.grid_menu)
    GridView mGridMenu;
    @BindView(R.id.mask)
    View mMask;

    private BangumiIndexAdapter mAdapter;
    private GridMenuAdapter mGridMenuAdapter;

    private BangumiIndex mBangumiIndex;
    private BangumiIndexCond mBangumiIndexCond;
    private List<BangumiIndexCond.Category> mBangumiTypeList;
    private List<BangumiIndexCond.Category> mBangumiStatusList;
    private List<BangumiIndexCond.Category> mBangumiRegionList;

    private List<Integer> mYears;

    private Subscription mBangumiIndexSub;
    private Subscription mBangumiIndexCondSub;

    private int mCurrentPage;
    private int mYear;
    private int mQuarter;

    private int mIndexSort = 0;//0降序 1升序
    private int mIndexSortType = 1;//0更新时间 1追番人数 2开播时间
//    private int mCurrentSelectFilter = -1;

    private String mFilterType = "0";
    private String mFilterStyle = "0";//0全部
    private String mFilterStatus = "0";

    private boolean mIsFliterTypeClicked = false;
    private boolean mIsFliterStyleClicked = false;
    private boolean mIsFliterStatusClicked = false;

    private int mCurrentFilter = 0;//0类型 1风格 2状态 3筛选

    private TextView[] mTvSortButtons;
    private ImageView[] mIvUps;
    private ImageView[] mIvDowns;
    private TextView[] mTvFliterButtons;

    public static BangumiIndexFragment newInstance(int year, int quarter, ArrayList<Integer> years) {
        BangumiIndexFragment fragment = new BangumiIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("quarter", quarter);
        bundle.putIntegerArrayList("years", years);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_bangumi_index;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDrawer();
        StatusBarUtils.setDrawerToolbarLayout(getActivity(), (Toolbar) getActivity().findViewById(R.id.nav_top_bar), mNavView);
        mCurrentPage = 1;
        mYear = getArguments().getInt("year");
        mQuarter = getArguments().getInt("quarter");
        mYears = getArguments().getIntegerArrayList("years");
        mTvSortButtons = new TextView[]{mTvSortRecent, mTvSortHit, mTvSortDay};
        mIvUps = new ImageView[]{mIvRecentUp, mIvHitUp, mIvDayUp};
        mIvDowns = new ImageView[]{mIvRecentDown, mIvHitDown, mIvDayDown};
        mTvFliterButtons = new TextView[]{mTvFilterType, mTvFilterStyle, mTvFilterStatus, mTvFilterDrawer};
        initSortAndFliterView();
        initRecyclerView();
        loadBangumiIndexCond(0);
        loadDataAccordingFlilter();
        initDrawerData();
    }

    private void initSortAndFliterView() {
        mGridMenu.setColumnWidth(4);
        mGridMenuAdapter = new GridMenuAdapter();
        mGridMenu.setAdapter(mGridMenuAdapter);
        mGridMenu.setOnItemClickListener(this);
        mBangumiTypeList = new ArrayList<>();
        mBangumiTypeList.add(generateCategory("全部", "0"));
        mBangumiTypeList.add(generateCategory("TV版", "1"));
        mBangumiTypeList.add(generateCategory("OVA·OAD", "2"));
        mBangumiTypeList.add(generateCategory("剧场版", "3"));
        mBangumiTypeList.add(generateCategory("其他", "4"));
        mBangumiStatusList = new ArrayList<>();
        mBangumiStatusList.add(generateCategory("全部", "0"));
        mBangumiStatusList.add(generateCategory("完结", "2"));
        mBangumiStatusList.add(generateCategory("连载", "1"));
        mBangumiRegionList = new ArrayList<>();
        mBangumiRegionList.add(generateCategory("全部", "0"));
        mBangumiRegionList.add(generateCategory("国产", "1"));
        mBangumiRegionList.add(generateCategory("日本", "2"));
        mBangumiRegionList.add(generateCategory("美国", "3"));
        mBangumiRegionList.add(generateCategory("其他", "4"));
        for (TextView mTvSortButton : mTvSortButtons) {
            mTvSortButton.setOnClickListener(this);
        }
        for (TextView mTvFliterButton : mTvFliterButtons) {
            mTvFliterButton.setOnClickListener(this);
        }
    }

    private BangumiIndexCond.Category generateCategory(String name, String id) {
        BangumiIndexCond.Category category = new BangumiIndexCond.Category();
        category.setTagId(id);
        category.setTagName(name);
        return category;
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mNavView = (RelativeLayout) getActivity().findViewById(R.id.nav_view);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        if(displayMetrics.widthPixels <= 320){
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mNavView.getLayoutParams();
//            params.rightMargin = DisplayUtils.dip2px(getContext(),-65);
//            mNavView.setLayoutParams(params);
//        }
        mDrawerViewHolder = new DrawerViewHolder(mNavView);
    }

    private void initDrawerData() {
        mDrawerViewHolder.setTypeTags(mBangumiTypeList);
        mDrawerViewHolder.setStatusTags(mBangumiStatusList);
        mDrawerViewHolder.setRegionTags(mBangumiRegionList);
        mDrawerViewHolder.setYearTags(mYears, true);
    }

    private void initRecyclerView() {
        mAdapter = new BangumiIndexAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mLoadMoreRecyclerView.getItemViewType(position) == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        GridLayoutItemDecoration itemDecoration = new GridLayoutItemDecoration(getContext(), true);
        mLoadMoreRecyclerView.setLayoutManager(gridLayoutManager);
        mLoadMoreRecyclerView.addItemDecoration(itemDecoration);
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreLinstener(this);
        mLoadMoreRecyclerView.setEnableLoadMore(true);
        mAdapter.setOnBangumiItemClickListener(this);
    }

    private void setSortViewStatus() {
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        int colorBlack = ContextCompat.getColor(getContext(), R.color.black);
        for (int i = 0; i < mTvSortButtons.length; i++) {
            if (i == mIndexSortType) {
                mTvSortButtons[i].setTextColor(colorPrimary);
            } else {
                mTvSortButtons[i].setTextColor(colorBlack);
            }
        }
        for (int i = 0; i < mIvDowns.length; i++) {
            if (i == mIndexSortType) {
                (mIndexSort == 0 ? mIvDowns[i] : mIvUps[i]).setColorFilter(colorPrimary);
                (mIndexSort == 1 ? mIvDowns[i] : mIvUps[i]).setColorFilter(null);
            } else {
                (mIndexSort == 0 ? mIvDowns[i] : mIvUps[i]).setColorFilter(null);
                (mIndexSort == 1 ? mIvDowns[i] : mIvUps[i]).setColorFilter(null);
            }
        }
        if (mFilterType.equals("0")) {
            mTvFilterType.setTextColor(colorBlack);
        } else {
            mTvFilterType.setTextColor(colorPrimary);
        }
        if (mFilterStyle.equals("0")) {
            mTvFilterStyle.setTextColor(colorBlack);
        } else {
            mTvFilterStyle.setTextColor(colorPrimary);
        }
        if (mFilterStatus.equals("0")) {
            mTvFilterStatus.setTextColor(colorBlack);
        } else {
            mTvFilterStatus.setTextColor(colorPrimary);
        }
    }

    private void loadDataAccordingFlilter() {
        setSortViewStatus();
        loadData(mIndexSort, mIndexSortType, mFilterStatus, mCurrentPage, 30, mQuarter, mYear, mFilterStyle, 0, mFilterType);
    }

    private void showGridMenu() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(500);
        mGridMenu.startAnimation(translateAnimation);
        mGridMenu.setVisibility(View.VISIBLE);
        mMask.setVisibility(View.VISIBLE);
    }

    private void hideGridMenu() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
        translateAnimation.setDuration(500);
        mGridMenu.startAnimation(translateAnimation);
        mGridMenu.setVisibility(View.GONE);
        mMask.setVisibility(View.GONE);
    }

    /**
     * @param indexSort    0降序 1升序
     * @param indexType    0更新时间 1追番人数 2开播时间
     * @param isFinish     是否完结 0全部 1连载中 2完结
     * @param page         当前页码
     * @param pageSize     每页所包含的内容数量
     * @param quarter      季度 0：1, 1：4, 2：7, 3：10;
     * @param startYear    开始年份
     * @param tagId        标签Id
     * @param updatePeriod 默认0
     * @param version      类型：0全部 1Tv版 2OVA·OAD 3剧场版 4其他
     */
    private void loadData(int indexSort, int indexType, String isFinish, int page, int pageSize, int quarter, final int startYear, final String tagId, int updatePeriod, String version) {
        mBangumiIndexSub = RetrofitHelper.getInstance()
                .getBangumiService().getBangumiIndex(
                        indexSort, indexType, "", isFinish, page, pageSize,
                        quarter, startYear, tagId, System.currentTimeMillis(), updatePeriod, version)
                .flatMap(new Func1<BiliBiliResultResponse<BangumiIndex>, Observable<BangumiIndex>>() {
                    @Override
                    public Observable<BangumiIndex> call(BiliBiliResultResponse<BangumiIndex> bangumiIndexBiliBiliResultResponse) {
                        if (bangumiIndexBiliBiliResultResponse.isSuccess()) {
                            return Observable.just(bangumiIndexBiliBiliResultResponse.getResult());
                        } else {
                            return Observable.error(new ApiException(bangumiIndexBiliBiliResultResponse.getCode()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BangumiIndex>() {
                    @Override
                    public void call(BangumiIndex bangumiIndex) {
                        mBangumiIndex = bangumiIndex;
                        mAdapter.addBangumis(mBangumiIndex.getList());
                        mAdapter.notifyDataSetChanged();
                        mLoadMoreRecyclerView.setLoading(false);
                        if (mBangumiIndex.getPages().equals(mCurrentPage + "") || mBangumiIndex.getList().size() == 0) {
                            mLoadMoreRecyclerView.setEnableLoadMore(false);
                            mLoadMoreRecyclerView.setLoadView(R.string.no_data_tips, false);
                        } else {
                            mLoadMoreRecyclerView.setEnableLoadMore(true);
                            mLoadMoreRecyclerView.setLoadView(R.string.loading, true);
                        }
                    }
                });
    }

    private void loadBangumiIndexCond(int type) {
        mBangumiIndexCondSub = RetrofitHelper.getInstance()
                .getBangumiService()
                .getBangumiIndexCond(System.currentTimeMillis(), type)
                .flatMap(new Func1<BiliBiliResultResponse<BangumiIndexCond>, Observable<BangumiIndexCond>>() {
                    @Override
                    public Observable<BangumiIndexCond> call(BiliBiliResultResponse<BangumiIndexCond> bangumiIndexCondBiliBiliResultResponse) {
                        if (bangumiIndexCondBiliBiliResultResponse.isSuccess()) {
                            return Observable.just(bangumiIndexCondBiliBiliResultResponse.getResult());
                        } else {
                            return Observable.error(new ApiException(bangumiIndexCondBiliBiliResultResponse.getCode()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BangumiIndexCond>() {
                    @Override
                    public void call(BangumiIndexCond bangumiIndexCond) {
                        mBangumiIndexCond = bangumiIndexCond;
                        BangumiIndexCond.Category defaultCategory = new BangumiIndexCond.Category();
                        defaultCategory.setTagName("全部");
                        defaultCategory.setTagId("0");
                        mBangumiIndexCond.getCategory().add(0, defaultCategory);
                        mDrawerViewHolder.setStyleTags(mBangumiIndexCond.getCategory(), true);
                    }
                });
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadDataAccordingFlilter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mBangumiIndexCondSub, mBangumiIndexSub);
    }

    @Override
    public void onBangumiClick(BangumiIndexAdapter.BangumiHolder holder) {
        BangumiDetailActivity.startActivity(getContext(), holder.getSeasonId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sort_hit:
                if (mIndexSortType != 1) {
                    mIndexSort = 0;
                } else {
                    mIndexSort = 1 - mIndexSort;
                }
                mIndexSortType = 1;
                mCurrentPage = 1;
                mAdapter.setIndexSortType(mIndexSortType);
                mAdapter.clear();
                loadDataAccordingFlilter();
                break;
            case R.id.btn_sort_recent:
                if (mIndexSortType != 0) {
                    mIndexSort = 0;
                } else {
                    mIndexSort = 1 - mIndexSort;
                }
                mIndexSortType = 0;
                mCurrentPage = 1;
                mAdapter.setIndexSortType(mIndexSortType);
                mAdapter.clear();
                loadDataAccordingFlilter();
                break;
            case R.id.btn_sort_day:
                if (mIndexSortType != 2) {
                    mIndexSort = 0;
                } else {
                    mIndexSort = 1 - mIndexSort;
                }
                mIndexSortType = 2;
                mCurrentPage = 1;
                mAdapter.setIndexSortType(mIndexSortType);
                mAdapter.clear();
                loadDataAccordingFlilter();
                break;
            case R.id.btn_filter_type:
                mCurrentFilter = 0;
                mGridMenuAdapter.setmBangumiCategories(mBangumiTypeList);
                mGridMenuAdapter.notifyDataSetChanged();
                if (!mIsFliterTypeClicked) {
                    mGridMenuAdapter.selectItem(mFilterType);
                    showGridMenu();
                } else {
                    hideGridMenu();
                }
                mIsFliterTypeClicked = !mIsFliterTypeClicked;
                mIsFliterStyleClicked = false;
                mIsFliterStatusClicked = false;
                break;
            case R.id.btn_filter_style:
                mCurrentFilter = 1;
                mGridMenuAdapter.setmBangumiCategories(mBangumiIndexCond.getCategory());
                mGridMenuAdapter.notifyDataSetChanged();
                if (!mIsFliterStyleClicked) {
                    mGridMenuAdapter.selectItem(mFilterStyle);
                    showGridMenu();
                } else {
                    hideGridMenu();
                }
                mIsFliterTypeClicked = false;
                mIsFliterStyleClicked = !mIsFliterStyleClicked;
                mIsFliterStatusClicked = false;
                break;
            case R.id.btn_filter_status:
                mCurrentFilter = 2;
                mGridMenuAdapter.setmBangumiCategories(mBangumiStatusList);
                mGridMenuAdapter.notifyDataSetChanged();
                if (!mIsFliterStatusClicked) {
                    mGridMenuAdapter.selectItem(mFilterStatus);
                    showGridMenu();
                } else {
                    hideGridMenu();
                }
                mIsFliterTypeClicked = false;
                mIsFliterStyleClicked = false;
                mIsFliterStatusClicked = !mIsFliterStatusClicked;
                break;
            case R.id.btn_filter_drawer:
                openOrCloseDrawer();
                break;
        }
    }

    private void openOrCloseDrawer() {
        if (!mDrawerLayout.isDrawerOpen(mNavView)) {
            mDrawerLayout.openDrawer(mNavView);
            int index = Integer.valueOf(mFilterType);
            mDrawerViewHolder.mTagsType.selectTag(index);
            mDrawerViewHolder.onItemClick(mDrawerViewHolder.mTagsType, index, null);
        } else {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mCurrentFilter) {
            case 0:
                mFilterType = mBangumiTypeList.get(position).getTagId();
                mGridMenuAdapter.selectItem(mFilterType);
                if (position == 0) {
                    mTvFilterType.setText("类型");
                } else {
                    mTvFilterType.setText(mBangumiTypeList.get(position).getTagName());
                }
                break;
            case 1:
                mFilterStyle = mBangumiIndexCond.getCategory().get(position).getTagId();
                mGridMenuAdapter.selectItem(mFilterStyle);
                if (position == 0) {
                    mTvFilterStyle.setText("风格");
                } else {
                    mTvFilterStyle.setText(mBangumiIndexCond.getCategory().get(position).getTagName());
                }
                break;
            case 2:
                mFilterStatus = mBangumiStatusList.get(position).getTagId();
                mGridMenuAdapter.selectItem(mFilterStatus);
                if (position == 0) {
                    mTvFilterStatus.setText("状态");
                } else {
                    mTvFilterStatus.setText(mBangumiStatusList.get(position).getTagName());
                }
                break;
        }
        hideGridMenu();
        mIsFliterTypeClicked = false;
        mIsFliterStyleClicked = false;
        mIsFliterStatusClicked = false;
        mCurrentPage = 1;
        mAdapter.clear();
        loadDataAccordingFlilter();
    }

    class DrawerViewHolder implements View.OnClickListener, FlowLayout.OnItemClickListener {
        @BindView(R.id.exit)
        View mDrawerExit;

        @BindView(R.id.tv_type_selected)
        TextView mTvTypeSelect;
        @BindView(R.id.btn_type)
        View mBtnTypeExpand;
        @BindView(R.id.tags_type)
        FlowLayout mTagsType;
        @BindView(R.id.iv_type)
        ImageView mIvType;

        @BindView(R.id.tv_style_selected)
        TextView mTvSyleSelect;
        @BindView(R.id.btn_style)
        View mBtnStyleeExpand;
        @BindView(R.id.tags_style)
        FlowLayout mTagsStyle;
        @BindView(R.id.iv_style)
        ImageView mIvStyle;

        @BindView(R.id.tv_status_selected)
        TextView mTvStatusSelect;
        @BindView(R.id.btn_status)
        View mBtnStatusExpand;
        @BindView(R.id.tags_status)
        FlowLayout mTagsStatus;
        @BindView(R.id.iv_status)
        ImageView mIvStatus;

        @BindView(R.id.tv_region_selected)
        TextView mTvRegionSelect;
        @BindView(R.id.btn_region)
        View mBtnRegionExpand;
        @BindView(R.id.tags_region)
        FlowLayout mTagsRegion;
        @BindView(R.id.iv_region)
        ImageView mIvRegion;

        @BindView(R.id.tv_time_selected)
        TextView mTvTimeSelect;
        @BindView(R.id.btn_time)
        View mBtnTimeExpand;
        @BindView(R.id.tags_month)
        FlowLayout mTagsMonth;
        @BindView(R.id.tags_year)
        FlowLayout mTagsYear;
        @BindView(R.id.iv_time)
        ImageView mIvTime;

        @BindView(R.id.reset)
        View mBtnReset;
        @BindView(R.id.confirm)
        View mBtnConfirm;

        public DrawerViewHolder(View view) {
            ButterKnife.bind(this, view);
            mBtnStyleeExpand.setVisibility(View.VISIBLE);
            mDrawerExit.setOnClickListener(this);
            mBtnStyleeExpand.setOnClickListener(this);
            mBtnTimeExpand.setOnClickListener(this);
            mTagsType.setOnItemClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.exit:
                    mDrawerLayout.closeDrawers();
                    break;
                case R.id.btn_style:
                    if (mTagsStyle.getChildCount() == 5) {
                        setStyleTags(mBangumiIndexCond.getCategory(), false);
                        mIvStyle.setImageLevel(2);
                    } else {
                        setStyleTags(mBangumiIndexCond.getCategory(), true);
                        mIvStyle.setImageLevel(1);
                    }
                    break;
                case R.id.btn_time:
                    if (mTagsYear.getChildCount() == 5) {
                        setYearTags(mYears, false);
                        mIvTime.setImageLevel(2);
                    } else {
                        setYearTags(mYears, true);
                        mIvTime.setImageLevel(1);
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(ViewGroup parent, int position, View view) {
            switch (parent.getId()) {
                case R.id.tags_type:
                    String selectText;
                    if (position != 0) {
                        selectText = ":" + mBangumiTypeList.get(position).getTagName();
                    } else {
                        selectText = "";
                    }
                    mTvTypeSelect.setText(selectText);
                    break;
            }
        }

        public void setTypeTags(List<BangumiIndexCond.Category> tags) {
            for (int i = 0; i < tags.size(); i++) {
                mTagsType.addTag(tags.get(i).getTagName(), i);
            }
        }

        public void setStyleTags(List<BangumiIndexCond.Category> tags, boolean isLite) {
            mTagsStyle.removeAllViews();
            if (isLite) {
                int liteAmount = tags.size() > 5 ? 5 : tags.size();
                for (int i = 0; i < liteAmount; i++) {
                    mTagsStyle.addTag(tags.get(i).getTagName(), i);
                    mTagsStyle.selectTag(0);
                }
            } else {
                for (int i = 0; i < tags.size(); i++) {
                    mTagsStyle.addTag(tags.get(i).getTagName(), i);
                    mTagsStyle.selectTag(0);
                }
            }
        }


        public void setStatusTags(List<BangumiIndexCond.Category> tags) {
            mTagsStatus.removeAllViews();
            for (int i = 0; i < tags.size(); i++) {
                mTagsStatus.addTag(tags.get(i).getTagName(), i);
                mTagsStatus.selectTag(0);
            }
        }

        public void setRegionTags(List<BangumiIndexCond.Category> tags) {
            mTagsRegion.removeAllViews();
            for (int i = 0; i < tags.size(); i++) {
                mTagsRegion.addTag(tags.get(i).getTagName(), i);
                mTagsRegion.selectTag(0);
            }
        }

        public void setYearTags(List<Integer> years, boolean isLite) {
            mTagsMonth.removeAllViews();
            mTagsYear.removeAllViews();
            mTagsMonth.addTag("全部", 0);
            mTagsMonth.addTag("1月", 1);
            mTagsMonth.addTag("4月", 2);
            mTagsMonth.addTag("7月", 3);
            mTagsMonth.addTag("10月", 4);
            mTagsYear.addTag("全部", 0);
            int liteAmount;
            if (isLite) {
                liteAmount = years.size() > 4 ? 4 : years.size();
            } else {
                liteAmount = years.size();
            }
            for (int i = 0; i < liteAmount; i++) {
                mTagsYear.addTag(years.get(i) + "", i + 1);
            }
            mTagsMonth.selectTag(0);
            mTagsYear.selectTag(0);
        }
    }
}
