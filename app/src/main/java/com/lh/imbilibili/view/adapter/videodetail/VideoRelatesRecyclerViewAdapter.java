package com.lh.imbilibili.view.adapter.videodetail;

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
import com.lh.imbilibili.model.video.VideoDetail;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/2.
 */

public class VideoRelatesRecyclerViewAdapter extends RecyclerView.Adapter<VideoRelatesRecyclerViewAdapter.VideoHolder> {

    private List<VideoDetail> mVideoDetails;
    private Context mContext;
    private OnVideoItemClickListener mOnVideoItemClickListener;

    public VideoRelatesRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setVideoDetails(List<VideoDetail> videoDetails) {
        mVideoDetails = videoDetails;
    }

    public void setOnVideoItemClickListener(OnVideoItemClickListener listener) {
        mOnVideoItemClickListener = listener;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        VideoDetail video = mVideoDetails.get(position);
        Glide.with(mContext).load(video.getPic()).centerCrop().into(holder.mIvCover);
        holder.mTvTitle.setText(video.getTitle());
        holder.mTvAuthor.setText(video.getOwner().getName());
        holder.mTvInfoViews.setText(StringUtils.formateNumber(video.getStat().getView()));
        holder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getStat().getDanmaku()));
        holder.mTvPayBadge.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (mVideoDetails == null) {
            return 0;
        }
        return mVideoDetails.size();
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
                mOnVideoItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnVideoItemClickListener {
        void onItemClick(int position);
    }
}
