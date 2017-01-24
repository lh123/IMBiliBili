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
import com.lh.imbilibili.data.BilibiliResponseHandler;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.helper.LoginHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.user.RsaData;
import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.utils.BiliBilliSignUtils;
import com.lh.imbilibili.utils.DisposableUtils;
import com.lh.imbilibili.utils.DrawableTintUtils;
import com.lh.imbilibili.utils.RsaHelper;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.rxbuslibrary.RxBus;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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
    private Disposable mLoginSub;

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
                .flatMap(new Function<BiliBiliResponse<RsaData>, ObservableSource<BiliBiliResponse<User>>>() {

                    @Override
                    public ObservableSource<BiliBiliResponse<User>> apply(BiliBiliResponse<RsaData> rsaDataBiliBiliResponse) throws Exception {
                        RsaData rsaData = rsaDataBiliBiliResponse.getResult();
                        String temp = rsaData.getHash() + password;
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
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<User>, User>handlerResult())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribeWith(new DisposableSingleObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        ToastUtils.showToastShort("登陆成功");
                        UserManagerUtils.getInstance().saveUserInfo(getApplicationContext(), user);
                        UserManagerUtils.getInstance().readUserInfo(getApplicationContext());
                        RxBus.getInstance().post(user);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort("登陆失败");
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
        DisposableUtils.dispose(mLoginSub);
    }
}
