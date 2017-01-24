package com.lh.imbilibili.view.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.cachelibrary.strategy.CacheStrategy;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.model.user.UserDetailInfo;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.RxCacheUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.common.LoginActivity;
import com.lh.imbilibili.view.history.HistoryRecordFragment;
import com.lh.imbilibili.view.usercenter.UserCenterActivity;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements IDrawerLayoutActivity, View.OnClickListener {

    private static final String USER_DETAIL_CACHE_NAME = "user_detail";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.container)
    ViewGroup mContainer;
    @BindView(R.id.drawer)
    NavigationView mDrawer;

    private ImageView mIvAvatar;
    private TextView mTvNickName;
    private TextView mTvLevel;
    private TextView mTvMemberState;
    private TextView mTvCoinCount;

    private UserDetailInfo mUserDetailInfo;
    private boolean isShowHome;
    private Disposable mUserInfoSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        StatusBarUtils.setDrawerToolbarTabLayout(this, mDrawerLayout, mDrawer, mContainer);
        View headView = mDrawer.getHeaderView(0);
        mIvAvatar = (ImageView) headView.findViewById(R.id.user_avatar);
        mTvNickName = (TextView) headView.findViewById(R.id.user_nick_text);
        mTvLevel = (TextView) headView.findViewById(R.id.level);
        mTvMemberState = (TextView) headView.findViewById(R.id.member_status);
        mTvCoinCount = (TextView) headView.findViewById(R.id.user_coin_count);
        initView();
        switchFragment(0);
        RxBus.getInstance().register(this);
    }

    private void initView() {
        mIvAvatar.setOnClickListener(this);
        mTvNickName.setOnClickListener(this);
        if (UserManagerUtils.getInstance().getCurrentUser() != null) {
            loadUserInfo();
        } else {
            mIvAvatar.setImageResource(R.drawable.bili_default_avatar);
            mTvNickName.setText("点击头像登陆");
            mTvLevel.setVisibility(View.GONE);
        }
        mDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_home:
                        switchFragment(0);
                        break;
                    case R.id.item_history:
                        switchFragment(1);
                        break;
                }
                closeDrawer();
                return true;
            }
        });
    }

    private void switchFragment(int index) {
        Fragment fragment;
        String tag;
        isShowHome = index == 0;
        switch (index) {
            case 0:
                tag = MainFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = MainFragment.newInstance();
                }
                break;
            case 1:
                tag = HistoryRecordFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = HistoryRecordFragment.newInstance();
                }
                break;
            default:
                return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).commit();
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawer);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_avatar:
            case R.id.user_nick_text:
                if (UserManagerUtils.getInstance().getCurrentUser() == null) {
                    LoginActivity.startActivity(this);
                } else {
                    UserCenterActivity.startActivity(this, UserManagerUtils.getInstance().getCurrentUser().getMid(), 0);
                }
                break;
        }
    }

    @Subscribe(scheduler = EventThread.UI)
    public void onUserInfoReceiver(User user){
        loadUserInfo();
    }

    public void loadUserInfo() {
        mUserInfoSub = CommonHelper.getInstance()
                .getUserService()
                .getUserDetailInfo()
                .compose(RxCacheUtils.getInstance().<UserDetailInfo>transformer(USER_DETAIL_CACHE_NAME, CacheStrategy.priorityRemote(),UserDetailInfo.class))
                .subscribeOn(Schedulers.io())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserDetailInfo>() {
                    @Override
                    public void onSuccess(UserDetailInfo userDetailInfo) {
                        mUserDetailInfo = userDetailInfo;
                        UserManagerUtils.getInstance().setUserDetailInfo(mUserDetailInfo);
                        RxBus.getInstance().post(mUserDetailInfo);
                        bindUserInfoViewWithData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort("加载用户信息失败");
                    }
                });
    }

    private void bindUserInfoViewWithData() {
        Glide.with(this).load(mUserDetailInfo.getFace()).transform(new CircleTransformation(getApplicationContext())).into(mIvAvatar);
        mTvNickName.setText(mUserDetailInfo.getUname());
        mTvLevel.setVisibility(View.VISIBLE);
        mTvLevel.setText(StringUtils.format("LV%d", mUserDetailInfo.getLevelInfo().getCurrentLevel()));
        if (mUserDetailInfo.getIdentification() == 0) {
            mTvMemberState.setVisibility(View.VISIBLE);
        } else {
            mTvMemberState.setVisibility(View.GONE);
        }
        mTvCoinCount.setText(StringUtils.format("硬币 : %d", mUserDetailInfo.getCoins()));
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
            return;
        }
        if (!isShowHome) {
            mDrawer.setCheckedItem(R.id.item_home);
            switchFragment(0);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unRegister(this);
        DisposableUtils.dispose(mUserInfoSub);
    }
}
