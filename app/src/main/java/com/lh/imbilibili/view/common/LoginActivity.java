package com.lh.imbilibili.view.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.helper.LoginHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.RsaData;
import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.utils.BiliBilliSignUtils;
import com.lh.imbilibili.utils.DrawableTintUtils;
import com.lh.imbilibili.utils.RsaHelper;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.BaseActivity;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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

    private void login(final String username, final String password) {
        mLoginSub = LoginHelper.getInstance()
                .getLoginService()
                .getRsaKey(System.currentTimeMillis())
                .flatMap(new Func1<BilibiliDataResponse<RsaData>, Observable<BilibiliDataResponse<User>>>() {
                    @Override
                    public Observable<BilibiliDataResponse<User>> call(BilibiliDataResponse<RsaData> rsaDataBilibiliDataResponse) {
                        RsaData rsaData = rsaDataBilibiliDataResponse.getData();
                        String temp = rsaData.getHash() + password;
                        try {
                            String psw = RsaHelper.encryptByPublicKey(temp.getBytes(), RsaHelper.getPublicKey(rsaData.getKey()));
                            LinkedHashMap<String, String> map = new LinkedHashMap<>();
                            map.put("appkey", Constant.APPKEY);
                            map.put("build", "428001");
                            map.put("mobi_app", Constant.MOBI_APP);
                            map.put("password", psw);
                            map.put("ts", System.currentTimeMillis() + "");
                            map.put("username", username);
                            StringBuilder bodyBuilder = new StringBuilder();
                            boolean isFirst = true;
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                if (isFirst) {
                                    isFirst = false;
                                    bodyBuilder.append(entry.getKey())
                                            .append("=")
                                            .append(entry.getValue());
                                } else {
                                    if (entry.getKey().equals("password")) {
                                        System.out.println(entry.getKey());
                                        bodyBuilder.append("&")
                                                .append(entry.getKey())
                                                .append("=")
                                                .append(URLEncoder.encode(entry.getValue(), "utf-8"));
                                    } else {
                                        bodyBuilder.append("&")
                                                .append(entry.getKey())
                                                .append("=")
                                                .append(entry.getValue());
                                    }
                                }
                            }
                            String sign = BiliBilliSignUtils.getSign(bodyBuilder.toString(), Constant.SECRETKEY);
                            map.put("sign", sign);
                            return LoginHelper.getInstance().getLoginService().doLogin(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return Observable.error(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BilibiliDataResponse<User>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort("失败");
                    }

                    @Override
                    public void onNext(BilibiliDataResponse<User> userBilibiliDataResponse) {
                        if (userBilibiliDataResponse.isSuccess()) {
                            ToastUtils.showToastShort("登陆成功");
                            UserManagerUtils.getInstance().saveUserInfo(getApplicationContext(), userBilibiliDataResponse.getData());
                            UserManagerUtils.getInstance().readUserInfo(getApplicationContext());
                            RxBus.getInstance().send(userBilibiliDataResponse.getData());
                            finish();
                        }
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
