package com.lh.imbilibili.view.adapter.usercenter;

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
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/17.
 * 投稿视频Adapter
 */

public class ArchiveRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<UserCenter.Archive> mVideos;

    private OnVideoItemClickListener mOnVideoItemClickListener;

    private Context mContext;

    public ArchiveRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnVideoItemClickListener(OnVideoItemClickListener listener) {
        mOnVideoItemClickListener = listener;
    }

    public void addVideos(List<UserCenter.Archive> videos) {
        if (mVideos == null) {
            mVideos = videos;
        } else {
            mVideos.addAll(videos);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new VideoViewHolder(inflater.inflate(R.layout.video_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
        UserCenter.Archive video = mVideos.get(position);
        Glide.with(mContext).load(video.getCover()).into(videoViewHolder.mIvCover);
        videoViewHolder.mTvTitle.setText(video.getTitle());
        videoViewHolder.mTvAuthor.setVisibility(View.GONE);
        videoViewHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
        videoViewHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
        videoViewHolder.mTvPayBadge.setVisibility(View.GONE);
        videoViewHolder.mAid = video.getParam();
    }

    @Override
    public int getItemCount() {
        if (mVideos == null) {
            return 0;
        }
        return mVideos.size();
    }

    @SuppressWarnings("WeakerAccess")
    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        private String mAid;

        VideoViewHolder(View itemView) {
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
