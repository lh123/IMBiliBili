package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.model.user.UserDetailInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 * 用户中心Api
 */

public interface UserService {

    @GET(Constant.ACCOUNT_URL + Constant.MY_INFO)
    Observable<UserDetailInfo> getUserDetailInfo();

    @GET(Constant.APP_URL + Constant.USER_SPACE)
    Observable<BiliBiliResponse<UserCenter>> getUserSpaceInfo(@Query("ps") int ps,
                                                              @Query("ts") long ts,
                                                              @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_ARCHIVE)
    Observable<BiliBiliResponse<UserCenter.CenterList<UserCenter.Archive>>> getUserArchive(@Query("pn") int pn,
                                                                                               @Query("ps") int ps,
                                                                                               @Query("ts") long ts,
                                                                                               @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_COIN_ARCHIVE)
    Observable<BiliBiliResponse<UserCenter.CenterList<UserCenter.Archive>>> getUserCoinArchive(@Query("pn") int pn,
                                                                                                   @Query("ps") int ps,
                                                                                                   @Query("ts") long ts,
                                                                                                   @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_BANGUMI)
    Observable<BiliBiliResponse<UserCenter.CenterList<UserCenter.Season>>> getUserBangumi(@Query("pn") int pn,
                                                                                              @Query("ps") int ps,
                                                                                              @Query("ts") long ts,
                                                                                              @Query("vmid") int mid);

    @GET(Constant.APP_URL + Constant.USER_SPACE_COMMUNITY)
    Observable<BiliBiliResponse<UserCenter.CenterList<UserCenter.Community>>> getUserCommunity(@Query("pn") int pn,
                                                                                                   @Query("ps") int ps,
                                                                                                   @Query("ts") long ts,
                                                                                                   @Query("vmid") int mid);

//    @GET(Constant.APP_URL + Constant.USER_SPACE_GAME)
//    Observable<BiliBiliResponse<UserCenter.CenterList<UserCenter.Game>>> getUserGame(@Query("pn") int pn,
//                                                                                         @Query("ps") int ps,
//                                                                                         @Query("ts") long ts,
//                                                                                         @Query("vmid") int mid);
}
