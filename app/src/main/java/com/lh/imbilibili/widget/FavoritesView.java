package com.lh.imbilibili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;

import java.util.List;

/**
 * Created by liuhui on 2016/10/16.
 * 用户收藏View
 */

public class FavoritesView extends FrameLayout {

    private ImageView mFillImage;
    private ImageView mTopImage;
    private ImageView mBottomImage;
    private LinearLayout mBottomImageLayout;
    private ImageView mLeftImage;
    private ImageView mRightImage;
    private List<UserCenter.Favourite.Video> mVideos;

    public FavoritesView(Context context) {
        super(context);
        init(context);
    }

    public FavoritesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FavoritesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.favorites_view_layout, this, false);
        mFillImage = (ImageView) layout.findViewById(R.id.image_fill);
        mTopImage = (ImageView) layout.findViewById(R.id.image_top);
        mBottomImage = (ImageView) layout.findViewById(R.id.image_bottom);
        mBottomImageLayout = (LinearLayout) layout.findViewById(R.id.image_bottom_layout);
        mLeftImage = (ImageView) layout.findViewById(R.id.image_left);
        mRightImage = (ImageView) layout.findViewById(R.id.image_right);
        addView(layout);
    }

    private void setEmptyImage() {
        mFillImage.setVisibility(VISIBLE);
        mTopImage.setVisibility(GONE);
        mBottomImage.setVisibility(GONE);
        mBottomImageLayout.setVisibility(GONE);
        Glide.with(getContext()).load(R.drawable.ic_favorite_box_default_large).asBitmap().centerCrop().into(mFillImage);
    }

    private void setOneImage() {
        mFillImage.setVisibility(VISIBLE);
        mTopImage.setVisibility(GONE);
        mBottomImage.setVisibility(GONE);
        mBottomImageLayout.setVisibility(GONE);
        Glide.with(getContext()).load(mVideos.get(0).getPic()).asBitmap().centerCrop().into(mFillImage);
    }

    private void setTwoImage() {
        mTopImage.setVisibility(VISIBLE);
        mBottomImage.setVisibility(VISIBLE);
        mFillImage.setVisibility(GONE);
        mBottomImageLayout.setVisibility(GONE);
        Glide.with(getContext()).load(mVideos.get(0).getPic()).asBitmap().centerCrop().into(mTopImage);
        Glide.with(getContext()).load(mVideos.get(1).getPic()).asBitmap().centerCrop().into(mBottomImage);
    }

    private void setThreeImage() {
        mTopImage.setVisibility(VISIBLE);
        mBottomImageLayout.setVisibility(VISIBLE);
        mBottomImage.setVisibility(GONE);
        mFillImage.setVisibility(GONE);
        Glide.with(getContext()).load(mVideos.get(0).getPic()).asBitmap().centerCrop().into(mTopImage);
        Glide.with(getContext()).load(mVideos.get(1).getPic()).asBitmap().centerCrop().into(mLeftImage);
        Glide.with(getContext()).load(mVideos.get(2).getPic()).asBitmap().centerCrop().into(mRightImage);
    }

    public void setImages(List<UserCenter.Favourite.Video> videos) {
        this.mVideos = videos;
        if (videos != null && videos.size() > 0) {
            switch (videos.size()) {
                case 1:
                    setOneImage();
                    break;
                case 2:
                    setTwoImage();
                    break;
                case 3:
                    setThreeImage();
                    break;
            }
        } else {
            setEmptyImage();
        }
    }
}
