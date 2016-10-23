package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.user.UserResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuhui on 2016/7/5.
 * 登陆api
 */
public interface LoginService {

    @GET(Constant.ACCOUNT_URL + Constant.LOGIN)
    Observable<UserResponse> doLogin(@Query("pwd") String password,
                                     @Query("type") String type,
                                     @Query("userid") String userid);
}

