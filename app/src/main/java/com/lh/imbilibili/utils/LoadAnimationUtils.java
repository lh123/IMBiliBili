package com.lh.imbilibili.utils;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by liuhui on 2016/10/7.
 */

public class LoadAnimationUtils {

    public static void startLoadAnimate(ImageView imageView, @DrawableRes int resId) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resId);
        if (imageView.getDrawable() instanceof AnimationDrawable) {
            AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
            drawable.setOneShot(false);
            drawable.start();
        }
    }

    /**
     * @param imageView 显示动画的View
     * @param resId     0不显示
     */
    public static void stopLoadAnimate(ImageView imageView, @DrawableRes int resId) {
        if (imageView.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) imageView.getDrawable()).stop();
        }
        if (resId != 0) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(resId);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}
