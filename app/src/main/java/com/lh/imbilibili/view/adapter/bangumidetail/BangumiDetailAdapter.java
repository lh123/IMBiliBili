package com.lh.imbilibili.view.adapter.bangumidetail;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.Bangumi;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.bangumi.Tag;
import com.lh.imbilibili.model.feedback.Feedback;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.BlurTransformation;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;
import com.lh.imbilibili.utils.transformation.TopCropTransformation;
import com.lh.imbilibili.view.adapter.GridLayoutItemDecoration;
import com.lh.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.lh.imbilibili.widget.FeedbackView;
import com.lh.imbilibili.widget.ScalableImageView;
import com.lh.imbilibili.widget.layoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/31.
 * 番剧详情Adapter
 */

public class BangumiDetailAdapter extends RecyclerView.Adapter {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_SEASON_LIST = 2;
    public static final int TYPE_EPOSIDE = 3;
    public static final int TYPE_DESC = 4;
    public static final int TYPE_RECOMMEND = 5;
    public static final int TYPE_FEEDBACK_HEAD = 6;
    public static final int TYPE_HOT_FEEDBACK = 7;
    public static final int TYPE_HOT_FEEDBACK_MORE = 8;
    public static final int TYPE_NORMAL_FEEDBACK = 9;

    private int mEpSelectPosition;
    private int mReplyCount;

    private BangumiDetail mBangumiDetail;
    private List<Bangumi> mSeasonRecommends;
    private List<Feedback> mHotFeedbacks;
    private List<Feedback> mFeedbacks;

    private Context mContext;

    private List<Integer> mTypeList;

    private OnItemClickListener mOnItemClickListener;

