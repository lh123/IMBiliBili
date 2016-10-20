package com.lh.imbilibili.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by liuhui on 2016/10/8.
 */

public class DrawableTintUtils {
    public static void tintDrawable(Context context, Drawable drawable, @ColorRes int colorRes) {
        Drawable tintDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(tintDrawable, ContextCompat.getColor(context, colorRes));
    }
}
