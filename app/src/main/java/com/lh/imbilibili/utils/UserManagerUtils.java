package com.lh.imbilibili.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.model.user.UserDetailInfo;

/**
 * Created by liuhui on 2016/10/8.
 */

public class UserManagerUtils {
    private static UserManagerUtils mUserManager;
    private User mUser;
    private UserDetailInfo mUserDetailInfo;

    private UserManagerUtils() {
    }

    public static UserManagerUtils getInstance() {
        if (mUserManager == null) {
            synchronized (UserManagerUtils.class) {
                if (mUserManager == null) {
                    mUserManager = new UserManagerUtils();
                }
            }
        }
        return mUserManager;
    }

    public void saveUserInfo(Context context, User user) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("accessToken", user.getAccessToken());
        editor.putString("refreshToken", user.getRefreshToken());
        editor.putInt("mid", user.getMid());
        editor.putLong("expires", user.getExpiresIn());
        editor.apply();
    }

    public void readUserInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPreferences.contains("accessToken")) {
            return;
        }
        if (mUser == null) {
            mUser = new User();
        }
        mUser.setMid(sharedPreferences.getInt("mid", 0));
        mUser.setAccessToken(sharedPreferences.getString("accessToken", ""));
        mUser.setRefreshToken(sharedPreferences.getString("refreshToken", ""));
        mUser.setExpiresIn(sharedPreferences.getLong("expires", 0));
    }

    public User getCurrentUser() {
        return mUser;
    }

    public void setUserDetailInfo(UserDetailInfo info) {
        mUserDetailInfo = info;
    }

    public UserDetailInfo getUserDetailInfo() {
        return mUserDetailInfo;
    }
}
