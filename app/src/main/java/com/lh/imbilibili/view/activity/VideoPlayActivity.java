package com.lh.imbilibili.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lh.danmakulibrary.BiliBiliDanmakuParser;
import com.lh.danmakulibrary.Danmaku;
import com.lh.danmakulibrary.DanmakuView;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.SourceData;
import com.lh.imbilibili.model.VideoPlayData;
import com.lh.imbilibili.utils.DanmakuUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.VideoUtils;
import com.lh.imbilibili.view.BaseActivity;
import com.lh.imbilibili.widget.media.VideoControlView;
import com.lh.imbilibili.widget.media.VideoPlayerView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by home on 2016/8/3.
 */
public class VideoPlayActivity extends BaseActivity implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnPlayControlListener, IMediaPlayer.OnPreparedListener {

    private static final int MSG_SYNC_NOW = 1;
    private static final int MSG_SYNC_AT_TIME = 2;

    private static final String DANMAKU_ERROR = "DanmakuError";
    private static final String VIDEO_URL_ERROR = "VideoURLError";

    @BindView(R.id.pre_play_msg)
    TextView mPrePlayMsg;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_buffering)
    TextView mTvBuffering;
    @BindView(R.id.videoview)
    VideoPlayerView mIjkVideoView;
    @BindView(R.id.video_control_view)
    VideoControlView mVideoControlView;
    @BindView(R.id.danmaku_view)
    DanmakuView mDanmakuView;

    private String mAid;
    private String mCid;

    private String mTitle;

    private ArrayList<Danmaku> mDanmakus;

    private VideoHandler mHandler;

    private boolean mResumePlay = false;
    private boolean mIsFirstLoadVideo = true;

    private int mPrePlayerPosition;
    private int mCurrentQuality = 3;
    private long firstBackPressTime = -1;

    private StringBuilder mPreMsgBuilder;

    private boolean mIsVideoUrlSuccess;
    private boolean mIsDanmakuSuccess;

    public static void startVideoActivity(Context context, String aid, String cid, String title) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("aid", aid);
        intent.putExtra("cid", cid);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        mHandler = new VideoHandler(new WeakReference<>(this));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ButterKnife.bind(this);
        mAid = getIntent().getStringExtra("aid");
        mCid = getIntent().getStringExtra("cid");
        mTitle = getIntent().getStringExtra("title");
        mPreMsgBuilder = new StringBuilder();
        initIjkPlayer();
        mVideoControlView.setVideoView(mIjkVideoView);
        preparePlay();
    }

    private void initIjkPlayer() {
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mVideoControlView.setVideoTitle(mTitle);
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mVideoControlView.setOnPlayControlListener(this);
    }

    private void preparePlay() {
        mIsVideoUrlSuccess = false;
        mIsDanmakuSuccess = false;
        mPreMsgBuilder.append("【完成】\n解析视频信息...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        RetrofitHelper.getInstance()
                .getBangumiService()
                .getSource(mAid, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<BiliBiliResultResponse<List<SourceData>>, Observable<?>>() {
                    @Override
                    public Observable<?> call(BiliBiliResultResponse<List<SourceData>> listBiliBiliResultResponse) {
                        if (listBiliBiliResultResponse.isSuccess()) {
                            SourceData sourceData = listBiliBiliResultResponse.getResult().get(0);
                            mCid = sourceData.getCid();
                            mPreMsgBuilder.append("【完成】\n" +
                                    "解析视频地址...\n" +
                                    "全舰弹幕填装...");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            return Observable.mergeDelayError(loadVideoInfo(), DanmakuUtils.downLoadDanmaku(mCid));
                        } else {
                            mPreMsgBuilder.append("【失败】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            return Observable.error(new ApiException(listBiliBiliResultResponse.getCode()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        startPlaying();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!mIsDanmakuSuccess) {
                            String target = "全舰弹幕填装...";
                            int startPosition = mPreMsgBuilder.indexOf(target);
                            mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            startPlaying();
                        } else if (!mIsVideoUrlSuccess) {
                            String target = "解析视频地址...";
                            int startPosition = mPreMsgBuilder.indexOf(target);
                            mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                        }
                        if (mIsVideoUrlSuccess) {
                            startPlaying();
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o instanceof InputStream) {
                            String target = "全舰弹幕填装...";
                            int startPosition = mPreMsgBuilder.indexOf(target);
                            mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            preparDanmaku((InputStream) o);
                            mIsDanmakuSuccess = true;
                        } else if (o instanceof String) {
                            String target = "解析视频地址...";
                            int startPosition = mPreMsgBuilder.indexOf(target);
                            mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            mIjkVideoView.setVideoPath((String) o);
                            mVideoControlView.setCurrentVideoQuality(mCurrentQuality);
                            mIsVideoUrlSuccess = true;
                        }
                    }
                });
    }

    private Observable<String> loadVideoInfo() {
        return RetrofitHelper.getInstance()
                .getVideoPlayService()
                .getPlayData(Constant.BUILD, Constant.PLATFORM, mAid, 0, 0, 0, mCid, mCurrentQuality, "json")
                .flatMap(new Func1<VideoPlayData, Observable<String>>() {
                    @Override
                    public Observable<String> call(VideoPlayData videoPlayData) {
                        if (videoPlayData.getDurl() != null && !videoPlayData.getDurl().isEmpty()) {
                            return VideoUtils.concatVideo(getApplicationContext(), videoPlayData.getDurl());
                        } else {
                            return Observable.just(DANMAKU_ERROR);
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private void preparDanmaku(InputStream stream) {
        if (mDanmakuView != null) {
            BiliBiliDanmakuParser mParser = new BiliBiliDanmakuParser();
            try {
                mDanmakus = mParser.parse(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDanmakus != null) {
                mDanmakuView.setDanmakuSource(mDanmakus);
            }
        }
    }

    private void startPlaying() {
        mPreMsgBuilder.append("\n开始缓冲...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mHandler.removeMessages(MSG_SYNC_AT_TIME);
        if (mPrePlayerPosition != 0) {
            mIjkVideoView.seekTo(mPrePlayerPosition);
            mPrePlayerPosition = 0;
        }
        mIjkVideoView.start();
        mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mResumePlay) {
            mIjkVideoView.resume();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
            mDanmakuView.resume();
            mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIjkVideoView.isPlaying()) {
            mResumePlay = true;
            mIjkVideoView.pause();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
            mHandler.removeMessages(MSG_SYNC_NOW);
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mIjkVideoView.release();
        if (mDanmakuView != null) {
            mDanmakuView.stop();
            mDanmakuView = null;
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                mProgressBar.setVisibility(View.GONE);
                mTvBuffering.setVisibility(View.GONE);
                mPrePlayMsg.setVisibility(View.GONE);
                mDanmakuView.start();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                mHandler.removeMessages(MSG_SYNC_AT_TIME);
                mHandler.removeMessages(MSG_SYNC_NOW);
                mDanmakuView.pause();
                mTvBuffering.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                mHandler.removeMessages(MSG_SYNC_AT_TIME);
                mDanmakuView.resume();
                mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
                mProgressBar.setVisibility(View.GONE);
                mTvBuffering.setVisibility(View.GONE);
                mPrePlayMsg.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
//        startPlaying();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (firstBackPressTime < -1) {
            ToastUtils.showToast(this, "再按一次退出", Toast.LENGTH_SHORT);
            firstBackPressTime = System.currentTimeMillis();
        } else {
            long secondBackPressTime = System.currentTimeMillis();
            if (secondBackPressTime - firstBackPressTime < 2000) {
                super.onBackPressed();
                if (mDanmakuView != null) {
                    mDanmakuView.release();
                    mDanmakuView = null;
                }
            } else {
                ToastUtils.showToast(this, "再按一次退出", Toast.LENGTH_SHORT);
                firstBackPressTime = secondBackPressTime;
            }
        }
    }

    @Override
    public void onQualitySelect(int quality) {
        mCurrentQuality = quality;
        mPrePlayerPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
        mIjkVideoView.release();
        preparePlay();
    }

    @Override
    public void onDanamkuShowOrHideClick() {
        if (mDanmakuView.isShow()) {
            mDanmakuView.hide();
        } else {
            mDanmakuView.show();
        }
    }

    //doNothing
    @Override
    public void onFullScreenClick() {
    }

    @Override
    public void onVideoPause() {
        mDanmakuView.pause();
    }

    @Override
    public void onVideoStart() {
        mHandler.removeMessages(MSG_SYNC_NOW);
        if (mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        mHandler.sendEmptyMessage(MSG_SYNC_NOW);
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (!mIsFirstLoadVideo) {
            mHandler.removeMessages(MSG_SYNC_NOW);
            mIjkVideoView.seekTo(mPrePlayerPosition);
            mHandler.sendEmptyMessage(MSG_SYNC_NOW);
        }
    }

    private static class VideoHandler extends Handler {

        private WeakReference<VideoPlayActivity> mActivity;

        private VideoHandler(WeakReference<VideoPlayActivity> mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SYNC_NOW:
                    if (mActivity.get().mDanmakuView.isPrepared()) {
                        long videoCurrentPosition = mActivity.get().mIjkVideoView.getCurrentPosition();
                        long danmakuCurrentPosition = mActivity.get().mDanmakuView.getCurrentTime();
                        long diff = danmakuCurrentPosition - videoCurrentPosition;
                        if (diff > 500 && diff < 1000) {
                            mActivity.get().mDanmakuView.pause();
                            sendEmptyMessageDelayed(MSG_SYNC_NOW, diff);
                        } else if (diff > 1000 || diff < -500) {
                            mActivity.get().mDanmakuView.seekTo(videoCurrentPosition);
                            sendEmptyMessage(MSG_SYNC_NOW);
                        } else {
                            if (mActivity.get().mIjkVideoView.isPlaying()) {
                                mActivity.get().mDanmakuView.resume();
                            }
                        }
                    }
                    break;
                case MSG_SYNC_AT_TIME:
                    removeMessages(MSG_SYNC_AT_TIME);
                    sendEmptyMessage(MSG_SYNC_NOW);
                    sendEmptyMessageDelayed(MSG_SYNC_AT_TIME, 1000 * 60);
                    break;
            }
        }
    }
}
