package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.feedback.Feedback;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/7/31.
 */
public class FeedbackView extends FrameLayout implements View.OnClickListener {
    @BindView(R.id.avatar)
    ScalableImageView ivAvatar;
    @BindView(R.id.level)
    ImageView ivLevel;
    @BindView(R.id.nick_name)
    TextView tvNickName;
    @BindView(R.id.menu)
    ImageView ivMenu;
    @BindView(R.id.rating)
    TextView tvRating;
    @BindView(R.id.comments)
    TextView tvComments;
    @BindView(R.id.floor)
    TextView tvFloor;
    @BindView(R.id.pub_time)
    TextView tvPubTime;
    @BindView(R.id.message)
    TextView tvMessage;

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
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_item_include, this, false);
        ButterKnife.bind(this, view);
        addView(view);
        ivMenu.setOnClickListener(this);
    }

    public void setData(Feedback feedback) {
        if (feedback.getMember() != null) {
            Glide.with(getContext()).load(feedback.getMember().getAvatar()).transform(new CircleTransformation(getContext())).into(ivAvatar);
            tvNickName.setText(feedback.getMember().getUname());
        }
        tvRating.setText(StringUtils.format("%d", feedback.getLike()));
        tvFloor.setText(StringUtils.format("#%d", feedback.getFloor()));
        tvComments.setText(StringUtils.format("%d", feedback.getRcount()));
        tvPubTime.setText(StringUtils.formateDateRelative(feedback.getCtime()));
        tvMessage.setText(feedback.getContent().getMessage());
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
            popupWindow.showAsDropDown(ivMenu);
        }
    }
}
