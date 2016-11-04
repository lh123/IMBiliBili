package com.lh.imbilibili.view.adapter.videodetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.video.VideoDetail;

import java.util.List;

/**
 * Created by liuhui on 2016/10/2.
 */

public class VideoPageRecyclerViewAdapter extends RecyclerView.Adapter<VideoPageRecyclerViewAdapter.PageViewHolder> {
    private List<VideoDetail.Page> mPages;
    private int selectPosition = 0;

    private OnPageClickListener listener;

    public void setData(List<VideoDetail.Page> pages) {
        mPages = pages;
    }

    public void setOnPageClickListener(OnPageClickListener listener) {
        this.listener = listener;
    }

    @Override
    public VideoPageRecyclerViewAdapter.PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_detail_page_item, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoPageRecyclerViewAdapter.PageViewHolder holder, int position) {
        VideoDetail.Page page = mPages.get(position);
        holder.mTvTitle.setText(page.getPart());
        holder.itemView.setSelected(position == selectPosition);
        holder.mTvTitle.setSelected(position == selectPosition);
    }

    @Override
    public int getItemCount() {
        if (mPages == null) {
            return 0;
        }
        return mPages.size();
    }

    private void selectItem(int position) {
        int pre = selectPosition;
        selectPosition = position;
        notifyItemChanged(selectPosition);
        notifyItemChanged(pre);
    }


    public interface OnPageClickListener {
        void onPageClick(int position);
    }

    class PageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvTitle;

        PageViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_page_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectItem(getAdapterPosition());
            if (listener != null) {
                listener.onPageClick(getAdapterPosition());
            }
        }
    }
}
