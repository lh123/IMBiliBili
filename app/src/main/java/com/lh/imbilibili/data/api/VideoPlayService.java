package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.video.VideoDetail;
import com.lh.imbilibili.model.video.VideoPlayData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface VideoPlayService {

    @GET(Constant.INTERFACE_URL + Constant.PLAY_URL)
    Observable<VideoPlayData> getPlayData(@Query("_appver") String build,
                                          @Query("_device") String device,
                                          @Query("_aid") String aid,
                                          @Query("_tid") int tid,
                                          @Query("_p") int p,
                                          @Query("_down") int down,
                                          @Query("cid") String cid,
                                          @Query("quality") int quality,
                                          @Query("otype") String otype);

    @GET(Constant.APP_URLS + Constant.VIDEO_DETAIL)
    Observable<BilibiliDataResponse<VideoDetail>> getVideoDetail(@Query("aid") String aid,
                                                                 @Query("plat") int plat,
                                                                 @Query(Constant.QUERY_TS) long ts);


}
