package com.lh.imbilibili.view.adapter.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.search.Season;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/6.
 */

public class BangumiSearchAdapter extends RecyclerView.Adapter {

    private List<Season> mSeasons;

    private Context mContext;
    private OnBangumiItemClickListener mOnBangumiItemClickListener;

    public BangumiSearchAdapter(Context context) {
        mContext = context;
    }

    public void setOnBangumiItemClickListener(OnBangumiItemClickListener listener) {
        mOnBangumiItemClickListener = listener;
    }

    public void addData(List<Season> datas) {
        if (datas == null) {
            return;
        }
        if (mSeasons == null) {
            mSeasons = datas;
        } else {
            mSeasons.addAll(datas);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_bangumi_item, parent, false);
        return new BangumiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BangumiViewHolder bangumiViewHolder = (BangumiViewHolder) holder;
        Season season = mSeasons.get(position);
        bangumiViewHolder.mParam = season.getParam();
        Glide.with(mContext).load(season.getCover()).into(bangumiViewHolder.mIvCover);
        bangumiViewHolder.mTvTitle.setText(season.getTitle());
        if (season.getFinish() == 1) {
            bangumiViewHolder.mTvNewestEp.setText(StringUtils.format("%s，%d话全", season.getNewestSeason(), season.getTotalCount()));
        } else {
            bangumiViewHolder.mTvNewestEp.setText(StringUtils.format("%s，更新至第%s话", season.getNewestSeason(), season.getIndex()));
        }
        bangumiViewHolder.mTvCatDesc.setText(season.getCatDesc());
    }

    @Override
    public int getItemCount() {
        if (mSeasons == null) {
            return 0;
        }
        return mSeasons.size();
    }

    class BangumiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.type)
        TextView mTvType;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.newest_ep)
        TextView mTvNewestEp;
        @BindView(R.id.cat_desc)
        TextView mTvCatDesc;

        String mParam;

        BangumiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnBangumiItemClickListener != null) {
                mOnBangumiItemClickListener.onBangumiClick(mParam);
            }
        }
    }

    public interface OnBangumiItemClickListener {
        void onBangumiClick(String aid);
    }
}
