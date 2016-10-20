package com.lh.imbilibili.view.adapter.categoryfragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/9/29.
 */

public class GridViewAdapter extends RecyclerView.Adapter {

    private OnItemClickListener mOnItemClickListener;

    private String[] itemNames = new String[]{
            "直播",
            "番剧",
            "动画",
            "音乐",
            "舞蹈",
            "游戏",
            "科技",
            "生活",
            "鬼畜",
            "时尚",
            "广告",
            "娱乐",
            "电影",
            "电视剧",
            "游戏中心",
    };

    private int[] itemIcon = new int[]{
            R.mipmap.ic_category_live,
            R.mipmap.ic_category_t13,
            R.mipmap.ic_category_t1,
            R.mipmap.ic_category_t3,
            R.mipmap.ic_category_t129,
            R.mipmap.ic_category_t4,
            R.mipmap.ic_category_t36,
            R.mipmap.ic_category_t160,
            R.mipmap.ic_category_t119,
            R.mipmap.ic_category_t155,
            R.mipmap.ic_category_t165,
            R.mipmap.ic_category_t5,
            R.mipmap.ic_category_t23,
            R.mipmap.ic_category_t11,
            R.mipmap.ic_category_game_center
    };


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        ViewHolder holder = (ViewHolder) h;
        holder.mItemIcon.setImageResource(itemIcon[position]);
        holder.mItemName.setText(itemNames[position]);
    }

    @Override
    public int getItemCount() {
        return itemNames.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_name)
        TextView mItemName;

        @BindView(R.id.iv_ico)
        ImageView mItemIcon;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
