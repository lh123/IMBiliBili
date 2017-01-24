package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.attention.DynamicVideo;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.model.attention.FollowBangumiResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/14.
 * 关注Api
 */

public interface AttentionService {

    @GET(Constant.MY_FOLLOWS)
    Observable<FollowBangumiResponse<List<FollowBangumi>>> getFollowBangumi(@Query("mid") int mid,
                                                                            @Query("ts") long ts);


    @GET(Constant.API_URL + Constant.VIDEO_DYNAMIC)
    Observable<BiliBiliResponse<DynamicVideo>> getDynamicVideo(@Query("pn") int pn,
                                                                   @Query("ps") int ps,
                                                                   @Query("type") int type);

    @GET(Constant.MY_CONCERNED_SEASON)
    Observable<FollowBangumiResponse<List<FollowBangumi>>> getConcernedBangumi(@Query("page") int page,
                                                                               @Query("pagesize") int pageSize,
                                                                               @Query("ts") long ts);

    @GET(Constant.UNCONCERN_SEASON)
    Observable<BiliBiliResponse> unConcernSeason(@Query("season_id") String seasonId);

    @GET(Constant.CONCERN_SEASON)
    Observable<BiliBiliResponse> concernSeason(@Query("season_id") String seasonId);

}
