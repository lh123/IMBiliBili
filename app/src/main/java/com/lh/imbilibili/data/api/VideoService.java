package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.VideoDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface VideoService {
    @GET(Constant.APP_URLS + Constant.VIDEO_DETAIL)
    Call<BilibiliDataResponse<VideoDetail>> getVideoDetail(@Query("aid") String aid,
                                                           @Query("plat") int plat,
                                                           @Query(Constant.QUERY_TS) long ts);

}
