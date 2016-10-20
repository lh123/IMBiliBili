package com.lh.imbilibili.view.activity;

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
import android.widget.Toast;

import com.lh.imbilibili.R;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.user.UserResponse;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.utils.DrawableTintUtils;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liuhui on 2016/10/7.
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
    private Call<UserResponse> mLoginCall;

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
        mLoginCall = RetrofitHelper.getInstance().getLoginService().doLogin(password, "jsonp", username);
        mLoginCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!TextUtils.isEmpty(response.body().getAccess_key())) {
                    UserResponse userResponse = response.body();
                    UserManagerUtils.getInstance().saveUserInfo(getApplicationContext(), userResponse);
                    UserManagerUtils.getInstance().readUserInfo(getApplicationContext());
                    BusUtils.getBus().post(userResponse);
                    finish();
                } else {
                    ToastUtils.showToast(LoginActivity.this, response.body().getCode() + "", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                ToastUtils.showToast(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT);
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
}
