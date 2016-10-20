package com.lh.imbilibili.view.adapter.bangumifragment;

import android.content.Context;
import android.graphics.Color;
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
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.model.IndexBangumiRecommend;
import com.lh.imbilibili.model.IndexPage;
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;
import com.lh.imbilibili.widget.BannerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by liuhui on 2016/7/6.
 * 番剧的RecyclerView Adapter
 */
@SuppressWarnings("WeakerAccess")
public class BangumiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int BANNER = 0;
    public static final int NAV = 1;
    public static final int SERIALIZING_HEAD = 2;
    public static final int SERIALIZING_GRID_ITEM = 3;
    public static final int SEASON_BANGUMI_HEAD = 4;
    public static final int SEASON_BANGUMI_ITEM = 5;
    public static final int BANGUMI_RECOMMEND_HEAD = 6;
    public static final int BANGUMI_RECOMMEND_ITEM = 7;

    private IndexPage mIndexPage;
    private List<IndexBangumiRecommend> mBangumis;

    private List<Integer> mTypeList;

    private int[] mHeadImgs = new int[]{R.drawable.bangumi_home_ic_season_1,
            R.drawable.bangumi_home_ic_season_2,
            R.drawable.bangumi_home_ic_season_3,
            R.drawable.bangumi_home_ic_season_4};
    private int[] mMonth = new int[]{1, 4, 7, 10};

    private Context context;

    private OnItemClickListener itemClickListener;

    public BangumiAdapter(Context context) {
        this.context = context;
        mTypeList = new ArrayList<>();
    }

    public void setmIndexPage(IndexPage indexPage) {
        mIndexPage = indexPage;
    }

    public void addBangumis(List<IndexBangumiRecommend> bangumis) {
        if (mBangumis == null) {
            mBangumis = bangumis;
        } else {
            mBangumis.addAll(bangumis);
        }
    }

    public void clearRecommend() {
        if (mBangumis != null) {
            mBangumis.clear();
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == BANNER) {
            View bannerView = inflater.inflate(R.layout.banner_item, parent, false);
            holder = new BannerHolder(bannerView);
        } else if (viewType == NAV) {
            View navView = inflater.inflate(R.layout.bangumi_nav_item, parent, false);
            holder = new NavHolder(navView);
        } else if (viewType == SERIALIZING_HEAD) {
            View headView = inflater.inflate(R.layout.common_head_item, parent, false);
            holder = new HeadHolder(headView);
        } else if (viewType == SERIALIZING_GRID_ITEM) {
            View gridView = inflater.inflate(R.layout.bangumi_grid_item_width_badge, parent, false);
            holder = new BangumiGridHolder(gridView);
        } else if (viewType == SEASON_BANGUMI_HEAD) {
            View headView = inflater.inflate(R.layout.common_head_item, parent, false);
            holder = new HeadHolder(headView);
        } else if (viewType == SEASON_BANGUMI_ITEM) {
            View container = inflater.inflate(R.layout.bangumi_grid_item, parent, false);
            holder = new SeasonHolder(container);
        } else if (viewType == BANGUMI_RECOMMEND_HEAD) {
            View headView = inflater.inflate(R.layout.common_head_item, parent, false);
            holder = new HeadHolder(headView);
        } else if (viewType == BANGUMI_RECOMMEND_ITEM) {
            View itemView = inflater.inflate(R.layout.bangumi_recommend_item, parent, false);
            holder = new BangumiRecommendHolder(itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*if (getItemViewType(position) == BANNER) {
            BannerHolder bannerHolder = (BannerHolder) holder;
            Glide.with(context)
                    .load(mIndexPage.getAd().getHead().get(position).getImg())
                    .into(bannerHolder.bannerView);
        } else*/
        if (getItemViewType(position) == SERIALIZING_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("新番连载");
            headHolder.setLeftDrawable(R.drawable.ic_lianzai);
            headHolder.tvSubTitle.setText("所有连载");
        } else if (getItemViewType(position) == SERIALIZING_GRID_ITEM) {
            BangumiGridHolder gridHolder = (BangumiGridHolder) holder;
            List<Bangumi> bangumis = mIndexPage.getSerializing();
            int realPosition = position - mTypeList.indexOf(SERIALIZING_GRID_ITEM);
            if (realPosition < bangumis.size()) {
                gridHolder.itemView.setVisibility(View.VISIBLE);
                Bangumi bangumi = bangumis.get(realPosition);
                Glide.with(context).load(bangumi.getCover())
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(context.getApplicationContext(), DisplayUtils.dip2px(context.getApplicationContext(), 2)))
                        .into(gridHolder.ivCover);
                gridHolder.tvFavourite.setVisibility(View.GONE);
                gridHolder.tv1.setVisibility(View.VISIBLE);
                gridHolder.tvTitle.setText(bangumi.getTitle());
                gridHolder.tv1.setText(StringUtils.format("更新至第%s话", bangumi.getNewestEpIndex()));
                gridHolder.tv2.setText(bangumi.getLastTime());
                Drawable drawable = DrawableCompat.wrap(gridHolder.tv2.getBackground());
                if (bangumi.getWatchingCount() >= 8000) {
                    gridHolder.tv2.setVisibility(View.VISIBLE);
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorPrimary));
                } else if (bangumi.getWatchingCount() >= 10) {
                    gridHolder.tv2.setVisibility(View.VISIBLE);
                    DrawableCompat.setTint(drawable, Color.GRAY);
                } else {
                    gridHolder.tv2.setVisibility(View.GONE);
                }
                gridHolder.tv2.setBackground(drawable);
                gridHolder.tv2.setText(StringUtils.format("%s人在看", StringUtils.formateNumber(bangumi.getWatchingCount())));
                gridHolder.mSeasonId = bangumi.getSeasonId();
            } else {
                gridHolder.itemView.setVisibility(View.GONE);
            }
        } else if (getItemViewType(position) == SEASON_BANGUMI_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText(StringUtils.format("%d月新番", mMonth[mIndexPage.getPrevious().getSeason() - 1]));
            headHolder.setLeftDrawable(mHeadImgs[mIndexPage.getPrevious().getSeason() - 1]);
            headHolder.tvSubTitle.setText("分季列表");
        } else if (getItemViewType(position) == SEASON_BANGUMI_ITEM) {
            SeasonHolder seasonHolder = (SeasonHolder) holder;
            List<Bangumi> bangumis = mIndexPage.getPrevious().getList();
            int realPosition = position - mTypeList.indexOf(SEASON_BANGUMI_ITEM);
            if (realPosition < bangumis.size()) {
                seasonHolder.itemView.setVisibility(View.VISIBLE);
                Bangumi bangumi = bangumis.get(realPosition);
                seasonHolder.tv1.setVisibility(View.GONE);
                seasonHolder.tvFavourite.setVisibility(View.VISIBLE);
                Glide.with(context).load(bangumi.getCover())
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(context.getApplicationContext(), DisplayUtils.dip2px(context.getApplicationContext(), 2)))
                        .into(seasonHolder.ivCover);
                seasonHolder.tvTitle.setText(bangumi.getTitle());
                seasonHolder.tvFavourite.setText(StringUtils.format("%s人在追番", StringUtils.formateNumber(bangumi.getFavourites())));
                seasonHolder.mSeasonId = bangumi.getSeasonId();
            } else {
                seasonHolder.itemView.setVisibility(View.GONE);
            }
        } else if (getItemViewType(position) == BANGUMI_RECOMMEND_HEAD) {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvTitle.setText("番剧推荐");
            headHolder.setLeftDrawable(R.drawable.ic_bangumi_recommend);
            headHolder.tvSubTitle.setVisibility(View.GONE);
        } else if (getItemViewType(position) == BANGUMI_RECOMMEND_ITEM) {
            BangumiRecommendHolder recommendHolder = (BangumiRecommendHolder) holder;
            int realPosition = position - mTypeList.indexOf(BANGUMI_RECOMMEND_ITEM);
            IndexBangumiRecommend bangumi = mBangumis.get(realPosition);
            Glide.with(context).load(bangumi.getCover()).into(recommendHolder.ivCover);
            recommendHolder.tvTitle.setText(bangumi.getTitle());
            recommendHolder.tv1.setText(bangumi.getDesc());
            recommendHolder.ivBadge.setVisibility("1".equals(bangumi.getIsNew()) ? View.VISIBLE : View.GONE);
            recommendHolder.data = bangumi.getLink();
        }
    }

    @Override
    public int getItemCount() {
        mTypeList.clear();
        if (mIndexPage != null && mIndexPage.getAd() != null && mIndexPage.getAd().getHead() != null && !mIndexPage.getAd().getHead().isEmpty()) {
            mTypeList.add(BANNER);
        }
        mTypeList.add(NAV);
        if (mIndexPage != null) {
            if (mIndexPage.getSerializing() != null && !mIndexPage.getSerializing().isEmpty()) {
                mTypeList.add(SERIALIZING_HEAD);
                for (int i = 0; i < 6; i++) {
                    mTypeList.add(SERIALIZING_GRID_ITEM);
                }
            }
            if (mIndexPage.getPrevious() != null && mIndexPage.getPrevious().getList() != null) {
                mTypeList.add(SEASON_BANGUMI_HEAD);
                mTypeList.add(SEASON_BANGUMI_ITEM);
                mTypeList.add(SEASON_BANGUMI_ITEM);
                mTypeList.add(SEASON_BANGUMI_ITEM);
            }
        }
        if (mBangumis != null) {
            mTypeList.add(BANGUMI_RECOMMEND_HEAD);
            for (int i = 0; i < mBangumis.size(); i++) {
                mTypeList.add(BANGUMI_RECOMMEND_ITEM);
            }
        }
        return mTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    public interface OnItemClickListener {
        void onClick(int itemType, String data);
    }

    public class BannerHolder extends RecyclerView.ViewHolder {

        BannerView bannerView;
        BannerAdapter adapter;

        public BannerHolder(View itemView) {
            super(itemView);
            bannerView = (BannerView) itemView;
            adapter = new BannerAdapter();
            adapter.setData(mIndexPage.getAd().getHead());
            bannerView.setAdaper(adapter);
        }
    }

    public class NavHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.follow_bangumi)
        ViewGroup followBangumi;

        public NavHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            followBangumi.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onClick(NAV, v.getId() + "");
            }
        }
    }

    public class HeadHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.sub_title)
        TextView tvSubTitle;

        public HeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(getItemViewType(), "");
                    }
                }
            });
        }

        public void setLeftDrawable(int resId) {
            Drawable drawable = ContextCompat.getDrawable(context, resId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(drawable, null, null, null);
        }
    }

    public class BangumiGridHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedback_container)
        ViewGroup container;
        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.text1)
        TextView tv1;
        @BindView(R.id.text2)
        TextView tv2;
        @BindView(R.id.favourites)
        TextView tvFavourite;

        private String mSeasonId;

        public BangumiGridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(SERIALIZING_GRID_ITEM, mSeasonId);
                    }
                }
            });
        }
    }

    public class SeasonHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedback_container)
        ViewGroup container;
        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.text1)
        TextView tv1;
        @BindView(R.id.favourites)
        TextView tvFavourite;
        private String mSeasonId;

        public SeasonHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(SEASON_BANGUMI_ITEM, mSeasonId);
                    }
                }
            });
        }
    }

    public class BangumiRecommendHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.badge)
        ImageView ivBadge;
        @BindView(R.id.text1)
        TextView tv1;

        private String data;

        public BangumiRecommendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(BANGUMI_RECOMMEND_ITEM, data);
                    }
                }
            });
        }
    }
}
