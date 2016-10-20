package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.model.user.UserDetailInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface UserService {

    @GET(Constant.ACCOUNT_URL + Constant.MY_INFO)
    Call<UserDetailInfo> getUserDetailInfo();

    @GET(Constant.APP_URL + Constant.USER_SPACE)
    Call<BilibiliDataResponse<UserCenter>> getUserSpaceInfo(@Query("ps") int ps,
                                                            @Query("ts") long ts,
                                                            @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_ARCHIVE)
    Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> getUserArchive(@Query("pn") int pn,
                                                                                         @Query("ps") int ps,
                                                                                         @Query("ts") long ts,
                                                                                         @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_COIN_ARCHIVE)
    Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> getUserCoinArchive(@Query("pn") int pn,
                                                                                             @Query("ps") int ps,
                                                                                             @Query("ts") long ts,
                                                                                             @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_BANGUMI)
    Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>> getUserBangumi(@Query("pn") int pn,
                                                                                        @Query("ps") int ps,
                                                                                        @Query("ts") long ts,
                                                                                        @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_COMMUNITY)
    Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>> getUserCommunity(@Query("pn") int pn,
                                                                                             @Query("ps") int ps,
                                                                                             @Query("ts") long ts,
                                                                                             @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_GAME)
    Call<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Game>>> getUserGame(@Query("pn") int pn,
                                                                                   @Query("ps") int ps,
                                                                                   @Query("ts") long ts,
                                                                                   @Query("vmid") int mid);
}
