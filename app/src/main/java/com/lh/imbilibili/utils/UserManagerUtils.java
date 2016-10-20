package com.lh.imbilibili.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.model.user.UserDetailInfo;
import com.lh.imbilibili.model.user.UserResponse;

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

    public void saveUserInfo(Context context, UserResponse user) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("accessKey", user.getAccess_key());
        editor.putInt("mid", user.getMid());
        editor.putLong("expires", user.getExpires());
        editor.apply();
    }

    public void readUserInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPreferences.contains("accessKey")) {
            return;
        }
        if (mUser == null) {
            mUser = new User();
        }
        mUser.setMid(sharedPreferences.getInt("mid", 0));
        mUser.setAccessKey(sharedPreferences.getString("accessKey", ""));
        mUser.setExpires(sharedPreferences.getLong("expires", 0));
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
