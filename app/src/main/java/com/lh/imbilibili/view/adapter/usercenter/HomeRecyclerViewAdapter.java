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
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;
import com.lh.imbilibili.widget.FavoritesView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/16.
 * user center home adapter
 */

@SuppressWarnings("WeakerAccess")
public class HomeRecyclerViewAdapter extends RecyclerView.Adapter {

    public static final int TYPE_ARCHIVE_HEAD = 1;
    public static final int TYPE_ARCHIVE_ITEM = 2;
    public static final int TYPE_COIN_ARCHIVE_HEAD = 3;
    public static final int TYPE_COIN_ARCHIVE_ITEM = 4;
    public static final int TYPE_FAVOURITE_HEAD = 5;
    public static final int TYPE_FAVOURITE_ITEM = 6;
    public static final int TYPE_FOLLOW_BANGUMI_HEAD = 7;
    public static final int TYPE_FOLLOW_BANGUMI_ITEM = 8;
    public static final int TYPE_COMMUNITY_HEAD = 9;
    public static final int TYPE_COMMUNITY_ITEM = 10;
    public static final int TYPE_GAME_HEAD = 11;
    public static final int TYPE_GAME_ITEM = 12;

    private UserCenter mUserCenter;

    private Context mContext;
    private List<Integer> mTypeList;
    private boolean mIsMe;

    private OnItemClickListener mOnItemClickListener;

