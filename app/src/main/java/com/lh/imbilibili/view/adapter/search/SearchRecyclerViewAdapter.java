package com.lh.imbilibili.view.adapter.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.search.Archive;
import com.lh.imbilibili.model.search.Movie;
import com.lh.imbilibili.model.search.SearchResult;
import com.lh.imbilibili.model.search.Season;
import com.lh.imbilibili.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/5.
 * 搜索主界面Adapter
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter {

    public static final int TYPE_SEASON = 1;
    public static final int TYPE_SEASON_MORE = 2;
    public static final int TYPE_MOVIE = 3;
    public static final int TYPE_VIDEO = 4;

    private SearchResult mSearchResult;
    private int mTotalBangumiSize;

    private OnSearchItemClickListener mOnItemClickListener;
    private Context mContext;

    public SearchRecyclerViewAdapter(Context context, SearchResult searchResult) {
        mContext = context;
        mSearchResult = searchResult;
        mTotalBangumiSize = searchResult.getNav().get(0).getTotal();
    }

    public void setSearchResult(SearchResult searchResult) {
        mSearchResult = searchResult;
        mTotalBangumiSize = searchResult.getNav().get(0).getTotal();
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addData(List<Archive> archives) {
        mSearchResult.getItems().getArchive().addAll(archives);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_SEASON) {
            View view = inflater.inflate(R.layout.search_result_bangumi_item, parent, false);
            viewHolder = new BangumiViewHolder(view);
        } else if (viewType == TYPE_SEASON_MORE) {
            TextView view = new TextView(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.getResources().getDimensionPixelSize(R.dimen.text_size_medium));
            view.setLayoutParams(params);
            viewHolder = new MoreViewHolder(view);
        } else if (viewType == TYPE_MOVIE) {
            View view = inflater.inflate(R.layout.search_result_movie_item, parent, false);
            viewHolder = new MovieViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.video_list_item, parent, false);
            viewHolder = new VideoViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_SEASON) {
            BangumiViewHolder bangumiViewHolder = (BangumiViewHolder) holder;
            Season season = mSearchResult.getItems().getSeason().get(position);
            bangumiViewHolder.mParam = season.getParam();
            Glide.with(mContext).load(season.getCover()).into(bangumiViewHolder.mIvCover);
            bangumiViewHolder.mTvTitle.setText(season.getTitle());
            if (season.getFinish() == 1) {
                bangumiViewHolder.mTvNewestEp.setText(StringUtils.format("%s，%d话全", season.getNewestSeason(), season.getTotalCount()));
            } else {
                bangumiViewHolder.mTvNewestEp.setText(StringUtils.format("%s，更新至第%s话", season.getNewestSeason(), season.getIndex()));
            }
            bangumiViewHolder.mTvCatDesc.setText(season.getCatDesc());
        } else if (type == TYPE_SEASON_MORE) {
            MoreViewHolder moreViewHolder = (MoreViewHolder) holder;
            moreViewHolder.mTvMore.setText(StringUtils.format("更多番剧(%d)>>", mTotalBangumiSize));
        } else if (type == TYPE_MOVIE) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
            int partPosition = position - mSearchResult.getItems().getSeason().size();
            if (mTotalBangumiSize > mSearchResult.getItems().getSeason().size()) {
                partPosition--;
            }
            Movie movie = mSearchResult.getItems().getMovie().get(partPosition);
            Glide.with(mContext).load(movie.getCover()).into(movieViewHolder.mIvCover);
            movieViewHolder.mTvBadge.setText(movie.getCoverMark());
            movieViewHolder.mTvDuration.setText(StringUtils.format("%d分钟", movie.getLength()));
            movieViewHolder.mTvTitle.setText(movie.getTitle());
            movieViewHolder.mTvArea.setText(movie.getArea());
            String[] splitYear = movie.getScreenDate().split("-");
            if (splitYear.length > 0) {
                movieViewHolder.mTvYear.setText(splitYear[0]);
            }
            String[] splitDir = movie.getStaff().split("\\n");
            if (splitDir.length == 2) {
                movieViewHolder.mTvDirector.setText(splitDir[0]);
                movieViewHolder.mTvActor.setText(splitDir[1]);
            }
        } else if (type == TYPE_VIDEO) {
            VideoViewHolder videoHolder = (VideoViewHolder) holder;
            int partPosition = position - mSearchResult.getItems().getSeason().size() - mSearchResult.getItems().getMovie().size();
            if (mTotalBangumiSize > mSearchResult.getItems().getSeason().size()) {
                partPosition--;
            }
            Archive video = mSearchResult.getItems().getArchive().get(partPosition);
            videoHolder.mParam = video.getParam();
            Glide.with(mContext).load(video.getCover()).into(videoHolder.mIvCover);
            videoHolder.mTvTitle.setText(video.getTitle());
            videoHolder.mTvAuthor.setText(video.getAuthor());
            videoHolder.mTvInfoViews.setText(StringUtils.formateNumber(video.getPlay()));
            videoHolder.mTvInfoDanmakus.setText(StringUtils.formateNumber(video.getDanmaku()));
            videoHolder.mTvPayBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        SearchResult.Item item = mSearchResult.getItems();
        int totalSize = item.getSeason().size() + item.getMovie().size() + item.getArchive().size();
        if (mTotalBangumiSize > item.getSeason().size()) {
            totalSize++;
        }
        return totalSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTotalBangumiSize > mSearchResult.getItems().getSeason().size()) {
            if (position < mSearchResult.getItems().getSeason().size()) {
                return TYPE_SEASON;
            } else if (position == mSearchResult.getItems().getSeason().size()) {
                return TYPE_SEASON_MORE;
            } else if (position < mSearchResult.getItems().getSeason().size() + mSearchResult.getItems().getMovie().size() + 1) {
                return TYPE_MOVIE;
            } else {
                return TYPE_VIDEO;
            }
        } else {
            if (position < mSearchResult.getItems().getSeason().size()) {
                return TYPE_SEASON;
            } else if (position < mSearchResult.getItems().getSeason().size() + mSearchResult.getItems().getMovie().size()) {
                return TYPE_MOVIE;
            } else {
                return TYPE_VIDEO;
            }
        }
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
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onSearchItemClick(mParam, TYPE_SEASON);
            }
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.badge)
        TextView mTvBadge;
        @BindView(R.id.duration)
        TextView mTvDuration;
        @BindView(R.id.type)
        TextView mTvType;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.year)
        TextView mTvYear;
        @BindView(R.id.area)
        TextView mTvArea;
        @BindView(R.id.director)
        TextView mTvDirector;
        @BindView(R.id.actor)
        TextView mTvActor;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvMore;

        MoreViewHolder(View itemView) {
            super(itemView);
            mTvMore = (TextView) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onSearchItemClick(null, TYPE_SEASON_MORE);
            }
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.pay_badge)
        TextView mTvPayBadge;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.author)
        TextView mTvAuthor;
        @BindView(R.id.info_views)
        TextView mTvInfoViews;
        @BindView(R.id.info_danmakus)
        TextView mTvInfoDanmakus;
        @BindView(R.id.text2)
        TextView mTvSecond;

        private String mParam;

        VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int tintColor = ContextCompat.getColor(mContext, R.color.gray_dark);
            Drawable drawableCompat = DrawableCompat.wrap(mTvInfoViews.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            drawableCompat = DrawableCompat.wrap(mTvInfoDanmakus.getCompoundDrawables()[0]);
            DrawableCompat.setTint(drawableCompat, tintColor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onSearchItemClick(mParam, TYPE_VIDEO);
            }
        }
    }

    public interface OnSearchItemClickListener {
        void onSearchItemClick(String param, int type);
    }
}
