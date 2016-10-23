package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.model.user.UserDetailInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/10/8.
 * 用户中心Api
 */

public interface UserService {

    @GET(Constant.ACCOUNT_URL + Constant.MY_INFO)
    Observable<UserDetailInfo> getUserDetailInfo();

    @GET(Constant.APP_URL + Constant.USER_SPACE)
    Observable<BilibiliDataResponse<UserCenter>> getUserSpaceInfo(@Query("ps") int ps,
                                                                  @Query("ts") long ts,
                                                                  @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_ARCHIVE)
    Observable<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> getUserArchive(@Query("pn") int pn,
                                                                                               @Query("ps") int ps,
                                                                                               @Query("ts") long ts,
                                                                                               @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_COIN_ARCHIVE)
    Observable<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Archive>>> getUserCoinArchive(@Query("pn") int pn,
                                                                                                   @Query("ps") int ps,
                                                                                                   @Query("ts") long ts,
                                                                                                   @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_BANGUMI)
    Observable<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Season>>> getUserBangumi(@Query("pn") int pn,
                                                                                              @Query("ps") int ps,
                                                                                              @Query("ts") long ts,
                                                                                              @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_COMMUNITY)
    Observable<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Community>>> getUserCommunity(@Query("pn") int pn,
                                                                                                   @Query("ps") int ps,
                                                                                                   @Query("ts") long ts,
                                                                                                   @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_GAME)
    Observable<BilibiliDataResponse<UserCenter.CenterList<UserCenter.Game>>> getUserGame(@Query("pn") int pn,
                                                                                         @Query("ps") int ps,
                                                                                         @Query("ts") long ts,
                                                                                         @Query("vmid") int mid);
}
