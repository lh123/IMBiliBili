package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.video.VideoDetail;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/11/5.
 */

public interface VideoService {

    @GET(Constant.APP_URLS + Constant.VIDEO_DETAIL)
    Observable<BiliBiliResponse<VideoDetail>> getVideoDetail(@Query("aid") String aid,
                                                             @Query("plat") int plat,
                                                             @Query(Constant.QUERY_TS) long ts);

}
