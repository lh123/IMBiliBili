package com.lh.imbilibili.view.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.home.Splash;
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.StorageUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.home.MainActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Created by liuhui on 2016/7/5.
 * 启动界面
 */
public class SplashActivity extends BaseActivity {
    @BindView(R.id.splash)
    ImageView ivSplash;
    @BindView(R.id.splash_icon)
    ImageView ivSplashIcon;
    @BindView(R.id.skip_layout)
    FrameLayout flSkip;
    @BindView(R.id.skip)
    TextView tvSkip;

    private Call<BilibiliDataResponse<Splash>> call;
    private Intent intent;
    private Bitmap splashBitmap;
    private Bitmap splashIcoBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initSplash();
    }

    private void initSplash() {
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int width = DisplayUtils.getWindowsWith(this);
        int height = DisplayUtils.getWindowsHeight(this);
        final File splashFile = StorageUtils.getAppFile(this, Constant.SPLASH_FILE);
        setDefaultSplash();
        IMBilibiliApplication.getApplication().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 2000);
//        if(!splashFile.exists()||splashFile.length()==0){
//            call=api.getSplash(Constant.PLAT,Constant.BUILD,Constant.CHANNEL, width +"", height +"");
//            call.enqueue(new Callback<BilibiliDataResponse<Splash>>() {
//                @Override
//                public void onResponse(Call<BilibiliDataResponse<Splash>> call, Response<BilibiliDataResponse<Splash>> response) {
//                    if(response.isSuccessful()&&response.body().getCode()==0){
//                        FileUtils.writeToFile(splashFile,new Gson().toJson(response.body()));
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<BilibiliDataResponse<Splash>> call, Throwable t) {
//
//                }
//            });
//        }
    }

    private void setDefaultSplash() {
        ivSplashIcon.setVisibility(View.VISIBLE);
        Glide.with(this).load("file:///android_asset/splash/ic_splash_default.png").into(ivSplash);
        Glide.with(this).load("file:///android_asset/splash/ic_splash_copy.png").into(ivSplashIcon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashBitmap != null && !splashBitmap.isRecycled()) {
            splashBitmap.recycle();
        }
        if (splashIcoBitmap != null && !splashIcoBitmap.isRecycled()) {
            splashIcoBitmap.recycle();
        }
    }
}
