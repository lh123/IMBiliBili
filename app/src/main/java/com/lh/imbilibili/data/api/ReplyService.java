package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.FeedbackData;
import com.lh.imbilibili.model.ReplyCount;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/10/8.
 * 回复Api
 */

public interface ReplyService {
    @GET(Constant.API_URL + Constant.FEEDBACK)
    Observable<BilibiliDataResponse<FeedbackData>> getFeedback(@Query("nohot") int nohot,
                                                               @Query("oid") String oid,
                                                               @Query("pn") int pn,
                                                               @Query("ps") int ps,
                                                               @Query("sort") int sort,
                                                               @Query(Constant.QUERY_TYPE) int type);


    @GET(Constant.API_URL + Constant.REPLY_COUNT)
    Observable<BilibiliDataResponse<ReplyCount>> getReplyCount(@Query("oid") String oid,
                                                               @Query(Constant.QUERY_TYPE) int type);


}
