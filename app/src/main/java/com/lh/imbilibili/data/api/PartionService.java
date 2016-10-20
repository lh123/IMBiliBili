package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface PartionService {
    @GET(Constant.APP_URL + Constant.PARTION_INFO)
    Call<BilibiliDataResponse<PartionHome>> getPartionInfo(@Query("rid") int rid,
                                                           @Query("channel") String channel);

    @GET(Constant.APP_URL + Constant.PARTION_DYNAMIC)
    Call<BilibiliDataResponse<List<PartionVideo>>> getPartionDynamic(@Query("rid") int rid,
                                                                     @Query("pn") int pn,
                                                                     @Query("ps") int ps);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD)
    Call<BilibiliDataResponse<PartionHome>> getPartionChild(@Query("rid") int rid,
                                                            @Query("channel") String channel);

    @GET(Constant.APP_URL + Constant.PARTION_CHILD_LIST)
    Call<BilibiliDataResponse<List<PartionVideo>>> getPartionChildList(@Query("rid") int rid,
                                                                       @Query("pn") int pn,
                                                                       @Query("ps") int ps,
                                                                       @Query("order") String order);
}
