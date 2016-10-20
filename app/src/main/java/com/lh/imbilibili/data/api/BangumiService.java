package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.model.BangumiIndex;
import com.lh.imbilibili.model.BangumiIndexCond;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.IndexBangumiRecommend;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.model.SeasonGroup;
import com.lh.imbilibili.model.SeasonRecommend;
import com.lh.imbilibili.model.SourceData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/10/8.
 */

public interface BangumiService {


    @GET(Constant.BANGUMI_RECOMMEND)
    Call<BiliBiliResultResponse<List<IndexBangumiRecommend>>> getBangumiRecommend(@Query("cursor") String cursor,
                                                                                  @Query("pagesize") int pagesize,
                                                                                  @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.INDEX_PAGE)
    Call<BiliBiliResultResponse<IndexPage>> getIndexPage(@Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_DETAIL)
    Call<BiliBiliResultResponse<BangumiDetail>> getBangumiDetail(@Query(Constant.QUERY_SEASON_ID) String seasonId,
                                                                 @Query(Constant.QUERY_TS) long ts,
                                                                 @Query(Constant.QUERY_TYPE) String type);

    @GET(Constant.SEASON_RECOMMEND)
    Call<BiliBiliResultResponse<SeasonRecommend>> getSeasonRecommend(@Path(Constant.QUERY_SEASON_ID) String seasonId,
                                                                     @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.GET_SOURCES)
    Call<BiliBiliResultResponse<List<SourceData>>> getSource(@Query(Constant.QUERY_EPISODE_ID) String episodeId,
                                                             @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.SEASON_GROUP)
    Call<BiliBiliResultResponse<List<SeasonGroup>>> getSeasonGroup(@Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_INDEX)
    Call<BiliBiliResultResponse<BangumiIndex>> getBangumiIndex(@Query("index_sort") int indexSort,
                                                               @Query("index_type") int indexType,
                                                               @Query("initial") String initial,
                                                               @Query("is_finish") String isFinish,
                                                               @Query("page") int page,
                                                               @Query("page_size") int pageSize,
                                                               @Query("quarter") int quarter,
                                                               @Query("start_year") int startYear,
                                                               @Query("tag_id") String tagId,
                                                               @Query(Constant.QUERY_TS) long ts,
                                                               @Query("update_period") int updatePeriod,
                                                               @Query("version") String version);

    @GET(Constant.BANGUMI_INDEX_COND)
    Call<BiliBiliResultResponse<BangumiIndexCond>> getBangumiIndexCond(@Query(Constant.QUERY_TS) long ts,
                                                                       @Query(Constant.QUERY_TYPE) int type);
}
