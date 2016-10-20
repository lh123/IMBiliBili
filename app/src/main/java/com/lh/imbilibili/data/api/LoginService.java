package com.lh.imbilibili.data.api;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.user.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liuhui on 2016/7/5.
 * biliapi
 */
public interface LoginService {

    @GET(Constant.ACCOUNT_URL + Constant.LOGIN)
    Call<UserResponse> doLogin(@Query("pwd") String password,
                               @Query("type") String type,
                               @Query("userid") String userid);
}

