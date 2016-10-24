package com.lh.imbilibili.view.adapter.feedback;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.BangumiDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/2.
 */
public class FeedbackEpAdapter extends RecyclerView.Adapter<FeedbackEpAdapter.EpisodeViewHolder> {
    private List<BangumiDetail.Episode> episodes;
    private int selectPosition = 0;

    private onEpClickListener listener;

    public FeedbackEpAdapter(@Nullable List<BangumiDetail.Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_ep_grid_item, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        if (position == 0 && episodes.size() == 0) {
            holder.tvIndex.setText("无");
            holder.newTag.setVisibility(View.GONE);
        } else {
            BangumiDetail.Episode episode = episodes.get(position);
            holder.tvIndex.setText(episode.getIndex());
            holder.itemView.setSelected(position == selectPosition);
        }
    }

    @Override
    public int getItemCount() {
        if (episodes == null) {
            return 0;
        } else {
            return episodes.size();
        }
    }

    public void selectItem(int position) {
        int pre = selectPosition;
        selectPosition = position;
        notifyItemChanged(selectPosition);
        notifyItemChanged(pre);
    }

    public void setOnEpClickListener(onEpClickListener listener) {
        this.listener = listener;
    }

    public interface onEpClickListener {
        void onEpClick(int position);
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.index)
        TextView tvIndex;
        @BindView(R.id.new_tag)
        TextView newTag;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(getAdapterPosition());
                    if (listener != null && episodes.size() > 0) {
                        listener.onEpClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
