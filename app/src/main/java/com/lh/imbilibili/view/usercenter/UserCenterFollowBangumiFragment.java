package com.lh.imbilibili.view.usercenter;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.FollowBangumiRecyclerViewAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.widget.EmptyView;
import com.lh.imbilibili.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/17.
 * 用户中心-追番界面
 */

public class UserCenterFollowBangumiFragment extends BaseFragment implements LoadMoreRecyclerView.OnLoadMoreLinstener, FollowBangumiRecyclerViewAdapter.OnItemClickListener {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    EmptyView mEmptyView;

    private UserCenter mUserCenter;
    private int mCurrentPage;

    private FollowBangumiRecyclerViewAdapter mAdapter;

    private Subscription mUserBangumiSub;

    private Subscription mBusSub;

    private UserCenterDataProvider mUserCenterProvider;

    private boolean mIsInitData;

    public static UserCenterFollowBangumiFragment newInstance() {
        return new UserCenterFollowBangumiFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mCurrentPage = 2;
        mIsInitData = false;
        if (getActivity() instanceof UserCenterDataProvider) {
            mUserCenterProvider = (UserCenterDataProvider) getActivity();
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new FollowBangumiRecyclerViewAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRecyclerView.getItemViewType(position) == LoadMoreRecyclerView.TYPE_LOAD_MORE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(getContext(), true));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreLinstener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBusSub = RxBus.getInstance()
                .toObserverable(UserCenter.class)
                .subscribe(new Action1<UserCenter>() {
                    @Override
                    public void call(UserCenter userCenter) {
                        if (mUserCenter == null) {
                            mUserCenter = userCenter;
                            initData();
                        }
                    }
                });
        if (mUserCenterProvider != null && mUserCenter == null) {
            mUserCenter = mUserCenterProvider.getUserCenter();
            initData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionUtils.unsubscribe(mBusSub);
    }

    public void initData() {
        if (mUserCenter == null || mIsInitData) {
            return;
        }
        mIsInitData = true;
        if (mUserCenter.getSetting().getBangumi() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_permission);
            mEmptyView.setText(R.string.space_tips_no_permission);
            return;
        }
        if (mUserCenter.getSeason().getCount() == 0) {
            mRecyclerView.setEnableLoadMore(false);
            mRecyclerView.setShowLoadingView(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setImgResource(R.drawable.img_tips_error_space_no_data);
            mEmptyView.setText(R.string.no_data_tips);
        } else {
            mRecyclerView.setShowLoadingView(true);
            if (mUserCenter.getSeason().getCount() <= PAGE_SIZE) {
                mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                mRecyclerView.setEnableLoadMore(false);
            } else {
                mRecyclerView.setEnableLoadMore(true);
            }
            mAdapter.addSeasons(mUserCenter.getSeason().getItem());
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadBangumiData() {
        mUserBangumiSub = RetrofitHelper.getInstance()
                .getUserService()
                .getUserBangumi(mCurrentPage, PAGE_SIZE, System.currentTimeMillis(), Integer.parseInt(mUserCenter.getCard().getMid()))
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>, Observable<UserCenter.CenterList<UserCenter.Season>>>() {
                    @Override
                    public Observable<UserCenter.CenterList<UserCenter.Season>> call(BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>> centerListBilibiliDataResponse) {
                        if (centerListBilibiliDataResponse.isSuccess()) {
                            return Observable.just(centerListBilibiliDataResponse.getData());
                        } else {
                            return Observable.error(new ApiException(centerListBilibiliDataResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserCenter.CenterList<UserCenter.Season>>() {
                    @Override
                    public void call(UserCenter.CenterList<UserCenter.Season> seasonCenterList) {
                        mRecyclerView.setLoading(false);
                        if (seasonCenterList.getCount() < PAGE_SIZE) {
                            mRecyclerView.setEnableLoadMore(false);
                            mRecyclerView.setLodingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
                        } else {
                            int startPosition = mAdapter.getItemCount();
                            mAdapter.addSeasons(seasonCenterList.getItem());
                            mAdapter.notifyItemRangeInserted(startPosition, seasonCenterList.getItem().size());
                            mCurrentPage++;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRecyclerView.setLoading(false);
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_list;
    }

    @Override
    public void onLoadMore() {
        loadBangumiData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mUserBangumiSub);
    }

    @Override
    public void onItemClick(String seasonId) {
        BangumiDetailActivity.startActivity(getContext(), seasonId);
    }
}
