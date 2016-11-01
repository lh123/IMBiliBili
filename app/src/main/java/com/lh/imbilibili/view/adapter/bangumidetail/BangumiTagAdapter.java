package com.lh.imbilibili.view.adapter.bangumidetail;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.Tag;

import java.util.List;

/**
 * Created by liuhui on 2016/10/31.
 * TagAdapter
 */

public class BangumiTagAdapter extends RecyclerView.Adapter {

    private List<Tag> mBangumiTags;

    private OnTagClickListener mOnTagClickListener;

    public void setBangumiTags(List<Tag> bangumiTags) {
        mBangumiTags = bangumiTags;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tagView = new TextView(parent.getContext());
        tagView.setGravity(Gravity.CENTER);
        int padding = parent.getResources().getDimensionPixelOffset(R.dimen.item_spacing);
        tagView.setPadding(padding, padding, padding, padding);
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tagView.setBackgroundResource(R.drawable.selector_bangumi_tag_bg);
        tagView.setTextColor(Color.BLACK);
        return new TagViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TagViewHolder tagViewHolder = (TagViewHolder) holder;
        Tag tag = mBangumiTags.get(position);
        tagViewHolder.mTagName.setText(tag.getTagName());
        tagViewHolder.mTagId = tag.getTagId();
    }

    @Override
    public int getItemCount() {
        if (mBangumiTags == null) {
            return 0;
        } else {
            return mBangumiTags.size();
        }
    }

    private class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTagName;
        private String mTagId;

        TagViewHolder(TextView itemView) {
            super(itemView);
            mTagName = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(mTagId);
            }
        }
    }

    public interface OnTagClickListener {
        void onTagClick(String tagId);
    }
}
