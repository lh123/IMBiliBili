package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.feedback.Feedback;
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/7/31.
 */
public class FeedbackView extends ForegroundLinearLayout implements View.OnClickListener {

    @BindView(R.id.avatar)
    ScalableImageView mIvAvatar;
    @BindView(R.id.level)
    ImageView mIvLevel;
    @BindView(R.id.nick_name)
    TextView mTvNickName;
    @BindView(R.id.menu)
    ImageView mIvMenu;
    @BindView(R.id.rating)
    TextView mTvRating;
    @BindView(R.id.comments)
    TextView mTvComments;
    @BindView(R.id.floor)
    TextView mTvFloor;
    @BindView(R.id.pub_time)
    TextView mTvPubTime;
    @BindView(R.id.message)
    TextView mTvMessage;

    LinearLayout mReplyContainer;

    public FeedbackView(Context context) {
        super(context);
        init(context);
    }

    public FeedbackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FeedbackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_item_include, this, false);
        ButterKnife.bind(this, view);
        mIvMenu.setOnClickListener(this);
        mReplyContainer = new LinearLayout(context);
        mReplyContainer.setBackgroundResource(R.drawable.ic_feedback_secomment_bg);
        mReplyContainer.setOrientation(LinearLayout.VERTICAL);
        @SuppressWarnings("ResourceType")
        ForegroundLinearLayout.LayoutParams params = new ForegroundLinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelOffset(R.dimen.item_spacing);
        params.leftMargin = DisplayUtils.dip2px(context, 52);
        params.topMargin = margin;
        params.bottomMargin = margin;
        mReplyContainer.setLayoutParams(params);
        addView(view);
        addView(mReplyContainer);
    }

    public void setData(Feedback feedback, boolean showReply) {
        if (feedback.getMember() != null) {
            Glide.with(getContext())
                    .load(feedback.getMember().getAvatar())
                    .asBitmap()
                    .transform(new CircleTransformation(getContext())).into(mIvAvatar);
            mTvNickName.setText(feedback.getMember().getUname());
        }
        mTvRating.setText(StringUtils.format("%d", feedback.getLike()));
        mTvFloor.setText(StringUtils.format("#%d", feedback.getFloor()));
        mTvComments.setText(StringUtils.format("%d", feedback.getRcount()));
        mTvPubTime.setText(StringUtils.formateDateRelative(feedback.getCtime()));
        mTvMessage.setText(feedback.getContent().getMessage());
        showReply = showReply && feedback.getReplies() != null && !feedback.getReplies().isEmpty();
        mReplyContainer.removeAllViews();
        if (showReply) {
            mReplyContainer.setVisibility(VISIBLE);
            for (int i = 0; i < feedback.getReplies().size(); i++) {
                Feedback reply = feedback.getReplies().get(i);
                addReply(reply, i != feedback.getReplies().size() - 1);
            }
        } else {
            mReplyContainer.setVisibility(GONE);
        }
    }

    private void addReply(Feedback reply, boolean haveDivider) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.feedback_list_item_reply, mReplyContainer, false);
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvPubTime = (TextView) view.findViewById(R.id.pub_time);
        TextView tvMessage = (TextView) view.findViewById(R.id.message);
        View divider = view.findViewById(R.id.divider);
        tvMessage.setText(reply.getContent().getMessage());
        tvName.setText(reply.getMember().getUname());
        tvPubTime.setText(StringUtils.formateDateRelative(reply.getCtime()));
        if (haveDivider) {
            divider.setVisibility(VISIBLE);
        } else {
            divider.setVisibility(GONE);
        }
        mReplyContainer.addView(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu) {
            PopupWindow popupWindow = new PopupWindow(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.txt_popup_view, this, false);
            popupWindow.setWidth(getMeasuredWidth() / 2);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(view);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.popup_round_shadow_bg));
            TextView tv = (TextView) view.findViewById(R.id.txt);
            tv.setText("举报");
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(mIvMenu);
        }
    }
}
