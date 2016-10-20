package com.lh.imbilibili.view.adapter.usercenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/17.
 */

public class FollowBangumiRecyclerViewAdapter extends RecyclerView.Adapter<FollowBangumiRecyclerViewAdapter.BangumiViewHolder> {

    private List<UserCenter.Season> mSeasons;

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public FollowBangumiRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addSeasons(List<UserCenter.Season> seasons) {
        if (mSeasons == null) {
            mSeasons = seasons;
        } else {
            mSeasons.addAll(seasons);
        }
    }

    @Override
    public BangumiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BangumiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_bangumi_mini_card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BangumiViewHolder holder, int position) {
        UserCenter.Season season = mSeasons.get(position);
        Glide.with(mContext)
                .load(season.getCover())
                .centerCrop()
                .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), DisplayUtils.dip2px(mContext.getApplicationContext(), 2)))
                .into(holder.mIvCover);
        holder.mTvTitle.setText(season.getTitle());
        holder.mSeasonId = season.getParam();
        if (season.getNewestEpIndex().equals(season.getTotalCount())) {
            holder.mTvText1.setText(StringUtils.format("%s话全", season.getTotalCount()));
        } else {
            holder.mTvText1.setText(StringUtils.format("更新至%s话", season.getNewestEpIndex()));
        }
    }

    @Override
    public int getItemCount() {
        if (mSeasons == null) {
            return 0;
        }
        return mSeasons.size();
    }

    class BangumiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.text1)
        TextView mTvText1;

        private String mSeasonId;

        public BangumiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mSeasonId);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String seasonId);
    }
}
