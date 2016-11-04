package com.lh.imbilibili.view.adapter.bangumidetail;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/7/30.
 */
public class BangumiEpAdapter extends RecyclerView.Adapter<BangumiEpAdapter.EpisodeViewHolder> {

    private List<BangumiDetail.Episode> episodes;
    private int mSelectPosition = 0;

    private boolean mItemMatchWidth = false;

    private OnEpClickListener listener;

    public void setEpisodes(List<BangumiDetail.Episode> episodes) {
        this.episodes = episodes;
    }

    public void setItemMatchWidht(boolean matchWidht) {
        mItemMatchWidth = matchWidht;
    }

    public void setSelectPosition(int position) {
        int prePosition = mSelectPosition;
        mSelectPosition = position;
        notifyItemChanged(prePosition);
        notifyItemChanged(mSelectPosition);
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    @Override
    public BangumiEpAdapter.EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_ep_grid_item, parent, false);
        if (mItemMatchWidth) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.setLayoutParams(params);
        }
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiEpAdapter.EpisodeViewHolder holder, int position) {
        if (position == 0 && episodes.size() == 0) {
            holder.mTvTitle.setText("无");
            holder.mBadge.setVisibility(View.GONE);
            holder.mTvIndexTitle.setVisibility(View.GONE);
        } else {
            BangumiDetail.Episode episode = episodes.get(position);
            if ("1".equals(episode.getIsNew())) {
                holder.mBadge.setVisibility(View.VISIBLE);
                holder.mBadge.setBackgroundResource(R.color.colorPrimary);
            } else {
                holder.mBadge.setVisibility(View.GONE);
            }
            holder.mTvTitle.setText(StringUtils.format("第%s话", episode.getIndex()));
            if (TextUtils.isEmpty(episodes.get(0).getIndexTitle())) {
                holder.mTvIndexTitle.setVisibility(View.GONE);
            } else {
                holder.mTvTitle.setGravity(Gravity.START);
                holder.mTvIndexTitle.setVisibility(View.VISIBLE);
                holder.mTvIndexTitle.setText(episode.getIndexTitle());
            }
            holder.mIndicator.setSelected(position == mSelectPosition);
            holder.mTvIndexTitle.setSelected(position == mSelectPosition);
            holder.mTvTitle.setSelected(position == mSelectPosition);
        }
    }

    @Override
    public int getItemCount() {
        if (episodes == null) {
            return 0;
        } else {
            if (episodes.size() > 0) {
                return episodes.size();
            } else {
                return 1;
            }
        }
    }

    private void selectItem(int position) {
        int pre = mSelectPosition;
        mSelectPosition = position;
        notifyItemChanged(mSelectPosition);
        notifyItemChanged(pre);
    }

    public void setOnEpClickListener(OnEpClickListener listener) {
        this.listener = listener;
    }

    public interface OnEpClickListener {
        void onEpClick(int position);
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.indicator)
        ViewGroup mIndicator;
        @BindView(R.id.title_layout)
        LinearLayout mTitleLayout;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.index_title)
        TextView mTvIndexTitle;
        @BindView(R.id.badge)
        ImageView mBadge;

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
