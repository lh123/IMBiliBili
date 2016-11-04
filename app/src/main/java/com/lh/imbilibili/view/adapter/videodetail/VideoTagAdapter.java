package com.lh.imbilibili.view.adapter.videodetail;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/10/27.
 */

public class VideoTagAdapter extends RecyclerView.Adapter {

    private String[] mTags;

    private OnTagClickListener mOnTagClickListener;

    public void setTags(String[] tags) {
        mTags = tags;
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.mOnTagClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tagView = new TextView(parent.getContext());
        tagView.setGravity(Gravity.CENTER);
        int padding = parent.getResources().getDimensionPixelOffset(R.dimen.item_spacing);
        int paddLR = parent.getResources().getDimensionPixelOffset(R.dimen.item_medium_spacing);
        tagView.setPadding(paddLR, padding, paddLR, padding);
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tagView.setBackgroundResource(R.drawable.selector_bangumi_tag_bg);
        tagView.setTextColor(Color.BLACK);
        return new TagViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TagViewHolder tagViewHolder = (TagViewHolder) holder;
        tagViewHolder.mTagName.setText(mTags[position]);
    }

    @Override
    public int getItemCount() {
        if (mTags == null) {
            return 0;
        } else {
            return mTags.length;
        }
    }

    private class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTagName;

        TagViewHolder(TextView itemView) {
            super(itemView);
            mTagName = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(mTagName.getText().toString());
            }
        }
    }

    public interface OnTagClickListener {
        void onTagClick(String tagContent);
    }
}
