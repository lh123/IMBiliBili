package com.lh.imbilibili.view.adapter.partion;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.model.partion.PartionBanner;
import com.lh.imbilibili.widget.BannerView;
import com.lh.imbilibili.widget.ScalableImageView;

import java.util.List;

/**
 * Created by liuhui on 2016/9/29.
 */

public class BannerAdapter extends BannerView.Adaper {
    private List<PartionBanner> mBanners;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public int getBannerCount() {
        if (mBanners == null) {
            return 0;
        } else {
            return mBanners.size();
        }
    }

    public void setData(List<PartionBanner> banners) {
        mBanners = banners;
    }

    @Override
    public Object getItemView(final ViewGroup container, final int position) {
        ScalableImageView imageView = new ScalableImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setWidthRatio(960);
//        imageView.setHeightRatio(300);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        Glide.with(container.getContext()).load(mBanners.get(position).getImage()).into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setmBanners(List<PartionBanner> mBanners) {
        this.mBanners = mBanners;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
