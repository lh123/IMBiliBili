package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.partion.PartionHome;
import com.lh.imbilibili.model.partion.PartionVideo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 * 分区Api
 */

public interface PartionService {
    @GET(Constant.APP_URL + Constant.PARTION_INFO)
    Observable<BiliBiliResponse<PartionHome>> getPartionInfo(@Query("rid") int rid,
                                                             @Query("channel") String channel);

    @GET(Constant.APP_URL + Constant.PARTION_DYNAMIC)
    Observable<BiliBiliResponse<List<PartionVideo>>> getPartionDynamic(@Query("rid") int rid,
                                                                           @Query("pn") int pn,
                                                                           @Query("ps") int ps);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD)
    Observable<BiliBiliResponse<PartionHome>> getPartionChild(@Query("rid") int rid,
                                                                  @Query("channel") String channel);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD_LIST)
    Observable<BiliBiliResponse<List<PartionVideo>>> getPartionChildList(@Query("rid") int rid,
                                                                             @Query("pn") int pn,
                                                                             @Query("ps") int ps,
                                                                             @Query("order") String order);
}
