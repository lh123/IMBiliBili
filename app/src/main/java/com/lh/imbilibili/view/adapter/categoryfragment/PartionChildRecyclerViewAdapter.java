package com.lh.imbilibili.view.adapter.categoryfragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/1.
 */

public class PartionChildRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HOT_HEAD = 1;
    private static final int TYPE_HOT_ITEM = 2;
    private static final int TYPE_NEW_HEAD = 3;
    private static final int TYPE_NEW_ITEM = 4;

    private PartionHome mPartionHomeData;
    private List<PartionVideo> mNewVideos;

    private OnVideoItemClickListener mOnVideoItemClickListener;

    private Context mContext;

    private List<Integer> mTypeList;

    public PartionChildRecyclerViewAdapter(Context context) {
        mContext = context;
        mTypeList = new ArrayList<>();
    }

    public void setOnVideoItemClickListener(OnVideoItemClickListener listener) {
        mOnVideoItemClickListener = listener;
    }

    public void addNewVideos(List<PartionVideo> newVideos) {
        if (newVideos == null) {
            return;
        }
        if (mNewVideos == null) {
            mNewVideos = newVideos;
        } else {
            mNewVideos.addAll(newVideos);
        }
    }

    public void setPartionHomeData(PartionHome partionHomeData) {
        mPartionHomeData = partionHomeData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HOT_HEAD:
            case TYPE_NEW_HEAD:
                viewHolder = new HeadHolder(new TextView(parent.getContext()));
                break;
            case TYPE_HOT_ITEM:
            case TYPE_NEW_ITEM:
                viewHolder = new VideoHolder(inflater.inflate(R.layout.video_list_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HOT_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.mHeadName.setText("最热视频");
        } else if (type == TYPE_HOT_ITEM) {
            VideoHolder videoHolder = (VideoHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_HOT_ITEM);
            PartionVideo video = mPartionHomeData.getRecommend().get(realPosition);
            Glide.with(mContext).load(video.getCover()).asBitmap().into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvAuthor.setText(video.getName());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mTvPayBadge.setVisibility(View.GONE);
            videoHolder.mAid = video.getParam();
        } else if (type == TYPE_NEW_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.mHeadName.setText("最新视频");
        } else if (type == TYPE_NEW_ITEM) {
            VideoHolder videoHolder = (VideoHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_NEW_ITEM);
            PartionVideo video = mNewVideos.get(realPosition);
            Glide.with(mContext).load(video.getCover()).asBitmap().into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvAuthor.setText(video.getName());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mTvPayBadge.setVisibility(View.GONE);
            videoHolder.mAid = video.getParam();
        }
    }

    @Override
    public int getItemCount() {
        mTypeList.clear();
        if (mPartionHomeData != null && mPartionHomeData.getRecommend() != null && mPartionHomeData.getRecommend().size() > 0) {
            mTypeList.add(TYPE_HOT_HEAD);
            for (int i = 0; i < mPartionHomeData.getRecommend().size(); i++) {
                mTypeList.add(TYPE_HOT_ITEM);
            }
        }
        if (mNewVideos != null && mNewVideos.size() > 0) {
            mTypeList.add(TYPE_NEW_HEAD);
            for (int i = 0; i < mNewVideos.size(); i++) {
                mTypeList.add(TYPE_NEW_ITEM);
            }
        }
        return mTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    private class HeadHolder extends RecyclerView.ViewHolder {
        private TextView mHeadName;

        HeadHolder(View itemView) {
            super(itemView);
            mHeadName = (TextView) itemView;
        }
    }

    @SuppressWarnings("WeakerAccess")
    class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.pay_badge)
        TextView mTvPayBadge;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.author)
        TextView mTvAuthor;
        @BindView(R.id.info_views)
        TextView mTvInfoViews;
        @BindView(R.id.info_danmakus)
        TextView mTvInfoDanmakus;
        @BindView(R.id.text2)
        TextView mTvSecond;

        private String mAid;

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
            if (mOnVideoItemClickListener != null) {
                mOnVideoItemClickListener.onVideoClick(mAid);
            }
        }
    }

    public interface OnVideoItemClickListener {
        void onVideoClick(String aid);
    }
}
