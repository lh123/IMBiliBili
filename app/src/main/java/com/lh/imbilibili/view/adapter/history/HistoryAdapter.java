package com.lh.imbilibili.view.adapter.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.history.History;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/8.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> mHistorys;

    private Context mContext;
    private OnHistoryItemClickLisstener mOnHistoryItemClickLisstener;

    public HistoryAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<History> histories) {
        mHistorys = histories;
    }

    public void setOnHistoryItemClickLisstener(OnHistoryItemClickLisstener lisstener) {
        mOnHistoryItemClickLisstener = lisstener;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        History history = mHistorys.get(position);
        Glide.with(mContext).load(history.getPic()).centerCrop().into(holder.mIvCover);
        holder.mTvTitle.setText(history.getTitle());
        holder.mTvViewTime.setText(StringUtils.formateDateActu(history.getViewAt() * 1000));
    }

    @Override
    public int getItemCount() {
        if (mHistorys == null) {
            return 0;
        }
        return mHistorys.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.view_time)
        TextView mTvViewTime;

        HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnHistoryItemClickLisstener != null) {
                mOnHistoryItemClickLisstener.onHistoryItemClick(mHistorys.get(getAdapterPosition()).getAid() + "");
            }
        }
    }

    public interface OnHistoryItemClickLisstener {
        void onHistoryItemClick(String aid);
    }
}
