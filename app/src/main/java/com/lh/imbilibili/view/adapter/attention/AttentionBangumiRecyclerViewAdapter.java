package com.lh.imbilibili.view.adapter.attention;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/15.
 */

public class AttentionBangumiRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<FollowBangumi> mBangumis;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public AttentionBangumiRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addBangumi(List<FollowBangumi> bangumis) {
        if (mBangumis == null) {
            mBangumis = bangumis;
        } else {
            mBangumis.addAll(bangumis);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BangumiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_bangumi_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BangumiViewHolder bangumiViewHolder = (BangumiViewHolder) holder;
        FollowBangumi bangumi = mBangumis.get(position);
        Glide.with(mContext).load(bangumi.getCover()).into(bangumiViewHolder.mIvCover);
        bangumiViewHolder.mTvTitle.setText(bangumi.getTitle());
        bangumiViewHolder.mTvWatchHistory.setText(StringUtils.format("看到第%s话", bangumi.getUserSeason().getLastEpIndex()));
        if (bangumi.getIsFinish().equals("1")) {
            bangumiViewHolder.mTvTotalCount.setText(StringUtils.format("%s话全", bangumi.getTotalCount()));
        } else {
            bangumiViewHolder.mTvTotalCount.setText(StringUtils.format("更新至第%s话", bangumi.getNewEp().getIndex()));
        }
        bangumiViewHolder.mTvFavourites.setText(StringUtils.format("%s人订阅", bangumi.getFavorites()));
    }

    @Override
    public int getItemCount() {
        if (mBangumis == null) {
            return 0;
        }
        return mBangumis.size();
    }

    @SuppressWarnings("WeakerAccess")
    public class BangumiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.watch_history)
        TextView mTvWatchHistory;
        @BindView(R.id.total_count)
        TextView mTvTotalCount;
        @BindView(R.id.favourites)
        TextView mTvFavourites;

        BangumiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mBangumis.get(getAdapterPosition()).getSeasonId());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String seasonId);
    }
}
