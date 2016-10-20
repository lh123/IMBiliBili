package com.lh.imbilibili.view.adapter.categoryfragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.PartionHome;
import com.lh.imbilibili.model.PartionVideo;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.view.adapter.categoryfragment.model.PartionModel;
import com.lh.imbilibili.widget.BannerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/9/29.
 * 分区首页
 */

public class PartionHomeRecyclerViewAdapter extends RecyclerView.Adapter {

    public static final int TYPE_BANNER = 1;
    public static final int TYPE_SUB_PARTION = 2;
    public static final int TYPE_HOT_RECOMMEND_HEAD = 3;
    public static final int TYPE_HOT_RECOMMEND_ITEM = 4;
    public static final int TYPE_NEW_VIDEO_HEAD = 5;
    public static final int TYPE_NEW_VIDEO_ITEM = 6;
    public static final int TYPE_PARTION_DYNAMIC_HEAD = 7;
    public static final int TYPE_PARTION_DYNAMIC_ITME = 8;

    private Context mContext;

    private PartionHome mPartionData;
    private PartionModel mPartionModel;
    private List<PartionVideo> mDynamicVideo;

    private OnItemClickListener mOnItemClickListener;

    private List<Integer> mTypeList;

    public PartionHomeRecyclerViewAdapter(Context context, PartionModel partionModel) {
        mContext = context;
        mPartionModel = partionModel;
        mTypeList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addDynamicVideo(List<PartionVideo> data) {
        if (mDynamicVideo == null) {
            mDynamicVideo = data;
        } else {
            mDynamicVideo.addAll(data);
        }
    }

    public void setPartionData(PartionHome partionData) {
        this.mPartionData = partionData;
    }

    public void clearDynamicVideo() {
        if (mDynamicVideo != null) {
            mDynamicVideo.clear();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_BANNER:
                viewHolder = new BannerHolder(inflater.inflate(R.layout.banner_item, parent, false));
                break;
            case TYPE_SUB_PARTION:
                viewHolder = new SubPartionHolder(inflater.inflate(R.layout.sub_partion_recyclerview_item, parent, false));
                break;
            case TYPE_HOT_RECOMMEND_HEAD:
                viewHolder = new HeadHolder(inflater.inflate(R.layout.common_head_item, parent, false));
                break;
            case TYPE_HOT_RECOMMEND_ITEM:
                viewHolder = new VideoHolder(inflater.inflate(R.layout.video_grid_card_item, parent, false));
                break;
            case TYPE_NEW_VIDEO_HEAD:
                viewHolder = new HeadHolder(inflater.inflate(R.layout.common_head_item, parent, false));
                break;
            case TYPE_NEW_VIDEO_ITEM:
                viewHolder = new VideoHolder(inflater.inflate(R.layout.video_grid_card_item, parent, false));
                break;
            case TYPE_PARTION_DYNAMIC_HEAD:
                viewHolder = new HeadHolder(inflater.inflate(R.layout.common_head_item, parent, false));
                break;
            case TYPE_PARTION_DYNAMIC_ITME:
                viewHolder = new VideoHolder(inflater.inflate(R.layout.video_grid_card_item, parent, false));
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HOT_RECOMMEND_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("热门推荐");
            headHolder.setLeftDrawable(R.drawable.ic_header_hot);
            headHolder.tvSubTitle.setVisibility(View.GONE);
        } else if (type == TYPE_HOT_RECOMMEND_ITEM) {
            int realPosition = position - mTypeList.indexOf(TYPE_HOT_RECOMMEND_ITEM);
            PartionVideo video = mPartionData.getRecommend().get(realPosition);
            VideoHolder videoHolder = (VideoHolder) holder;
            Glide.with(mContext).load(video.getCover()).asBitmap().into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mAid = video.getParam();
        } else if (type == TYPE_NEW_VIDEO_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("最新视频");
            headHolder.setLeftDrawable(R.drawable.ic_header_new);
            headHolder.tvSubTitle.setVisibility(View.GONE);
        } else if (type == TYPE_NEW_VIDEO_ITEM) {
            int realPosition = position - mTypeList.indexOf(TYPE_NEW_VIDEO_ITEM);
            PartionVideo video = mPartionData.getNewVideo().get(realPosition);
            VideoHolder videoHolder = (VideoHolder) holder;
            Glide.with(mContext).load(video.getCover()).asBitmap().into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mAid = video.getParam();
        } else if (type == TYPE_PARTION_DYNAMIC_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("全区动态");
            headHolder.setLeftDrawable(R.drawable.ic_header_ding);
            headHolder.tvSubTitle.setVisibility(View.GONE);
        } else if (type == TYPE_PARTION_DYNAMIC_ITME) {
            int realPosition = position - mTypeList.indexOf(TYPE_PARTION_DYNAMIC_ITME);
            PartionVideo video = mDynamicVideo.get(realPosition);
            VideoHolder videoHolder = (VideoHolder) holder;
            Glide.with(mContext).load(video.getCover()).asBitmap().into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mAid = video.getParam();
        }
    }

    @Override
    public int getItemCount() {
        mTypeList.clear();
        if (mPartionData != null) {
            if (mPartionData.getBanner() != null) {
                mTypeList.add(TYPE_BANNER);
            }
            mTypeList.add(TYPE_SUB_PARTION);
            if (mPartionData.getRecommend() != null) {
                mTypeList.add(TYPE_HOT_RECOMMEND_HEAD);
                for (int i = 0; i < mPartionData.getRecommend().size(); i++) {
                    mTypeList.add(TYPE_HOT_RECOMMEND_ITEM);
                }
            }
            if (mPartionData.getNewVideo() != null) {
                mTypeList.add(TYPE_NEW_VIDEO_HEAD);
                for (int i = 0; i < mPartionData.getNewVideo().size(); i++) {
                    mTypeList.add(TYPE_NEW_VIDEO_ITEM);
                }
            }
        }
        if (mDynamicVideo != null) {
            mTypeList.add(TYPE_PARTION_DYNAMIC_HEAD);
            for (int i = 0; i < mDynamicVideo.size(); i++) {
                mTypeList.add(TYPE_PARTION_DYNAMIC_ITME);
            }
        }
        return mTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    private class BannerHolder extends RecyclerView.ViewHolder {

        BannerView bannerView;
        BannerAdapter adapter;

        BannerHolder(View itemView) {
            super(itemView);
            bannerView = (BannerView) itemView;
            adapter = new BannerAdapter();
            adapter.setData(mPartionData.getBanner().getTop());
            bannerView.setAdaper(adapter);

        }
    }

    class SubPartionHolder extends RecyclerView.ViewHolder implements SubPartionGridAdapter.OnItemClickListener {

        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;

        SubPartionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            SubPartionGridAdapter adapter = new SubPartionGridAdapter(mPartionModel.getPartions());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 4);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
            adapter.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(int position) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onSubPartionItemClick(position);
            }
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.sub_title)
        TextView tvSubTitle;

        HeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setLeftDrawable(int resId) {
            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), resId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(drawable, null, null, null);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onHeadItemClick(getItemViewType());
            }
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.info_views)
        TextView mTvInfoViews;
        @BindView(R.id.info_danmakus)
        TextView mTvInfoDanmakus;

        String mAid;

        VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int tintColor = ContextCompat.getColor(mContext, R.color.gray_dark);
            Drawable drawableCompat = DrawableCompat.wrap(mTvInfoViews.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            drawableCompat = DrawableCompat.wrap(mTvInfoDanmakus.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onVideoItemClick(mAid);
            }
        }
    }

    public interface OnItemClickListener {
        void onVideoItemClick(String aid);

        void onSubPartionItemClick(int position);

        void onHeadItemClick(int type);
    }
}
