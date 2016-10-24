package com.lh.imbilibili.view.adapter.bangumi;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.model.home.Banner;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.widget.BannerView;
import com.lh.imbilibili.widget.ScalableImageView;

import java.util.List;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BannerAdapter extends BannerView.Adaper {

    private List<Banner> mBanners;

    @Override
    public int getBannerCount() {
        if (mBanners == null) {
            return 0;
        } else {
            return mBanners.size();
        }
    }

    public void setData(List<Banner> banners) {
        mBanners = banners;
    }

    @Override
    public Object getItemView(final ViewGroup container, final int position) {
        ScalableImageView imageView = new ScalableImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setWidthRatio(960);
        imageView.setHeightRatio(300);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        Glide.with(container.getContext()).load(mBanners.get(position).getImg()).into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = mBanners.get(position).getLink();
                if (link.contains("anime")) {
                    String[] temp = link.split("anime/");
                    BangumiDetailActivity.startActivity(container.getContext(), temp[temp.length - 1]);
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

    public void setmBanners(List<Banner> mBanners) {
        this.mBanners = mBanners;
    }
}
