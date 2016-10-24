package com.lh.imbilibili.view.adapter.bangumidetail;

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
 * Created by home on 2016/7/30.
 */
public class BangumiEpAdapter extends RecyclerView.Adapter<BangumiEpAdapter.EpisodeViewHolder> {

    private List<BangumiDetail.Episode> episodes;
    private int selectPosition = 0;

    private onEpClickListener listener;

    public BangumiEpAdapter(List<BangumiDetail.Episode> episodes) {
        this.episodes = episodes;
    }

    public void setEpisodes(List<BangumiDetail.Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public BangumiEpAdapter.EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_ep_grid_item, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiEpAdapter.EpisodeViewHolder holder, int position) {
        if (position == 0 && episodes.size() == 0) {
            holder.tvIndex.setText("无");
            holder.newTag.setVisibility(View.GONE);
        } else {
            BangumiDetail.Episode episode = episodes.get(position);
            if ("1".equals(episode.getIsNew())) {
                holder.newTag.setVisibility(View.VISIBLE);
            } else {
                holder.newTag.setVisibility(View.GONE);
            }
            holder.tvIndex.setText(episode.getIndex());
            holder.itemView.setSelected(position == selectPosition);
        }
    }

    @Override
    public int getItemCount() {
        if (episodes.size() > 0) {
            return episodes.size();
        } else {
            return 1;
        }
    }

    private void selectItem(int position) {
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
