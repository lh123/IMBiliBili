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
import com.lh.imbilibili.model.search.Up;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/6.
 */

public class UpUserSearchAdapter extends RecyclerView.Adapter {

    private List<Up> mUps;

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public UpUserSearchAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addData(List<Up> datas) {
        if (datas == null) {
            return;
        }
        if (mUps == null) {
            mUps = datas;
        } else {
            mUps.addAll(datas);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_upuser_item, parent, false);
        return new UpUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UpUserViewHolder upUserViewHolder = (UpUserViewHolder) holder;
        Up up = mUps.get(position);
        Glide.with(mContext).load(up.getCover()).asBitmap().transform(new CircleTransformation(mContext.getApplicationContext())).into(upUserViewHolder.mIvAvatar);
        upUserViewHolder.mTvTitle.setText(up.getTitle());
        upUserViewHolder.mTvFanNum.setText(StringUtils.format("粉丝:%s", StringUtils.formateNumber(up.getFans())));
        upUserViewHolder.mTvVideoNum.setText(StringUtils.format("视频数:%s", StringUtils.formateNumber(up.getArchives())));
        upUserViewHolder.mTvSign.setText(up.getSign());
        upUserViewHolder.mMid = up.getParam();
    }

    @Override
    public int getItemCount() {
        if (mUps == null) {
            return 0;
        }
        return mUps.size();
    }

    class UpUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.avatar)
        ImageView mIvAvatar;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.fan_num)
        TextView mTvFanNum;
        @BindView(R.id.video_num)
        TextView mTvVideoNum;
        @BindView(R.id.sign)
        TextView mTvSign;

        private String mMid;

        UpUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                int mid = Integer.parseInt(mMid);
                mOnItemClickListener.onUpItemClick(mid);
            }
        }
    }

    public interface OnItemClickListener {
        void onUpItemClick(int mid);
    }
}
