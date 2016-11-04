package com.lh.imbilibili.view.adapter.bangumidetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.Bangumi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/7/31.
 */
public class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.SeasonViewHolder> {

    private static final int TYPE_FIRST = 1;
    private static final int TYPE_MIDDLE = 2;
    private static final int TYPE_LAST = 3;

    private Context context;
    private List<Bangumi> seasons;

    private OnSeasonItemClickListener listener;

    private int selectPosition = 0;

    public SeasonListAdapter(Context context) {
        this.context = context;
    }

    public void setSeasons(List<Bangumi> seasons) {
        this.seasons = seasons;
    }

    @Override
    public SeasonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Drawable backDrawable;
        Drawable foreDrawable;
        if (viewType == TYPE_FIRST) {
            backDrawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.selector_bangumi_season_first_bg);
            foreDrawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.selector_bangumi_season_indicator_first);
        } else if (viewType == TYPE_LAST) {
            backDrawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.selector_bangumi_season_last_bg);
            foreDrawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.selector_bangumi_season_indicator_last);
        } else {
            backDrawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.selector_bangumi_season_mid_bg);
            foreDrawable = ContextCompat.getDrawable(parent.getContext(), R.drawable.selector_bangumi_season_indicator_mid);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seasons_item, parent, false);
        return new SeasonViewHolder(view, foreDrawable, backDrawable);
    }

    @Override
    public void onBindViewHolder(SeasonViewHolder holder, int position) {
        holder.mIndicatorView.setSelected(selectPosition == position);
        holder.mTvTitle.setSelected(selectPosition == position);
        holder.mTvTitle.setText(seasons.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        if (seasons == null) {
            return 0;
        } else {
            return seasons.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST;
        } else if (position == seasons.size() - 1) {
            return TYPE_LAST;
        } else {
            return TYPE_MIDDLE;
        }
    }

    public void selectItem(int position) {
        int prePosition = selectPosition;
        selectPosition = position;
        notifyItemChanged(prePosition);
        notifyItemChanged(selectPosition);
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public String getSelectSeasonId() {
        return seasons.get(selectPosition).getSeasonId();
    }

    public void selectItem(String seasonId) {
        for (int i = 0; i < seasons.size(); i++) {
            Bangumi bangumi = seasons.get(i);
            if (bangumi.getSeasonId().equals(seasonId)) {
                selectItem(i);
                break;
            }
        }
    }

    public void setOnSeasonItemClickListener(OnSeasonItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnSeasonItemClickListener {
        void onSeasonItemClick(int position);
    }

    public class SeasonViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.indicator)
        FrameLayout mIndicatorView;
        @BindView(R.id.title)
        TextView mTvTitle;

        private Drawable mForeDrawable;
        private Drawable mBackDrawable;

        public SeasonViewHolder(View itemView, Drawable fore, Drawable back) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mForeDrawable = DrawableCompat.wrap(fore.mutate());
            mBackDrawable = DrawableCompat.wrap(back.mutate());
            DrawableCompat.setTint(mForeDrawable, ContextCompat.getColor(context, R.color.colorPrimaryDark));
            DrawableCompat.setTintList(mBackDrawable, ContextCompat.getColorStateList(context, R.color.select_seasons_item_bg));
            mIndicatorView.setBackground(mBackDrawable);
            mIndicatorView.setForeground(mForeDrawable);
            mIndicatorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(getAdapterPosition());
                    if (listener != null) {
                        listener.onSeasonItemClick(getAdapterPosition());
                    }
                }
            });

        }
    }
}
