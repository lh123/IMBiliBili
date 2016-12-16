package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.video.VideoPlayData;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface VideoPlayService {

    @GET(Constant.PLAY_URL)
    Observable<VideoPlayData> getPlayData(@Query("_aid") String aid,
                                          @Query("_tid") int tid,
                                          @Query("_p") int p,
                                          @Query("_down") int down,
                                          @Query("cid") String cid,
                                          @Query("quality") int quality,
                                          @Query("otype") String otype);

    @GET(Constant.PLAY_URL)
    @Headers({"User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36"})
    Observable<Response<ResponseBody>> getPlayData(@Query("cid") String cid,
                                                   @Query("player") int player,
                                                   @Query("quality") int quality,
                                                   @Query("ts") long ts);
}
