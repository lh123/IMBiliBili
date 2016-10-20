package com.lh.imbilibili.view.adapter.bangumidetailactivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.widget.IndicatorTextView;

import java.util.List;

/**
 * Created by home on 2016/7/31.
 */
public class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.SeasonViewHold> {

    private Context context;
    private List<Bangumi> seasons;

    private OnSeasonItemClickListener listener;

    private int selectPosition = 0;

    public SeasonListAdapter(Context context, List<Bangumi> seasons) {
        this.context = context;
        this.seasons = seasons;
    }

    public void setSeasons(List<Bangumi> seasons) {
        this.seasons = seasons;
    }

    @Override
    public SeasonViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        IndicatorTextView view = new IndicatorTextView(parent.getContext());
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.BLACK);
        return new SeasonViewHold(view);
    }

    @Override
    public void onBindViewHolder(SeasonViewHold holder, int position) {
        Drawable foreDrawable = null;
        Drawable backDrawable = null;
        if (position == 0) {
            backDrawable = ContextCompat.getDrawable(context, R.drawable.bangumi_season_title_background_first);
            foreDrawable = ContextCompat.getDrawable(context, R.drawable.selector_bangumi_season_indicator_first);
        } else if (position == seasons.size() - 1) {
            backDrawable = ContextCompat.getDrawable(context, R.drawable.bangumi_season_title_background_last);
            foreDrawable = ContextCompat.getDrawable(context, R.drawable.selector_bangumi_season_indicator_last);
        } else {
            backDrawable = ContextCompat.getDrawable(context, R.drawable.bangumi_season_title_background_m);
            foreDrawable = ContextCompat.getDrawable(context, R.drawable.selector_bangumi_season_indicator_mid);
        }
        int[][] states = new int[2][];
        int[] colors = new int[]{Color.GRAY, Color.WHITE};
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        backDrawable = DrawableCompat.wrap(backDrawable);
        foreDrawable = DrawableCompat.wrap(foreDrawable);
        DrawableCompat.setTintList(backDrawable, colorStateList);
        DrawableCompat.setTint(foreDrawable, ContextCompat.getColor(context, R.color.colorPrimaryDark));
        if (selectPosition == position) {
            holder.tvTitle.setIndicatorDrawable(foreDrawable);
        } else {
            holder.tvTitle.setIndicatorDrawable(null);
        }
        holder.tvTitle.setBackground(backDrawable);
        holder.tvTitle.setText(seasons.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return seasons.size();
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

    public class SeasonViewHold extends RecyclerView.ViewHolder {
        IndicatorTextView tvTitle;

        public SeasonViewHold(View itemView) {
            super(itemView);
            tvTitle = (IndicatorTextView) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
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
