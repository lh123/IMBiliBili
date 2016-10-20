package com.lh.imbilibili.view.adapter.categoryfragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.view.adapter.categoryfragment.model.PartionModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/1.
 */

public class SubPartionGridAdapter extends RecyclerView.Adapter<SubPartionGridAdapter.PartionHolder> {

    private List<PartionModel.Partion> partions;

    private OnItemClickListener mOnItemClickListener;

    public SubPartionGridAdapter(List<PartionModel.Partion> partions) {
        this.partions = partions;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public PartionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_partion_item, parent, false);
        return new PartionHolder(view);
    }

    @Override
    public void onBindViewHolder(PartionHolder holder, int position) {
        holder.mIcon.setImageResource(partions.get(position).getImgId());
        holder.mName.setText(partions.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return partions.size();
    }

    class PartionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_ico)
        ImageView mIcon;
        @BindView(R.id.tv_name)
        TextView mName;

        PartionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
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
