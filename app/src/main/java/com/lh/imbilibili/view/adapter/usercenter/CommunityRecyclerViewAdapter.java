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

public class CommunityRecyclerViewAdapter extends RecyclerView.Adapter<CommunityRecyclerViewAdapter.CommunityViewHolder> {

    private Context mContext;

    private List<UserCenter.Community> mCommunities;

    public CommunityRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void addCommunities(List<UserCenter.Community> communities) {
        if (mCommunities == null) {
            mCommunities = communities;
        } else {
            mCommunities.addAll(communities);
        }
    }

    @Override
    public CommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommunityViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_center_community_game_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CommunityViewHolder holder, int position) {
        UserCenter.Community community = mCommunities.get(position);
        Glide.with(mContext)
                .load(community.getThumb())
                .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), DisplayUtils.dip2px(mContext.getApplicationContext(), 15)))
                .into(holder.mIvCover);
        holder.mTvTitle.setText(community.getName());
        holder.mTvDesc.setText(community.getDesc());
        holder.mTvMemberCount.setText(StringUtils.format("乡民:%d", community.getMemberCount()));
        holder.mPostCount.setText(StringUtils.format("帖子:%d", community.getPostCount()));
    }

    @Override
    public int getItemCount() {
        if (mCommunities == null) {
            return 0;
        }
        return mCommunities.size();
    }

    class CommunityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.description)
        TextView mTvDesc;
        @BindView(R.id.member_count)
        TextView mTvMemberCount;
        @BindView(R.id.post_count)
        TextView mPostCount;

        public CommunityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
