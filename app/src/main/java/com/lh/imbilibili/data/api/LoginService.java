package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.user.RsaData;
import com.lh.imbilibili.model.user.User;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/7/5.
 * 登陆api
 */
public interface LoginService {

    @POST(Constant.GET_KEY)
    Observable<BilibiliDataResponse<RsaData>> getRsaKey(@Query(Constant.QUERY_TS) long ts);

    @POST(Constant.LOGIN)
    @FormUrlEncoded
    Observable<BilibiliDataResponse<User>> doLogin(@FieldMap Map<String, String> body);
}

