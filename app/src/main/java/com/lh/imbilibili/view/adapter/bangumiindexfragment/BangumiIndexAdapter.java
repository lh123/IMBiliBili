package com.lh.imbilibili.view.adapter.bangumiindexfragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.Bangumi;
import com.lh.imbilibili.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/11.
 */
public class BangumiIndexAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Bangumi> mBangumis;

    private int mIndexSortType = 1;//0更新时间 1追番人数 3开播时间

    private OnBangumiItemClickListener mOnBangumiItemClickListener;

    public BangumiIndexAdapter(Context context) {
        mContext = context;
        mBangumis = new ArrayList<>();
    }

    public void addBangumis(List<Bangumi> bangumis) {
        mBangumis.addAll(bangumis);
    }

    public void clear() {
        int size = mBangumis.size();
        mBangumis.clear();
        notifyItemRangeRemoved(0,size);
    }

    public void setOnBangumiItemClickListener(OnBangumiItemClickListener l) {
        mOnBangumiItemClickListener = l;
    }

    public void setIndexSortType(int indexSortType) {
        mIndexSortType = indexSortType;
    }

    private String timeToStr(long time) {
        String str;
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(time * 1000);
        int tYear = calendar.get(Calendar.YEAR);
        int tMonth = calendar.get(Calendar.MONTH) + 1;
        int tDay = calendar.get(Calendar.DAY_OF_YEAR);
        int tDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int tHour = calendar.get(Calendar.HOUR_OF_DAY);
        int tMinute = calendar.get(Calendar.MINUTE);
        if (cYear == tYear) {
            if (cDay == tDay) {
                str = StringUtils.format("今天%02d:%02d", tHour, tMinute);
            } else if (cDay - tDay == 1) {
                str = StringUtils.format("昨天%02d:%02d", tHour, tMinute);
            } else {
                str = StringUtils.format("%d月%d日", tMonth, tDayOfMonth);
            }
        } else {
            str = StringUtils.format("%d年%d月", tYear % 100, tMonth);
        }
        return str;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_grid_item, parent, false);
        return new BangumiHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BangumiHolder bangumiHolder = (BangumiHolder) holder;
        bangumiHolder.tvFavourite.setVisibility(View.VISIBLE);
        Bangumi bangumi = mBangumis.get(position);
        Glide.with(mContext).load(bangumi.getCover()).into(bangumiHolder.ivCover);
        bangumiHolder.tvTitle.setText(bangumi.getTitle());
        bangumiHolder.tv1.setText(StringUtils.format("更新至第%s话", bangumi.getNewestEpIndex()));
        bangumiHolder.mSeasonId = bangumi.getSeasonId();
        if (mIndexSortType == 1) {
            bangumiHolder.tvFavourite.setText(StringUtils.format("%s人追番", StringUtils.formateNumber(bangumi.getFavorites())));
        } else if (mIndexSortType == 0) {
            bangumiHolder.tvFavourite.setText(StringUtils.format("%s更新", timeToStr(bangumi.getUpdateTime())));
        } else if (mIndexSortType == 2) {
            bangumiHolder.tvFavourite.setText(StringUtils.format("%s放送", timeToStr(bangumi.getPubTime())));
        }
    }

    @Override
    public int getItemCount() {
        if (mBangumis != null) {
            return mBangumis.size();
        } else {
            return 0;
        }
    }

    public interface OnBangumiItemClickListener {
        void onBangumiClick(BangumiHolder holder);
    }

    public class BangumiHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.feedback_container)
        ViewGroup container;
        @BindView(R.id.cover)
        ImageView ivCover;
        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.text1)
        TextView tv1;
        @BindView(R.id.favourites)
        TextView tvFavourite;
        private String mSeasonId;

        public BangumiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public String getSeasonId() {
            return mSeasonId;
        }

        @Override
        public void onClick(View v) {
            if (mOnBangumiItemClickListener != null) {
                mOnBangumiItemClickListener.onBangumiClick(this);
            }
        }
    }
}
