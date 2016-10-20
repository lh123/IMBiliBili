package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.BangumiDetail;
import com.lh.imbilibili.utils.StatusBarUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.view.fragment.FeedbackFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by home on 2016/8/2.
 */
public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.feedback_container)
    FrameLayout feedbackContainer;
    private FeedbackFragment feedbackFragment;

    public static void startActivity(Context context, int position, BangumiDetail bangumiDetail) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("data", bangumiDetail);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        StatusBarUtils.setSimpleToolbarLayout(this, mToolbar);
        initToolbar();
        initFragment();
    }

    private void initToolbar() {
        mToolbar.setTitle("评论");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFragment() {
        feedbackFragment = (FeedbackFragment) getSupportFragmentManager().findFragmentByTag(FeedbackFragment.TAG);
        if (feedbackFragment == null) {
            feedbackFragment = FeedbackFragment.newInstance(getIntent().getExtras());
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.feedback_container, feedbackFragment, FeedbackFragment.TAG).commit();
    }
}
