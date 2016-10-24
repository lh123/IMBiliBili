package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.home.Splash;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 * 闪屏Api
 */

public interface SplashService {

    @GET(Constant.SPLASH_URL)
    Call<BilibiliDataResponse<Splash>> getSplash(@Query("plat") String plat,
                                                 @Query("build") String build,
                                                 @Query("channel") String channel,
                                                 @Query("width") String width,
                                                 @Query("height") String height);
}
