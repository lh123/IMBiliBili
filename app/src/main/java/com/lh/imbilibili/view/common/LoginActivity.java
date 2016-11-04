package com.lh.imbilibili.view.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.user.UserResponse;
import com.lh.imbilibili.utils.DrawableTintUtils;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/7.
 * 登陆界面
 */

public class LoginActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    @BindView(R.id.iv_22)
    ImageView mIv22;
    @BindView(R.id.iv_33)
    ImageView mIv33;
    @BindView(R.id.username)
    EditText mEdUserName;
    @BindView(R.id.password)
    EditText mEdPassword;
    @BindView(R.id.login)
    TextView mBtnLogin;
    private Subscription mLoginSub;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        StatusBarUtils.setSimpleToolbarLayout(this, mToolbar);
        initView();
    }

    private void initView() {
        mToolbar.setTitle("登陆");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEdUserName.setOnFocusChangeListener(this);
        mEdPassword.setOnFocusChangeListener(this);
        mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom != oldBottom) {
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.scrollTo(0, mScrollView.getChildAt(0).getMeasuredHeight());
                        }
                    });
                }
            }
        });
        mBtnLogin.setOnClickListener(this);
    }

    private void login(final String username, String password) {
        mLoginSub = RetrofitHelper.getInstance()
                .getLoginService()
                .doLogin(password, "jsonp", username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserResponse>() {
                    @Override
                    public void call(UserResponse userResponse) {
                        if (!TextUtils.isEmpty(userResponse.getAccess_key())) {
                            UserManagerUtils.getInstance().saveUserInfo(getApplicationContext(), userResponse);
                            UserManagerUtils.getInstance().readUserInfo(getApplicationContext());
                            RxBus.getInstance().send(userResponse);
                            finish();
                        } else {
                            ToastUtils.showToastShort(userResponse.getCode() + "");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToastShort("网络异常");
                    }
                });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.password) {
            if (hasFocus) {
                mIv22.setImageResource(R.drawable.ic_22_hide);
                mIv33.setImageResource(R.drawable.ic_33_hide);
                DrawableTintUtils.tintDrawable(getApplicationContext(), mEdPassword.getCompoundDrawables()[0], R.color.colorAccent);
            } else {
                mIv22.setImageResource(R.drawable.ic_22);
                mIv33.setImageResource(R.drawable.ic_33);
                DrawableTintUtils.tintDrawable(getApplicationContext(), mEdPassword.getCompoundDrawables()[0], R.color.gray_dark);
            }
        } else if (v.getId() == R.id.username) {
            if (hasFocus) {
                DrawableTintUtils.tintDrawable(getApplicationContext(), mEdUserName.getCompoundDrawables()[0], R.color.colorAccent);
            } else {
                DrawableTintUtils.tintDrawable(getApplicationContext(), mEdUserName.getCompoundDrawables()[0], R.color.gray_dark);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            login(mEdUserName.getText().toString(), mEdPassword.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.unsubscribe(mLoginSub);
    }
}
