package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.partion.PartionHome;
import com.lh.imbilibili.model.partion.PartionVideo;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/10/8.
 * 分区Api
 */

public interface PartionService {
    @GET(Constant.APP_URL + Constant.PARTION_INFO)
    Observable<BilibiliDataResponse<PartionHome>> getPartionInfo(@Query("rid") int rid,
                                                                 @Query("channel") String channel);

    @GET(Constant.APP_URL + Constant.PARTION_DYNAMIC)
    Observable<BilibiliDataResponse<List<PartionVideo>>> getPartionDynamic(@Query("rid") int rid,
                                                                           @Query("pn") int pn,
                                                                           @Query("ps") int ps);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD)
    Observable<BilibiliDataResponse<PartionHome>> getPartionChild(@Query("rid") int rid,
                                                                  @Query("channel") String channel);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD_LIST)
    Observable<BilibiliDataResponse<List<PartionVideo>>> getPartionChildList(@Query("rid") int rid,
                                                                             @Query("pn") int pn,
                                                                             @Query("ps") int ps,
                                                                             @Query("order") String order);
}