    public HomeRecyclerViewAdapter(Context context) {
        mContext = context;
        mTypeList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setUserCenter(UserCenter userCenter) {
        mUserCenter = userCenter;
        mIsMe = UserManagerUtils.getInstance().getCurrentUser() != null && mUserCenter.getCard().getMid().equals(UserManagerUtils.getInstance().getCurrentUser().getMid() + "");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_ARCHIVE_HEAD:
            case TYPE_COIN_ARCHIVE_HEAD:
            case TYPE_FAVOURITE_HEAD:
            case TYPE_FOLLOW_BANGUMI_HEAD:
            case TYPE_COMMUNITY_HEAD:
            case TYPE_GAME_HEAD:
                viewHolder = new HeadViewHolder(inflater.inflate(R.layout.user_center_head_item, parent, false));
                break;
            case TYPE_ARCHIVE_ITEM:
            case TYPE_COIN_ARCHIVE_ITEM:
                viewHolder = new TwoVideoViewHolder(inflater.inflate(R.layout.two_video_grid_card_item, parent, false));
                break;
            case TYPE_FAVOURITE_ITEM:
                viewHolder = new FavViewHolder(new FavoritesView(parent.getContext()));
                break;
            case TYPE_FOLLOW_BANGUMI_ITEM:
                viewHolder = new BangumiViewHolder(inflater.inflate(R.layout.follow_bangumi_mini_card_item, parent, false));
                break;
            case TYPE_COMMUNITY_ITEM:
            case TYPE_GAME_ITEM:
                viewHolder = new CommunityGameViewHolder(inflater.inflate(R.layout.user_center_community_game_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == TYPE_ARCHIVE_HEAD) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.mTvTitle.setText(mIsMe ? R.string.author_space_header_my_videos : R.string.author_space_header_videos);
            headViewHolder.mTvCount.setText(StringUtils.formateNumber(mUserCenter.getArchive().getCount()));
            headViewHolder.mTvSubTitle.setText(R.string.head_title_enter);
        } else if (type == TYPE_ARCHIVE_ITEM) {
            TwoVideoViewHolder twoVideoViewHolder = (TwoVideoViewHolder) holder;
            for (int index = 0; index < 2; index++) {
                if (index < mUserCenter.getArchive().getCount()) {
                    twoVideoViewHolder.mVideos[index].itemView.setVisibility(View.VISIBLE);
                    UserCenter.Archive archive = mUserCenter.getArchive().getItem().get(index);
                    Glide.with(mContext).load(archive.getCover()).into(twoVideoViewHolder.mVideos[index].mIvCover);
                    twoVideoViewHolder.mVideos[index].mTvTitle.setText(archive.getTitle());
                    twoVideoViewHolder.mVideos[index].mTvInfoViews.setText(StringUtils.formateNumber(archive.getPlay()));
                    twoVideoViewHolder.mVideos[index].mTvInfoDanmakus.setText(StringUtils.formateNumber(archive.getDanmaku()));
                    twoVideoViewHolder.mVideos[index].mAid = archive.getParam();
                } else {
                    twoVideoViewHolder.mVideos[index].itemView.setVisibility(View.INVISIBLE);
                }
            }
        } else if (type == TYPE_COIN_ARCHIVE_HEAD) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.mTvTitle.setText(R.string.author_space_header_coins_videos);
            if (mUserCenter.getSetting().getCoinsVideo() == 0) {
                headViewHolder.mTvCount.setText(R.string.author_space_privacy_denied);
            } else {
                headViewHolder.mTvCount.setText(StringUtils.formateNumber(mUserCenter.getCoinArchive().getCount()));
                headViewHolder.mTvSubTitle.setText(R.string.head_title_enter);
            }
        } else if (type == TYPE_COIN_ARCHIVE_ITEM) {
            TwoVideoViewHolder twoVideoViewHolder = (TwoVideoViewHolder) holder;
            for (int index = 0; index < 2; index++) {
                if (index < mUserCenter.getCoinArchive().getCount()) {
                    twoVideoViewHolder.mVideos[index].itemView.setVisibility(View.VISIBLE);
                    UserCenter.Archive archive = mUserCenter.getCoinArchive().getItem().get(index);
                    Glide.with(mContext).load(archive.getCover()).into(twoVideoViewHolder.mVideos[index].mIvCover);
                    twoVideoViewHolder.mVideos[index].mTvTitle.setText(archive.getTitle());
                    twoVideoViewHolder.mVideos[index].mTvInfoViews.setText(StringUtils.formateNumber(archive.getPlay()));
                    twoVideoViewHolder.mVideos[index].mTvInfoDanmakus.setText(StringUtils.formateNumber(archive.getDanmaku()));
                    twoVideoViewHolder.mVideos[index].mAid = archive.getParam();
                } else {
                    twoVideoViewHolder.mVideos[index].itemView.setVisibility(View.INVISIBLE);
                }
            }
        } else if (type == TYPE_FAVOURITE_HEAD) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.mTvTitle.setText(mIsMe ? R.string.author_space_header_my_favorites : R.string.author_space_header_favorites);
            if (mUserCenter.getSetting().getFavVideo() == 0) {
                headViewHolder.mTvCount.setText(R.string.author_space_privacy_denied);
            } else {
                headViewHolder.mTvCount.setText(StringUtils.formateNumber(mUserCenter.getFavourite().getCount()));
                headViewHolder.mTvSubTitle.setText(R.string.head_title_enter);
            }
        } else if (type == TYPE_FAVOURITE_ITEM) {
            FavViewHolder favViewHolder = (FavViewHolder) holder;
            int relPosition = position - mTypeList.indexOf(TYPE_FAVOURITE_ITEM);
            if (relPosition < mUserCenter.getFavourite().getItem().size()) {
                favViewHolder.itemView.setVisibility(View.VISIBLE);
                UserCenter.Favourite fav = mUserCenter.getFavourite().getItem().get(relPosition);
                favViewHolder.mFavoritesView.setImages(fav.getVideos());
            } else {
                favViewHolder.itemView.setVisibility(View.GONE);
            }
        } else if (type == TYPE_FOLLOW_BANGUMI_HEAD) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.mTvTitle.setText(mIsMe ? R.string.author_space_header_my_bangumi : R.string.author_space_header_bangumi);
            if (mUserCenter.getSetting().getBangumi() == 0) {
                headViewHolder.mTvCount.setText(R.string.author_space_privacy_denied);
            } else {
                headViewHolder.mTvCount.setText(StringUtils.formateNumber(mUserCenter.getSeason().getCount()));
                headViewHolder.mTvSubTitle.setText(R.string.head_title_enter);
            }
        } else if (type == TYPE_FOLLOW_BANGUMI_ITEM) {
            BangumiViewHolder bangumiViewHolder = (BangumiViewHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_FOLLOW_BANGUMI_ITEM);
            if (realPosition < mUserCenter.getSeason().getItem().size()) {
                bangumiViewHolder.itemView.setVisibility(View.VISIBLE);
                UserCenter.Season season = mUserCenter.getSeason().getItem().get(realPosition);
                Glide.with(mContext)
                        .load(season.getCover())
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), DisplayUtils.dip2px(mContext.getApplicationContext(), 2)))
                        .into(bangumiViewHolder.mIvCover);
                bangumiViewHolder.mTvTitle.setText(season.getTitle());
                bangumiViewHolder.mSeasonId = season.getParam();
                if (season.getNewestEpIndex().equals(season.getTotalCount())) {
                    bangumiViewHolder.mTvText1.setText(StringUtils.format("%s话全", season.getTotalCount()));
                } else {
                    bangumiViewHolder.mTvText1.setText(StringUtils.format("更新至%s话", season.getNewestEpIndex()));
                }
            } else {
                bangumiViewHolder.itemView.setVisibility(View.GONE);
            }
        } else if (type == TYPE_COMMUNITY_HEAD) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.mTvTitle.setText(mIsMe ? R.string.author_space_header_my_group : R.string.author_space_header_group);
            if (mUserCenter.getSetting().getGroups() == 0) {
                headViewHolder.mTvCount.setText(R.string.author_space_privacy_denied);
            } else {
                headViewHolder.mTvCount.setText(StringUtils.formateNumber(mUserCenter.getCommunity().getCount()));
                headViewHolder.mTvSubTitle.setText(R.string.head_title_enter);
            }
        } else if (type == TYPE_COMMUNITY_ITEM) {
            CommunityGameViewHolder communityGameViewHolder = (CommunityGameViewHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_COMMUNITY_ITEM);
            UserCenter.Community community = mUserCenter.getCommunity().getItem().get(realPosition);
            Glide.with(mContext)
                    .load(community.getThumb())
                    .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), DisplayUtils.dip2px(mContext.getApplicationContext(), 15)))
                    .into(communityGameViewHolder.mIvCover);
            communityGameViewHolder.mTvTitle.setText(community.getName());
            communityGameViewHolder.mTvDesc.setText(community.getDesc());
            communityGameViewHolder.mTvMemberCount.setText(StringUtils.format("乡民:%d", community.getMemberCount()));
            communityGameViewHolder.mPostCount.setText(StringUtils.format("帖子:%d", community.getPostCount()));
        } else if (type == TYPE_GAME_HEAD) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.mTvTitle.setText(mIsMe ? R.string.author_space_header_my_game : R.string.author_space_header_game);
            if (mUserCenter.getSetting().getPlayedGame() == 0) {
                headViewHolder.mTvCount.setText(R.string.author_space_privacy_denied);
            } else {
                headViewHolder.mTvCount.setText(StringUtils.formateNumber(mUserCenter.getGame().getCount()));
                headViewHolder.mTvSubTitle.setText(R.string.head_title_enter);
            }
        } else if (type == TYPE_GAME_ITEM) {
            CommunityGameViewHolder communityGameViewHolder = (CommunityGameViewHolder) holder;
            int realPosition = position - mTypeList.indexOf(TYPE_GAME_ITEM);
            UserCenter.Game game = mUserCenter.getGame().getItem().get(realPosition);
            Glide.with(mContext)
                    .load(game.getIcon())
                    .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), DisplayUtils.dip2px(mContext.getApplicationContext(), 15)))
                    .into(communityGameViewHolder.mIvCover);
            communityGameViewHolder.mTvTitle.setText(game.getName());
            communityGameViewHolder.mTvDesc.setText(game.getSummary());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    @Override
    public int getItemCount() {
        if (mUserCenter == null) {
            return 0;
        }
        mTypeList.clear();
        if (mUserCenter.getArchive() != null && mUserCenter.getArchive().getCount() != 0) {
            mTypeList.add(TYPE_ARCHIVE_HEAD);
            mTypeList.add(TYPE_ARCHIVE_ITEM);
        }
        if (mUserCenter.getSetting().getCoinsVideo() == 0) {
            mTypeList.add(TYPE_COIN_ARCHIVE_HEAD);
        } else if (mUserCenter.getCoinArchive() != null && mUserCenter.getCoinArchive().getCount() != 0) {
            mTypeList.add(TYPE_COIN_ARCHIVE_HEAD);
            mTypeList.add(TYPE_COIN_ARCHIVE_ITEM);
        }
        if (mUserCenter.getSetting().getFavVideo() == 0) {
            mTypeList.add(TYPE_FAVOURITE_HEAD);
        } else if (mUserCenter.getFavourite() != null && mUserCenter.getFavourite().getCount() != 0) {
            mTypeList.add(TYPE_FAVOURITE_HEAD);
            mTypeList.add(TYPE_FAVOURITE_ITEM);
            mTypeList.add(TYPE_FAVOURITE_ITEM);
            mTypeList.add(TYPE_FAVOURITE_ITEM);
        }
        if (mUserCenter.getSetting().getBangumi() == 0) {
            mTypeList.add(TYPE_FOLLOW_BANGUMI_HEAD);
        } else if (mUserCenter.getSeason() != null && mUserCenter.getSeason().getCount() != 0) {
            mTypeList.add(TYPE_FOLLOW_BANGUMI_HEAD);
            mTypeList.add(TYPE_FOLLOW_BANGUMI_ITEM);
            mTypeList.add(TYPE_FOLLOW_BANGUMI_ITEM);
            mTypeList.add(TYPE_FOLLOW_BANGUMI_ITEM);
        }
        if (mUserCenter.getSetting().getGroups() == 0) {
            mTypeList.add(TYPE_COMMUNITY_HEAD);
        } else if (mUserCenter.getCommunity() != null && mUserCenter.getCommunity().getItem() != null) {
            int count = mUserCenter.getCommunity().getCount() > 2 ? 2 : mUserCenter.getCommunity().getItem().size();
            if (count > 0) {
                mTypeList.add(TYPE_COMMUNITY_HEAD);
            }
            for (int i = 0; i < count; i++) {
                mTypeList.add(TYPE_COMMUNITY_ITEM);
            }
        }
        if (mUserCenter.getSetting().getPlayedGame() == 0) {
            mTypeList.add(TYPE_GAME_HEAD);
        } else if (mUserCenter.getGame() != null && mUserCenter.getGame().getItem() != null) {
            int count = mUserCenter.getGame().getCount() > 2 ? 2 : mUserCenter.getGame().getItem().size();
            if (count > 0) {
                mTypeList.add(TYPE_GAME_HEAD);
            }
            for (int i = 0; i < count; i++) {
                mTypeList.add(TYPE_GAME_ITEM);
            }
        }
        return mTypeList.size();
    }

    class HeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.count)
        TextView mTvCount;
        @BindView(R.id.sub_title)
        TextView mTvSubTitle;

        HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getItemViewType(), null);
            }
        }
    }

    class TwoVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_layout_1)
        ViewGroup mCard1;
        @BindView(R.id.card_layout_2)
        ViewGroup mCard2;
        private VideoViewHolder[] mVideos;

        TwoVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mVideos = new VideoViewHolder[]{new VideoViewHolder(mCard1), new VideoViewHolder(mCard2)};
            mCard1.setOnClickListener(this);
            mCard2.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener == null) {
                return;
            }
            if (v.getId() == R.id.card_layout_1) {
                mOnItemClickListener.onItemClick(getItemViewType(), mVideos[0].mAid);
            } else {
                mOnItemClickListener.onItemClick(getItemViewType(), mVideos[1].mAid);
            }
        }

        public class VideoViewHolder {
            @BindView(R.id.cover)
            ImageView mIvCover;
            @BindView(R.id.title)
            TextView mTvTitle;
            @BindView(R.id.info_views)
            TextView mTvInfoViews;
            @BindView(R.id.info_danmakus)
            TextView mTvInfoDanmakus;

            private View itemView;
            private String mAid;

            VideoViewHolder(View itemView) {
                this.itemView = itemView;
                ButterKnife.bind(this, itemView);
                int tintColor = ContextCompat.getColor(mContext, R.color.gray_dark);
                Drawable drawableCompat = DrawableCompat.wrap(mTvInfoViews.getCompoundDrawables()[0]);
                DrawableCompat.setTint(drawableCompat, tintColor);
                drawableCompat = DrawableCompat.wrap(mTvInfoDanmakus.getCompoundDrawables()[0]);
                DrawableCompat.setTint(drawableCompat, tintColor);
            }
        }
    }

    class FavViewHolder extends RecyclerView.ViewHolder {
        private FavoritesView mFavoritesView;

        public FavViewHolder(View itemView) {
            super(itemView);
            mFavoritesView = (FavoritesView) itemView;
        }
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
                mOnItemClickListener.onItemClick(getItemViewType(), mSeasonId);
            }
        }
    }

    class CommunityGameViewHolder extends RecyclerView.ViewHolder {

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

        public CommunityGameViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int type, String data);
    }
}
