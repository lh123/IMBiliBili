package com.lh.imbilibili.data.api;

import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.video.PlusVideoPlayerData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/11/5.
 */

public interface PlusService {

    @GET("/api/geturl")
//    @Headers({"cookie:level=3; login=2; passlogin=1; mid=7786256; uname=lh1379; access_key=32f6853d6c799a5184efe1b832fddd28; face=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Fface%2F756f7941e3473f286da087b44533b8e2c666f813.gif; expires=1484372919; secure_header=access_key%2Cmid%2Clevel%2Csecurev2_time; securev2_time=1482056777; securev2=924f50b8; visiturl=%2Fvideo%2Fav7529879%2F"})
    Observable<PlusVideoPlayerData> getPlayData(@Query("bangumi") int bangumi,
                                                @Query("av") String av,
                                                @Query("page") String page);

    @GET("/api/view")
    Observable<Object> updateInfo(@Query("id") String id,
                                  @Query("update") int update);

    @GET("/api/bangumi")
    Observable<BiliBiliResponse<BangumiDetail>> getBangumiDetailFromPlus(@Query("season") String seasonid);

    @GET("http://biliplus.ipcjsdev.tk/api/geturl")
    Observable<PlusVideoPlayerData> getPlayDataUnBlock(@Query("bangumi") int bangumi,
                                                       @Query("av") String av,
                                                       @Query("page") String page);
}
