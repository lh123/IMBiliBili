package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.history.History;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 * 历史Api
 */

public interface HistoryService {
    @POST(Constant.API_URL + Constant.HISTORY_ADD)
    @FormUrlEncoded
    @Headers({"Content-Type: application/x-www-form-urlencoded; charset=UTF-8"})
    Call<BilibiliDataResponse> addHistory(@Field("aid") String aid);

    @GET(Constant.REPORT_WATCH)
    Call<BilibiliDataResponse> reportWatch(@Query("cid") String cid,
                                           @Query("episode_id") String eposideId,
                                           @Query("ts") long ts);

    @GET(Constant.API_URL + Constant.HISTORY)
    Call<BilibiliDataResponse<List<History>>> getHistory(@Query("pn") int pn,
                                                         @Query("ps") int ps);
}
