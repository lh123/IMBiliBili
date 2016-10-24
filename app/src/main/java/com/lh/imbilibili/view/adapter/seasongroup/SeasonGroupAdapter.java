package com.lh.imbilibili.view.adapter.seasongroup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.Bangumi;
import com.lh.imbilibili.model.bangumi.SeasonGroup;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/8.
 */
public class SeasonGroupAdapter extends RecyclerView.Adapter {

    public static final int SEASON_HEAD = 1;
    public static final int SEASON_ITEM = 2;

    private onItemClickListener mOnItemClickListener;

    private int[] mHeadImgs = new int[]{R.drawable.bangumi_home_ic_season_1,
            R.drawable.bangumi_home_ic_season_2,
            R.drawable.bangumi_home_ic_season_3,
            R.drawable.bangumi_home_ic_season_4};
    private int[] mMonth = new int[]{1, 4, 7, 10};

    private List<SeasonGroup> mSeasonGroups;

    private Context mContext;

    public SeasonGroupAdapter(Context mContext, @Nullable List<SeasonGroup> mSeasonGroups) {
        this.mContext = mContext;
        this.mSeasonGroups = mSeasonGroups;
    }

    public void setSeasonGroups(List<SeasonGroup> seasonGroups) {
        mSeasonGroups = seasonGroups;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        if (viewType == SEASON_HEAD) {
            View view = inflater.inflate(R.layout.common_head_item, parent, false);
            viewHolder = new SeasonHeadHolder(view);
        } else {
            View view = inflater.inflate(R.layout.bangumi_grid_item, parent, false);
            viewHolder = new SeasonItemHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int index = position / 4;
        SeasonGroup seasonGroup = mSeasonGroups.get(index);
        if (getItemViewType(position) == SEASON_HEAD) {
            SeasonHeadHolder headHolder = (SeasonHeadHolder) holder;
            Drawable drawable = ContextCompat.getDrawable(mContext, mHeadImgs[seasonGroup.getSeason() - 1]);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            headHolder.tvTitle.setCompoundDrawables(drawable, null, null, null);
            headHolder.tvTitle.setText(StringUtils.format("%d年%d月", seasonGroup.getYear(), mMonth[seasonGroup.getSeason() - 1]));
            headHolder.tvSubTitle.setText("更多");
            headHolder.mYear = seasonGroup.getYear();
            headHolder.mMonth = seasonGroup.getSeason();
        } else {
            SeasonItemHolder itemHolder = (SeasonItemHolder) holder;
            int groupIndex = position - (index * 4 + 1);
            int groupSize = seasonGroup.getList().size();
            if (groupIndex < groupSize) {
                itemHolder.itemView.setVisibility(View.VISIBLE);
                Bangumi bangumi = seasonGroup.getList().get(groupIndex);
                Glide.with(mContext).load(bangumi.getCover())
                        .transform(new RoundedCornersTransformation(mContext, 3))
                        .into(itemHolder.ivCover);
                itemHolder.tv1.setVisibility(View.GONE);
                itemHolder.tvFavourite.setVisibility(View.VISIBLE);
                itemHolder.mSeasonId = bangumi.getSeasonId();
                itemHolder.tvTitle.setText(bangumi.getTitle());
                itemHolder.tvFavourite.setText(StringUtils.format("%s人在追番", StringUtils.formateNumber(bangumi.getFavourites())));
            } else {
                itemHolder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mSeasonGroups == null) {
            return 0;
        } else {
            return mSeasonGroups.size() * 3 + mSeasonGroups.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 4 == 0) {
            return SEASON_HEAD;
        } else {
            return SEASON_ITEM;
        }
    }

    public interface onItemClickListener {
        void onItemClick(int type, RecyclerView.ViewHolder viewHolder);
    }

    public class SeasonHeadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.sub_title)
        TextView tvSubTitle;
        private int mYear;
        private int mMonth;

        public SeasonHeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public int getMonth() {
            return mMonth;
        }

        public int getYear() {
            return mYear;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(SEASON_HEAD, this);
            }
        }
    }

    public class SeasonItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public SeasonItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public String getSeasonId() {
            return mSeasonId;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(SEASON_ITEM, this);
            }
        }
    }
}
