package com.lh.imbilibili.data.api;

import com.lh.imbilibili.model.video.PlusVideoPlayerData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/11/5.
 */

public interface PlusVideoPlayService {

    @GET("https://www.bilipluxs.com/api/geturl")
    Observable<PlusVideoPlayerData> getPlayData(@Query("bangumi") int bangumi,
                                                @Query("av") String av,
                                                @Query("page") String page);

    @GET("https://www.biliplus.com/api/view")
    Observable<Object> updateInfo(@Query("id") String id,
                                  @Query("update") int update);
}
