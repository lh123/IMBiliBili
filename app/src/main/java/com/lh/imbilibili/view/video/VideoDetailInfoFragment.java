package com.lh.imbilibili.view.video;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.video.VideoDetail;
import com.lh.imbilibili.utils.RxBus;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.SubscriptionUtils;
import com.lh.imbilibili.utils.transformation.CircleTransformation;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.FlowItemDecoration;
import com.lh.imbilibili.view.adapter.videodetail.RelatesVideoItemDecoration;
import com.lh.imbilibili.view.adapter.videodetail.VideoPageRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.videodetail.VideoRelatesRecyclerViewAdapter;
import com.lh.imbilibili.view.adapter.videodetail.VideoTagAdapter;
import com.lh.imbilibili.view.search.SearchActivity;
import com.lh.imbilibili.view.usercenter.UserCenterActivity;
import com.lh.imbilibili.widget.layoutmanager.FlowLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by liuhui on 2016/10/2.
 * 视频介绍界面
 */

public class VideoDetailInfoFragment extends BaseFragment implements VideoPageRecyclerViewAdapter.OnPageClickListener, VideoRelatesRecyclerViewAdapter.OnVideoItemClickListener {

    public static final String EXTRA_DATA = "videoDetail";

    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_play_count)
    TextView mTvPlayCount;
    @BindView(R.id.tv_danmakus)
    TextView mTvDanmakus;
    @BindView(R.id.tv_description)
    TextView mTvDescription;
    @BindView(R.id.author_tag)
    RecyclerView mFlowLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.owner_layout)
    ViewGroup mOwnerLayout;
    @BindView(R.id.iv_author_face)
    ImageView mIvAuthorFace;
    @BindView(R.id.tv_author_name)
    TextView mTvAuthorName;
    @BindView(R.id.tv_pub_time)
    TextView mTvPubTime;
    @BindView(R.id.page_layout)
    View mPageLayout;
    @BindView(R.id.tv_page_count)
    TextView mTvPageCount;
    @BindView(R.id.page_recycler_view)
    RecyclerView mPageRecyclerView;

    private VideoDetail mVideoDetail;
    private VideoRelatesRecyclerViewAdapter mAdapter;
    private VideoPageRecyclerViewAdapter mVideoPageAdapter;
    private Subscription mBusSub;

    public static VideoDetailInfoFragment newInstance() {
        return new VideoDetailInfoFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
//        mFlowLayout.setOnItemClickListener(this);
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBusSub = RxBus.getInstance()
                .toObserverable(VideoStateChangeEvent.class)
                .subscribe(new Action1<VideoStateChangeEvent>() {
                    @Override
                    public void call(VideoStateChangeEvent videoStateChangeEvent) {
                        switch (videoStateChangeEvent.state) {
                            case VideoStateChangeEvent.STATE_LOAD_FINISH:
                                mVideoDetail = videoStateChangeEvent.videoDetail;
                                bindViewWithData();
                                break;
                            case VideoStateChangeEvent.STATE_PLAY:
//                                System.out.println("setNestedScrollingEnabled:false");
//                                mScrollView.setNestedScrollingEnabled(false);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionUtils.unsubscribe(mBusSub);
    }

    private void initRecyclerView() {
        mAdapter = new VideoRelatesRecyclerViewAdapter(getContext());
        mVideoPageAdapter = new VideoPageRecyclerViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RelatesVideoItemDecoration itemDecoration = new RelatesVideoItemDecoration(getContext());
        linearLayoutManager.setAutoMeasureEnabled(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnVideoItemClickListener(this);
        mPageRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager pageLayoutManager = new LinearLayoutManager(getContext());
        pageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPageRecyclerView.setLayoutManager(pageLayoutManager);
        mPageRecyclerView.setAdapter(mVideoPageAdapter);
        mVideoPageAdapter.setOnPageClickListener(this);
    }

    private void bindViewWithData() {
        mTvTitle.setText(mVideoDetail.getTitle());
        mTvDanmakus.setText(StringUtils.formateNumber(mVideoDetail.getStat().getDanmaku()));
        mTvPlayCount.setText(StringUtils.formateNumber(mVideoDetail.getStat().getView()));
        mTvDescription.setText(mVideoDetail.getDesc());
        Glide.with(this).load(mVideoDetail.getOwner().getFace()).asBitmap().transform(new CircleTransformation(getContext().getApplicationContext())).into(mIvAuthorFace);
        mTvAuthorName.setText(mVideoDetail.getOwner().getName());
        mTvPubTime.setText(StringUtils.formateDateRelative(mVideoDetail.getPubdate()));
        mOwnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCenterActivity.startActivity(getContext(), mVideoDetail.getOwner().getMid(), 1);
            }
        });
        if (mVideoDetail.getPages().size() > 1) {
            mPageLayout.setVisibility(View.VISIBLE);
            mTvPageCount.setText(StringUtils.format("分集(%d)", mVideoDetail.getPages().size()));
            mVideoPageAdapter.setData(mVideoDetail.getPages());
        } else {
            mPageLayout.setVisibility(View.GONE);
        }
        if (mVideoDetail.getTags() != null) {
            VideoTagAdapter tagAdapter = new VideoTagAdapter();
            tagAdapter.setTags(mVideoDetail.getTags());
            mFlowLayout.addItemDecoration(new FlowItemDecoration(getResources().getDimensionPixelSize(R.dimen.item_half_spacing)));
            FlowLayoutManager layoutManager = new FlowLayoutManager();
            mFlowLayout.setLayoutManager(layoutManager);
            mFlowLayout.setAdapter(tagAdapter);
            tagAdapter.setOnTagClickListener(new VideoTagAdapter.OnTagClickListener() {
                @Override
                public void onTagClick(String tagContent) {
                    SearchActivity.startActivity(getContext(), tagContent);
                }
            });
        }
        mAdapter.setVideoDetails(mVideoDetail.getRelates());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_video_detail_info;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public String getTitle() {
        return "简介";
    }

    public void onItemClick(ViewGroup parent, int position, View view) {
        SearchActivity.startActivity(getContext(), mVideoDetail.getTags()[position]);
    }

    @Override
    public void onPageClick(int position) {
        VideoDetailActivity activity = (VideoDetailActivity) getActivity();
        activity.changeVideoPage(position);
    }

    @Override
    public void onItemClick(int position) {
        VideoDetailActivity.startActivity(getContext(), mVideoDetail.getRelates().get(position).getAid());
    }
}
