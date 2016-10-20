package com.lh.imbilibili.view.adapter.bangumidetailactivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;
import com.lh.imbilibili.widget.ScalableImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/1.
 */
public class BangumiRecommendAdapter extends RecyclerView.Adapter<BangumiRecommendAdapter.BangumiHolder> {

    private Context context;
    private List<Bangumi> bangumis;

    private OnBangumiRecommendItemClickListener itemClickListener;

    public BangumiRecommendAdapter(Context context, List<Bangumi> bangumis) {
        this.context = context;
        this.bangumis = bangumis;
    }

    public void setOnBangumiRecommendItemClickListener(OnBangumiRecommendItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setBangumis(List<Bangumi> bangumis) {
        this.bangumis = bangumis;
    }

    @Override
    public BangumiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_grid_item, parent, false);
        return new BangumiHolder(view);
    }

    @Override
    public void onBindViewHolder(BangumiHolder holder, int position) {
        Bangumi bangumi = bangumis.get(position);
        Glide.with(context).load(bangumi.getCover())
                .transform(new RoundedCornersTransformation(context.getApplicationContext(), 2))
                .into(holder.ivCover);
        holder.tvTitle.setText(bangumi.getTitle());
        holder.tvFavourite.setText(StringUtils.format("%s人追番", StringUtils.formateNumber(bangumi.getFollow())));
    }

    @Override
    public int getItemCount() {
        return bangumis.size();
    }

    public interface OnBangumiRecommendItemClickListener {
        void onBangumiRecommendItemClick(int position);
    }

    public class BangumiHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cover)
        ScalableImageView ivCover;
        @BindView(R.id.favourites)
        TextView tvFavourite;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.text1)
        TextView txt1;

        public BangumiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txt1.setVisibility(View.GONE);
            tvFavourite.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBangumiRecommendItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
