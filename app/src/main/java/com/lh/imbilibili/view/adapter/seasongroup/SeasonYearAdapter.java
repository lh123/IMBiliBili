package com.lh.imbilibili.view.adapter.seasongroup;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;

import java.util.List;

/**
 * Created by home on 2016/8/9.
 */
public class SeasonYearAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private List<Integer> mYears;

    private onYearItemClickListener mOnYearItemClickListener;

    private int mCurrentSelectPosition = 0;

    public SeasonYearAdapter(Context context, @Nullable List<Integer> years) {
        mContext = context;
        mYears = years;
    }

    public void setYears(List<Integer> years) {
        mYears = years;
    }

    public void setOnYearItemClickListener(onYearItemClickListener l) {
        mOnYearItemClickListener = l;
    }

    public void selectItem(int position) {
        int prePosition = mCurrentSelectPosition;
        mCurrentSelectPosition = position;
        notifyItemChanged(prePosition);
        notifyItemChanged(mCurrentSelectPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setGravity(Gravity.CENTER);
        return new SeasonYearHolder(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SeasonYearHolder yearHolder = (SeasonYearHolder) holder;
        yearHolder.textView.setText(String.valueOf(mYears.get(position)));
        if (mCurrentSelectPosition == position) {
            yearHolder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            yearHolder.textView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        if (mYears == null) {
            return 0;
        } else {
            return mYears.size();
        }
    }

    public interface onYearItemClickListener {
        void onYearItemClick(int year);
    }

    public class SeasonYearHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public SeasonYearHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectItem(getAdapterPosition());
            if (mOnYearItemClickListener != null) {
                mOnYearItemClickListener.onYearItemClick(mYears.get(getAdapterPosition()));
            }
        }
    }
}
