package com.lh.imbilibili.view.usercenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.BiliBiliDataFunc;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.usercenter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/15.
 * 用户中心Activity
 */

public class UserCenterActivity extends BaseActivity implements UserCenterDataProvider {

    private static final String EXTRA_ID = "id";
    private static final String EXTRA_PAGE = "page";
    private static final int PAGE_SIZE = 10;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.user_background)
    ImageView mIvBackground;
    @BindView(R.id.nick_name)
    TextView mTvNickName;
    @BindView(R.id.user_sex)
    ImageView mIvSex;
    @BindView(R.id.user_level)
    ImageView mIvLevel;
    @BindView(R.id.tv_follow_users)
    TextView mTvFollowUsers;
    @BindView(R.id.tv_fans)
    TextView mTvFans;
    @BindView(R.id.author_verified_layout)
    ViewGroup mAuthorLayout;
    @BindView(R.id.author_verified_text)
    TextView mAuthorText;
    @BindView(R.id.user_desc)
    TextView mTvUserDesc;
    @BindView(R.id.user_avatar)
    ImageView mIvUserAvator;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private int mId;
    private int[] mLevelImg = new int[]{R.drawable.ic_lv0_large,
            R.drawable.ic_lv1_large,
            R.drawable.ic_lv2_large,
            R.drawable.ic_lv3_large,
            R.drawable.ic_lv4_large,
            R.drawable.ic_lv5_large,
            R.drawable.ic_lv6_large,
            R.drawable.ic_lv7_large,
            R.drawable.ic_lv8_large,
            R.drawable.ic_lv9_large};

    private UserCenter mUserCenter;
    private String[] mtitles;
    private int mDefaultPage;
    private Subscription mUserInfoSubscription;

    private Subscription mBusSub;

    /**
     * @param context Context
     * @param id      用户Id
     * @param page    默认展示的页面
     */
    public static void startActivity(Context context, int id, int page) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_PAGE, page);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        ButterKnife.bind(this);
        StatusBarUtils.setCollapsingToolbarLayout(this, mToolbar, mAppBarLayout, mCollapsingToolbarLayout);
        mId = getIntent().getIntExtra(EXTRA_ID, 0);
        mDefaultPage = getIntent().getIntExtra(EXTRA_PAGE, 0);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(this).load("file:///android_asset/ic_zone_background.png").centerCrop().into(mIvBackground);
        initViewPager();
        loadUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBusSub = RxBus.getInstance()
                .toObserverable(UserCenterHomeFragment.ItemClickEvent.class)
                .subscribe(new Action1<UserCenterHomeFragment.ItemClickEvent>() {
                    @Override
                    public void call(UserCenterHomeFragment.ItemClickEvent itemClickEvent) {
                        if (itemClickEvent.position >= 0 && itemClickEvent.position < mtitles.length) {
                            mViewPager.setCurrentItem(itemClickEvent.position);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionUtils.unsubscribe(mBusSub);
    }

    private void initViewPager() {
        BaseFragment[] mFragments = new BaseFragment[]{UserCenterHomeFragment.newInstance(),
                UserCenterArchiveFragment.newInstance(false),
                UserCenterFavouriteFragment.newInstance(),
                UserCenterFollowBangumiFragment.newInstance(),
                UserCenterCommunityFragment.newInstance(),
                UserCenterArchiveFragment.newInstance(true),
                UserCenterGameFragment.newInstance()};
        mtitles = new String[]{"主页", "投稿", "收藏", "追番", "兴趣圈", "投币", "游戏"};
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments, mtitles);
        mViewPager.setAdapter(viewPagerAdapter);
        mTabs.setupWithViewPager(mViewPager);
    }

    private void loadUserInfo() {
        mUserInfoSubscription = RetrofitHelper.getInstance()
                .getUserService()
                .getUserSpaceInfo(PAGE_SIZE, System.currentTimeMillis(), mId)
                .subscribeOn(Schedulers.io())
                .map(new BiliBiliDataFunc<UserCenter>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserCenter>() {
                    @Override
                    public void call(UserCenter userCenter) {
                        mUserCenter = userCenter;
                        bindViewData(mUserCenter);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(UserCenterActivity.this, "加载失败", Toast.LENGTH_SHORT);
                    }
                });
    }

    private void bindViewData(UserCenter userCenter) {
        RxBus.getInstance().send(userCenter);
        modifyTabsTitle(userCenter);
        mToolbar.setTitle(userCenter.getCard().getName());
        if (!TextUtils.isEmpty(userCenter.getImages().getImgUrl())) {
            Glide.with(this).load(userCenter.getImages().getImgUrl()).centerCrop().into(mIvBackground);
        }
        Glide.with(this).load(userCenter.getCard().getFace()).asBitmap().transform(new CircleTransformation(getApplicationContext())).into(mIvUserAvator);
        mTvNickName.setText(userCenter.getCard().getName());
        mIvLevel.setImageResource(mLevelImg[userCenter.getCard().getLevelInfo().getCurrentLevel()]);
        switch (userCenter.getCard().getSex()) {
            case "男":
                mIvSex.setImageResource(R.drawable.ic_user_male_border);
                break;
            case "女":
                mIvSex.setImageResource(R.drawable.ic_user_female_border);
                break;
            default:
                mIvSex.setImageResource(R.drawable.ic_user_gay_border);
                break;
        }
        mTvFollowUsers.setText(StringUtils.formateNumber(userCenter.getCard().getAttention()));
        mTvFans.setText(StringUtils.formateNumber(userCenter.getCard().getFans()));
        if (userCenter.getCard().getOfficialVerify().getType() == 1 && !TextUtils.isEmpty(userCenter.getCard().getOfficialVerify().getDesc())) {
            mAuthorLayout.setVisibility(View.VISIBLE);
            mAuthorText.setText(userCenter.getCard().getOfficialVerify().getDesc());
        }
        if (TextUtils.isEmpty(userCenter.getCard().getSign())) {
            mTvUserDesc.setText("这个人懒死了,什么都没有写(・－・。)");
        } else {
            mTvUserDesc.setText(userCenter.getCard().getSign());
        }
        mViewPager.setCurrentItem(mDefaultPage);
    }

    private void modifyTabsTitle(UserCenter userCenter) {
        String foramt = " %d";
        if (userCenter.getArchive() != null) {
            mtitles[1] += StringUtils.format(foramt, userCenter.getArchive().getCount());
        }
        if (userCenter.getSetting().getFavVideo() != 0 && userCenter.getFavourite() != null) {
            mtitles[2] += StringUtils.format(foramt, userCenter.getFavourite().getCount());
        }
        if (userCenter.getSetting().getBangumi() != 0 && userCenter.getSeason() != null) {
            mtitles[3] += StringUtils.format(foramt, userCenter.getSeason().getCount());
        }
        if (userCenter.getSetting().getGroups() != 0 && userCenter.getCommunity() != null) {
            mtitles[4] += StringUtils.format(foramt, userCenter.getCommunity().getCount());
        }
        if (userCenter.getSetting().getCoinsVideo() != 0 && userCenter.getCoinArchive() != null) {
            mtitles[5] += StringUtils.format(foramt, userCenter.getCoinArchive().getCount());
        }
        if (userCenter.getSetting().getPlayedGame() != 0 && userCenter.getGame() != null) {
            mtitles[6] += StringUtils.format(foramt, userCenter.getGame().getCount());
        }
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserInfoSubscription != null && !mUserInfoSubscription.isUnsubscribed()) {
            mUserInfoSubscription.unsubscribe();
        }
    }

    @Override
    public UserCenter getUserCenter() {
        return mUserCenter;
    }
}