    public BangumiDetailAdapter(Context context) {
        mContext = context;
        mTypeList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setBangumiDetail(BangumiDetail bangumiDetail) {
        mBangumiDetail = bangumiDetail;
    }

    public void setSeasonRecommend(List<Bangumi> seasonRecommends) {
        mSeasonRecommends = seasonRecommends;
    }

    public void setHotFeedbacks(List<Feedback> hotFeedbacks) {
        mHotFeedbacks = hotFeedbacks;
    }

    public int getEpSelectPosition() {
        return mEpSelectPosition;
    }

    public void addFeedBack(List<Feedback> feedbacks) {
        if (mFeedbacks == null) {
            mFeedbacks = feedbacks;
        } else {
            mFeedbacks.addAll(feedbacks);
        }
    }

    public void clearAllData() {
        mBangumiDetail = null;
        mSeasonRecommends = null;
        mEpSelectPosition = 0;
        mReplyCount = 0;
        clearFeedback();
    }

    public int[] clearFeedback() {
        int[] result = new int[2];
        if (mHotFeedbacks != null) {
            mHotFeedbacks.clear();
        }
        if (mFeedbacks != null) {
            mFeedbacks.clear();
        }
        result[0] = mTypeList.indexOf(TYPE_HOT_FEEDBACK);
        result[1] = mTypeList.lastIndexOf(TYPE_NORMAL_FEEDBACK) - mTypeList.indexOf(TYPE_HOT_FEEDBACK) + 1;
        return result;
    }

    public void setReplyCount(int count) {
        mReplyCount = count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.bangumi_detail_header_item, parent, false);
            holder = new HeadViewHolder(view);
        } else if (viewType == TYPE_SEASON_LIST) {
            View view = inflater.inflate(R.layout.bangumi_detail_season_list_item, parent, false);
            holder = new SeasonListViewHolder(view);
        } else if (viewType == TYPE_EPOSIDE) {
            View view = inflater.inflate(R.layout.bangumi_detail_eposide_item, parent, false);
            holder = new EposideViewHolder(view);
        } else if (viewType == TYPE_DESC) {
            View view = inflater.inflate(R.layout.bangumi_detail_desc_item, parent, false);
            holder = new DescViewHolder(view);
        } else if (viewType == TYPE_RECOMMEND) {
            View view = inflater.inflate(R.layout.bangumi_detail_recommond_item, parent, false);
            holder = new RecommendViewHolder(view);
        } else if (viewType == TYPE_FEEDBACK_HEAD) {
            View view = inflater.inflate(R.layout.bangumi_detail_common_head, parent, false);
            holder = new FeedbackHeadHolder(view);
        } else if (viewType == TYPE_HOT_FEEDBACK || viewType == TYPE_NORMAL_FEEDBACK) {
            View view = inflater.inflate(R.layout.feedback_item, parent, false);
            holder = new FeedbackViewHolder(view);
        } else if (viewType == TYPE_HOT_FEEDBACK_MORE) {
            View view = inflater.inflate(R.layout.bangumi_season_comment_footer, parent, false);
            holder = new HotFeedbackFooterHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = holder.getItemViewType();
        if (itemType == TYPE_HEADER) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            if (mBangumiDetail != null) {
                Glide.with(mContext)
                        .load(mBangumiDetail.getCover())
                        .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), 5))
                        .into(headViewHolder.mIvCover);
                Glide.with(mContext)
                        .load(mBangumiDetail.getCover())
                        .transform(new TopCropTransformation(mContext.getApplicationContext()), new BlurTransformation(mContext.getApplicationContext(), 20))
                        .into(headViewHolder.mIvBackground);
                headViewHolder.mTvTitle.setText(mBangumiDetail.getTitle());
                if ("0".equals(mBangumiDetail.getIsFinish())) {
                    headViewHolder.mTvText1.setText(StringUtils.format("连载中，每周%s更新", StringUtils.str2Weekday(mBangumiDetail.getWeekday())));
                } else {
                    headViewHolder.mTvText1.setText(StringUtils.format("已完结，%s话全", mBangumiDetail.getTotalCount()));
                }
                headViewHolder.mTvText2.setText(StringUtils.format("播放：%s", StringUtils.formateNumber(mBangumiDetail.getPlayCount())));
                headViewHolder.mTvText3.setText(StringUtils.format("追番：%s", StringUtils.formateNumber(mBangumiDetail.getFavorites())));
            }
        } else if (itemType == TYPE_SEASON_LIST) {
            SeasonListViewHolder viewHolder = (SeasonListViewHolder) holder;
            viewHolder.refreshData(mBangumiDetail.getSeasons());
        } else if (itemType == TYPE_EPOSIDE) {
            EposideViewHolder viewHolder = (EposideViewHolder) holder;
            viewHolder.refreshData(mBangumiDetail.getEpisodes());
        } else if (itemType == TYPE_DESC) {
            DescViewHolder viewHolder = (DescViewHolder) holder;
            viewHolder.mTvTitle.setText("简介");
            viewHolder.mTvSubTitle.setText("更多");
            viewHolder.mTvDesc.setText(mBangumiDetail.getEvaluate());
            viewHolder.refreshData(mBangumiDetail.getTags());
        } else if (itemType == TYPE_RECOMMEND) {
            RecommendViewHolder viewHolder = (RecommendViewHolder) holder;
            viewHolder.refreshData(mSeasonRecommends);
        } else if (itemType == TYPE_FEEDBACK_HEAD) {
            FeedbackHeadHolder headHolder = (FeedbackHeadHolder) holder;
            headHolder.mTvAdditionInfo.setVisibility(View.VISIBLE);
            headHolder.mTvTitle.setText(StringUtils.format("评论 第%s话", mBangumiDetail.getEpisodes().get(mEpSelectPosition).getIndex()));
            headHolder.mTvAdditionInfo.setText(StringUtils.format("(%d)", mReplyCount));
            headHolder.mTvSubTitle.setText("选集");
        } else if (itemType == TYPE_HOT_FEEDBACK) {
            FeedbackViewHolder viewHolder = (FeedbackViewHolder) holder;
            Feedback feedback = mHotFeedbacks.get(position - mTypeList.indexOf(TYPE_HOT_FEEDBACK));
            viewHolder.mFeedbackView.setData(feedback, false);
            if (position == mTypeList.lastIndexOf(TYPE_HOT_FEEDBACK)) {
                viewHolder.mDivider.setVisibility(View.GONE);
            } else {
                viewHolder.mDivider.setVisibility(View.VISIBLE);
            }
        } else if (itemType == TYPE_NORMAL_FEEDBACK) {
            FeedbackViewHolder viewHolder = (FeedbackViewHolder) holder;
            Feedback feedback = mFeedbacks.get(position - mTypeList.indexOf(TYPE_NORMAL_FEEDBACK));
            viewHolder.mFeedbackView.setData(feedback, true);
        }
    }

    @Override
    public int getItemCount() {
        mTypeList.clear();
        mTypeList.add(TYPE_HEADER);
        if (mBangumiDetail == null) {
            return mTypeList.size();
        }
        if (mBangumiDetail.getSeasons() != null && mBangumiDetail.getSeasons().size() > 1) {
            mTypeList.add(TYPE_SEASON_LIST);
        }
        mTypeList.add(TYPE_EPOSIDE);
        mTypeList.add(TYPE_DESC);
        if (mSeasonRecommends != null && mSeasonRecommends.size() > 0) {
            mTypeList.add(TYPE_RECOMMEND);
        }
        mTypeList.add(TYPE_FEEDBACK_HEAD);
        if ((mHotFeedbacks != null && !mHotFeedbacks.isEmpty())) {
            if (mHotFeedbacks != null) {
                int count = Math.min(mHotFeedbacks.size(), 3);
                for (int i = 0; i < count; i++) {
                    mTypeList.add(TYPE_HOT_FEEDBACK);
                }
                if (mHotFeedbacks.size() >= 3) {
                    mTypeList.add(TYPE_HOT_FEEDBACK_MORE);
                }
            }
        }
        if (mFeedbacks != null) {
            for (int i = 0; i < mFeedbacks.size(); i++) {
                mTypeList.add(TYPE_NORMAL_FEEDBACK);
            }
        }
        return mTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeList.get(position);
    }

    @SuppressWarnings("WeakerAccess")
    class HeadViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ScalableImageView mIvCover;
        @BindView(R.id.background)
        ScalableImageView mIvBackground;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.text1)
        TextView mTvText1;
        @BindView(R.id.text2)
        TextView mTvText2;
        @BindView(R.id.text3)
        TextView mTvText3;

        public HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class SeasonListViewHolder extends RecyclerView.ViewHolder implements SeasonListAdapter.OnSeasonItemClickListener {

        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private List<Bangumi> mSeasons;
        private SeasonListAdapter mAdapter;

        public SeasonListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mAdapter = new SeasonListAdapter(mContext);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnSeasonItemClickListener(this);
        }

        private void refreshData(List<Bangumi> seasons) {
            if (mSeasons == seasons) {
                return;
            }
            mSeasons = seasons;
            mAdapter.setSeasons(mSeasons);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSeasonItemClick(int position) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getItemViewType(), position);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class EposideViewHolder extends RecyclerView.ViewHolder implements BangumiEpAdapter.OnEpClickListener {

        @BindView(R.id.head_container)
        ViewGroup mHeadContainer;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.sub_title)
        TextView mTvSubTitle;
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private BangumiEpAdapter mAdapter;
        private List<BangumiDetail.Episode> mEpisodes;

        public EposideViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mAdapter = new BangumiEpAdapter();
            mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(mContext, LinearLayoutManager.HORIZONTAL));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setNestedScrollingEnabled(false);
            mAdapter.setOnEpClickListener(this);
        }

        private void refreshData(List<BangumiDetail.Episode> episodes) {
            if (mEpisodes == episodes) {
                return;
            }
            mTvTitle.setText("选集");
            if ("1".equals(mBangumiDetail.getIsFinish())) {
                mTvSubTitle.setText(StringUtils.format("%s话全", mBangumiDetail.getTotalCount()));
            } else {
                mTvSubTitle.setText(StringUtils.format("更新至第 %s 话", mBangumiDetail.getNewestEpIndex()));
            }
            mEpisodes = episodes;
            mAdapter.setEpisodes(mEpisodes);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onEpClick(int position) {
            mEpSelectPosition = position;
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getItemViewType(), position);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class DescViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head_container)
        ViewGroup mHeadContainer;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.sub_title)
        TextView mTvSubTitle;
        @BindView(R.id.description)
        TextView mTvDesc;
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private BangumiTagAdapter mAdapter;
        private List<Tag> mBangumiTags;

        public DescViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mAdapter = new BangumiTagAdapter();
            FlowLayoutManager layoutManager = new FlowLayoutManager();
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new TagItemDecoration(mContext));
            mRecyclerView.setAdapter(mAdapter);
        }

        private void refreshData(List<Tag> bangumiTags) {
            if (mBangumiTags == bangumiTags) {
                return;
            }
            mBangumiTags = bangumiTags;
            mAdapter.setBangumiTags(mBangumiTags);
            mAdapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings("WeakerAccess")
    class RecommendViewHolder extends RecyclerView.ViewHolder implements BangumiRecommendAdapter.OnBangumiRecommendItemClickListener {

        @BindView(R.id.head_container)
        ViewGroup mHeadContainer;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.sub_title)
        TextView mTvSubTitle;
        @BindView(R.id.sub_title_ico)
        ImageView mIvSubTitleIco;
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private BangumiRecommendAdapter mAdapter;
        private List<Bangumi> mBangumis;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mTvTitle.setText("番剧推荐");
            mTvSubTitle.setVisibility(View.GONE);
            mIvSubTitleIco.setVisibility(View.GONE);
            mAdapter = new BangumiRecommendAdapter(mContext, mSeasonRecommends);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
            mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(mContext, true));
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnBangumiRecommendItemClickListener(this);
        }

        private void refreshData(List<Bangumi> bangumis) {
            if (mBangumis == bangumis) {
                return;
            }
            mBangumis = bangumis;
            mAdapter.setBangumis(mBangumis);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onBangumiRecommendItemClick(int position) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getItemViewType(), position);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class FeedbackHeadHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head_container)
        ViewGroup mHeadContainer;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.addition_info)
        TextView mTvAdditionInfo;
        @BindView(R.id.sub_title)
        TextView mTvSubTitle;
        @BindView(R.id.sub_title_ico)
        ImageView mIvSubTitleIco;

        public FeedbackHeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class FeedbackViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feedback)
        FeedbackView mFeedbackView;
        @BindView(R.id.divider)
        View mDivider;

        public FeedbackViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class HotFeedbackFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView mTvTitle;

        public HotFeedbackFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int type, int position);
    }
}