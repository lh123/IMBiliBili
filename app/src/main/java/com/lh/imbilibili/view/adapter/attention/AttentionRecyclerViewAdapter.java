package com.lh.imbilibili.view.adapter.attention;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.attention.DynamicVideo;
import com.lh.imbilibili.model.attention.FollowBangumi;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/10.
 */

public class AttentionRecyclerViewAdapter extends RecyclerView.Adapter {

    public static final int TYPE_BANGUMI_FOLLOW_HEAD = 1;
    public static final int TYPE_BANGUMI_FOLLOW_ITEM = 2;
    public static final int TYPE_DYNAMIC_HEAD = 3;
    public static final int TYPE_DYNAMIC_ITEM = 4;

    private Context mContext;

    private List<FollowBangumi> mFollowBangumis;
    private List<DynamicVideo.Feed> mFeeds;

    private List<Integer> mTypeList;

    private OnItemClickListener mOnItemClickListener;

    public AttentionRecyclerViewAdapter(Context context) {
        mContext = context;
        mTypeList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setFollowBangumiData(List<FollowBangumi> followBangumiData) {
        mFollowBangumis = followBangumiData;
    }

    public void addFeeds(List<DynamicVideo.Feed> feeds) {
        if (mFeeds == null) {
            mFeeds = feeds;
        } else {
            mFeeds.addAll(feeds);
        }
    }

    public void clearFeeds() {
        if (mFeeds != null) {
            mFeeds.clear();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_BANGUMI_FOLLOW_HEAD || viewType == TYPE_DYNAMIC_HEAD) {
            View headView = inflater.inflate(R.layout.common_head_item, parent, false);
            holder = new HeadHolder(headView);
        } else if (viewType == TYPE_BANGUMI_FOLLOW_ITEM) {
            View itemView = inflater.inflate(R.layout.follow_bangumi_card_item, parent, false);
            holder = new BangumiViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.follow_dynamic_video_card, parent, false);
            holder = new DynamicVideoViewHolder(itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == TYPE_BANGUMI_FOLLOW_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("番剧");
            headHolder.setLeftDrawable(R.drawable.search_result_ic_bangumi);
            headHolder.tvSubTitle.setText("更多");
            headHolder.itemView.setClickable(true);
        } else if (type == TYPE_BANGUMI_FOLLOW_ITEM) {
            BangumiViewHolder bangumiViewHolder = (BangumiViewHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_BANGUMI_FOLLOW_ITEM);
            if (realPosition < mFollowBangumis.size()) {
                bangumiViewHolder.itemView.setVisibility(View.VISIBLE);
                FollowBangumi followBangumi = mFollowBangumis.get(realPosition);
                Glide.with(mContext).load(followBangumi.getCover()).into(bangumiViewHolder.ivCover);
                bangumiViewHolder.tvTitle.setText(followBangumi.getTitle());
                bangumiViewHolder.tv1.setText(StringUtils.format("看到第%s话", followBangumi.getUserSeason().getLastEpIndex()));
                bangumiViewHolder.tv2.setText(StringUtils.format("更新至第%s话", followBangumi.getNewEp().getIndex()));
                bangumiViewHolder.mSeasonId = followBangumi.getSeasonId();
            } else {
                bangumiViewHolder.itemView.setVisibility(View.GONE);
            }
        } else if (type == TYPE_DYNAMIC_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("动态");
            headHolder.setLeftDrawable(R.drawable.ic_header_dynamic);
            headHolder.tvSubTitle.setText("");
            headHolder.tvSubTitle.setCompoundDrawables(null, null, null, null);
            headHolder.itemView.setClickable(false);
        } else {
            DynamicVideoViewHolder dynamicVideoViewHolder = (DynamicVideoViewHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_DYNAMIC_ITEM);
            DynamicVideo.Feed feed = mFeeds.get(realPosition);
            dynamicVideoViewHolder.mAId = feed.getAddition().getAid();
            if (feed.getType() == 1) {//video
                dynamicVideoViewHolder.mIvAuthor.setVisibility(View.VISIBLE);
                dynamicVideoViewHolder.mTvType.setVisibility(View.GONE);
                Glide.with(mContext).load(feed.getSource().getAvatar()).transform(new CircleTransformation(mContext.getApplicationContext())).into(dynamicVideoViewHolder.mIvAuthor);
                dynamicVideoViewHolder.mTvAuthorName.setText(feed.getSource().getUname());
                int color = ContextCompat.getColor(mContext, R.color.colorPrimary);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                String date = StringUtils.formateDateCN(feed.getCtime() * 1000);
                spannableStringBuilder.append(date);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 0, date.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append("投递了");
                dynamicVideoViewHolder.mTvTime.setText(spannableStringBuilder);
            } else {
                dynamicVideoViewHolder.mIvAuthor.setVisibility(View.GONE);
                dynamicVideoViewHolder.mTvType.setVisibility(View.VISIBLE);
                dynamicVideoViewHolder.mTvAuthorName.setText(feed.getSource().getTitle());
                int color = ContextCompat.getColor(mContext, R.color.colorPrimary);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                String date = StringUtils.formateDateCN(feed.getCtime() * 1000);
                String updateIndex = StringUtils.format("第%s话", feed.getSource().getNewEp().getIndex());
                spannableStringBuilder.append(date);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 0, date.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append("更新了");
                spannableStringBuilder.append(updateIndex);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(color), spannableStringBuilder.length() - updateIndex.length(), spannableStringBuilder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                dynamicVideoViewHolder.mTvTime.setText(spannableStringBuilder);
            }
            Glide.with(mContext).load(feed.getAddition().getPic()).centerCrop().into(dynamicVideoViewHolder.mIvCover);
            dynamicVideoViewHolder.mTvTitle.setText(feed.getAddition().getTitle());
            dynamicVideoViewHolder.mTvViews.setText(StringUtils.formateNumber(feed.getAddition().getPlay()));
            dynamicVideoViewHolder.mTvDanmakus.setText(StringUtils.formateNumber(feed.getAddition().getVideoReview()));
        }
    }

    @Override
    public int getItemCount() {
        mTypeList.clear();
        if (mFollowBangumis != null && !mFollowBangumis.isEmpty()) {
            mTypeList.add(TYPE_BANGUMI_FOLLOW_HEAD);
            mTypeList.add(TYPE_BANGUMI_FOLLOW_ITEM);
            mTypeList.add(TYPE_BANGUMI_FOLLOW_ITEM);
            mTypeList.add(TYPE_BANGUMI_FOLLOW_ITEM);
        }
        if (mFeeds != null && !mFeeds.isEmpty()) {
            mTypeList.add(TYPE_DYNAMIC_HEAD);
            for (int i = 0; i < mFeeds.size(); i++) {
                mTypeList.add(TYPE_DYNAMIC_ITEM);
            }
        }
        return mTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    class HeadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.sub_title)
        TextView tvSubTitle;

        HeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setLeftDrawable(int resId) {
            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), resId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(drawable, null, null, null);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == TYPE_BANGUMI_FOLLOW_HEAD && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(null, getItemViewType());
            }
        }
    }

    class BangumiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.new_tag)
        TextView mTagNew;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.text1)
        TextView tv1;
        @BindView(R.id.text2)
        TextView tv2;

        private String mSeasonId;

        BangumiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mSeasonId, getItemViewType());
            }
        }
    }

    class DynamicVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_avatar)
        ImageView mIvAuthor;
        @BindView(R.id.tv_type)
        TextView mTvType;
        @BindView(R.id.tv_author_name)
        TextView mTvAuthorName;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.info_views)
        TextView mTvViews;
        @BindView(R.id.info_danmakus)
        TextView mTvDanmakus;

        private int mAId;

        public DynamicVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int tintColor = ContextCompat.getColor(mContext, R.color.gray_dark);
            Drawable drawableCompat = DrawableCompat.wrap(mTvViews.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            drawableCompat = DrawableCompat.wrap(mTvDanmakus.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mAId + "", getItemViewType());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String id, int type);
    }
}
