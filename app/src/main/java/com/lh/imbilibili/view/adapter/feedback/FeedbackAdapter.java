package com.lh.imbilibili.view.adapter.feedback;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.feedback.Feedback;
import com.lh.imbilibili.model.feedback.FeedbackData;
import com.lh.imbilibili.widget.FeedbackView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/2.
 */
public class FeedbackAdapter extends RecyclerView.Adapter {

    private static final int FEEDBACK_ITEM = 1;
    private static final int HOT_FEEDBACK_ITEM = 2;
    private static final int HOT_FEEDBACK_FOOTER = 3;

    private FeedbackData mFeedbackData;

    private OnFeedbackItemClickListener mOnFeedbackItemClickListener;

    public void setOnFeedbackItemClickListener(OnFeedbackItemClickListener listener) {
        mOnFeedbackItemClickListener = listener;
    }

    public void addFeedbackData(FeedbackData feedbackData) {
        if (mFeedbackData == null) {
            mFeedbackData = feedbackData;
        } else {
            mFeedbackData.getReplies().addAll(feedbackData.getReplies());
        }
    }

    public void clear() {
        mFeedbackData = null;
    }

    private void clickItem(int type, int position) {
        if (mOnFeedbackItemClickListener != null) {
            mOnFeedbackItemClickListener.onFeedbackItemClick(type, position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        if (viewType == FEEDBACK_ITEM || viewType == HOT_FEEDBACK_ITEM) {
            holder = new FeedbackHolder(new FeedbackView(parent.getContext()));
        } else if (viewType == HOT_FEEDBACK_FOOTER) {
            View view = inflater.inflate(R.layout.bangumi_season_comment_footer, parent, false);
            holder = new HotFeedbackFooterHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HOT_FEEDBACK_ITEM) {
            FeedbackHolder feedbackHolder = (FeedbackHolder) holder;
            Feedback feedback = mFeedbackData.getHots().get(position);
            feedbackHolder.feedbackView.setData(feedback, false);
        } else if (getItemViewType(position) == FEEDBACK_ITEM) {
            FeedbackHolder feedbackHolder = (FeedbackHolder) holder;
            Feedback feedback;
            if (mFeedbackData.getHots() != null && mFeedbackData.getHots().size() > 0) {
                feedback = mFeedbackData.getReplies().get(position - mFeedbackData.getHots().size());
            } else {
                feedback = mFeedbackData.getReplies().get(position);
            }
            feedbackHolder.feedbackView.setData(feedback, true);
        } else {
            HotFeedbackFooterHolder hotFeedbackFooterHolder = (HotFeedbackFooterHolder) holder;
            hotFeedbackFooterHolder.tvTitle.setText("更多热门评论>>");
        }
    }

    @Override
    public int getItemCount() {
        if (mFeedbackData == null || mFeedbackData.getReplies() == null) {
            return 0;
        } else {
            if (mFeedbackData.getHots() == null) {
                return mFeedbackData.getReplies().size();
            } else {
                return mFeedbackData.getReplies().size() + mFeedbackData.getHots().size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFeedbackData.getHots() != null && mFeedbackData.getHots().size() != 0) {
            if (position < mFeedbackData.getHots().size()) {
                return HOT_FEEDBACK_ITEM;
            } else if (position == mFeedbackData.getHots().size()) {
                return HOT_FEEDBACK_FOOTER;
            } else {
                return FEEDBACK_ITEM;
            }
        } else {
            return FEEDBACK_ITEM;
        }
    }

    public interface OnFeedbackItemClickListener {
        void onFeedbackItemClick(int type, int position);
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder {

        FeedbackView feedbackView;

        public FeedbackHolder(FeedbackView itemView) {
            super(itemView);
            feedbackView = itemView;
        }
    }

    public class HotFeedbackFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView tvTitle;

        public HotFeedbackFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(HOT_FEEDBACK_ITEM, getAdapterPosition());
                }
            });
        }
    }
}
