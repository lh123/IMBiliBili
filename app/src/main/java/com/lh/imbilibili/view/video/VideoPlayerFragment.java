package com.lh.imbilibili.view.video;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.danmakulibrary.BiliBiliDanmakuParser;
import com.lh.danmakulibrary.Danmaku;
import com.lh.danmakulibrary.DanmakuView;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.video.VideoPlayData;
import com.lh.imbilibili.utils.DanmakuUtils;
import com.lh.imbilibili.utils.VideoUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.widget.media.VideoControlView;
import com.lh.imbilibili.widget.media.VideoPlayerView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by liuhui on 2016/10/3.
 * 视频播放Fragment
 */

public class VideoPlayerFragment extends BaseFragment implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnPlayControlListener, IMediaPlayer.OnPreparedListener, VideoControlView.OnMediaControlViewVisibleChangeListener, IMediaPlayer.OnCompletionListener {

    private static final int MSG_SYNC_NOW = 1;
    private static final int MSG_SYNC_AT_TIME = 2;

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
    private OnVideoFragmentStateListener mOnVideoFragmentStateListener;

    private int mLastPlayPosition;
    private int mCurrentQuality = 3;

    private StringBuilder mPreMsgBuilder;

    public static VideoPlayerFragment newInstance(String aid, String cid, String title) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        bundle.putString("cid", cid);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnFullScreemButtonClick(OnVideoFragmentStateListener listener) {
        mOnVideoFragmentStateListener = listener;
    }

    @Override
    protected void initView(View view) {
        mHandler = new VideoHandler(new WeakReference<>(this));
        ButterKnife.bind(this, view);
        mDanmakuView.setShowDebugInfo(false);
        mAid = getArguments().getString("aid");
        mCid = getArguments().getString("cid");
        mTitle = getArguments().getString("title");
        mLastPlayPosition = 0;
        initIjkPlayer();
        mVideoControlView.setVideoView(mIjkVideoView);
        mVideoControlView.setFullScreenButtonVisible(true);
        mVideoControlView.setTopMediaControlViewVisible(false);
        preparePlay();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_videoview;
    }

    private void initIjkPlayer() {
        mPreMsgBuilder = new StringBuilder();
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mVideoControlView.setVideoTitle(mTitle);
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mVideoControlView.setOnPlayControlListener(this);
        mVideoControlView.setOnMediaControlViewVisibleChangeListener(this);
    }

    //加载视频播放所需的所有数据
    private void preparePlay() {
        mPreMsgBuilder.append("【完成】\n解析视频信息...【完成】\n" +
                "解析视频地址...\n" +
                "全舰弹幕填装...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        Observable.merge(loadVideoInfo(), downloadDanmaku())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        startPlaying();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    private Observable<String> loadVideoInfo() {
        return RetrofitHelper.getInstance()
                .getVideoPlayService()
                .getPlayData(mAid, 0, 0, 0, mCid, mCurrentQuality, "json")
                .flatMap(new Func1<VideoPlayData, Observable<String>>() {
                    @Override
                    public Observable<String> call(VideoPlayData videoPlayData) {
                        if (videoPlayData.getDurl() != null && !videoPlayData.getDurl().isEmpty()) {
                            return VideoUtils.concatVideo(videoPlayData.getDurl());
                        } else {
                            return Observable.error(new ApiException(-1));
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        mIjkVideoView.setVideoPath(s);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
    }

    private Observable<InputStream> downloadDanmaku() {
        return DanmakuUtils.downLoadDanmaku(mCid)
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        preparDanmaku(inputStream);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends InputStream>>() {
                    @Override
                    public Observable<? extends InputStream> call(Throwable throwable) {
                        String target = "全舰弹幕填装...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        return Observable.empty();
                    }
                })
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        String target = "全舰弹幕填装...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
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
        if (mLastPlayPosition != 0) {
            mIjkVideoView.seekTo(mLastPlayPosition);
            mLastPlayPosition = 0;
        }
        mIjkVideoView.start();
        mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLastPlayPosition != 0 && !mIjkVideoView.isPlaying()) {
            mIjkVideoView.seekTo(mLastPlayPosition);
            mIjkVideoView.start();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
            mDanmakuView.resume();
            mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIjkVideoView.isPlaying()) {
            mLastPlayPosition = mIjkVideoView.getCurrentPosition();
            mIjkVideoView.pause();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
            mHandler.removeMessages(MSG_SYNC_NOW);
            mDanmakuView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mIjkVideoView.release(true);
        if (mDanmakuView != null) {
            mDanmakuView.release();
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
    public void onQualitySelect(VideoControlView.QualityItem item) {
        mCurrentQuality = item.getId();
        mLastPlayPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
    }

    @Override
    public void onSourceChange(int index) {
    }

    public void changeVideo(String aid, String cid, String title) {
        mAid = aid;
        mCid = cid;
        mTitle = title;
        mVideoControlView.setVideoTitle(mTitle);
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mIjkVideoView.release(true);
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

    @Override
    public void onFullScreenClick() {
        if (mOnVideoFragmentStateListener != null) {
            mOnVideoFragmentStateListener.onFullScreenClick();
        }
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
        mHandler.removeMessages(MSG_SYNC_NOW);
        mHandler.sendEmptyMessage(MSG_SYNC_NOW);
    }

    @Override
    public void onMediaControlViewVisibleChange(boolean isShow) {
        if (mOnVideoFragmentStateListener != null) {
            mOnVideoFragmentStateListener.onMediaControlBarVisibleChanged(isShow);
        }
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mDanmakuView.pause();
    }

    private static class VideoHandler extends Handler {

        private WeakReference<VideoPlayerFragment> mFragment;

        private VideoHandler(WeakReference<VideoPlayerFragment> fragment) {
            mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFragment.get() == null
                    || mFragment.get().mIjkVideoView == null
                    || mFragment.get().mDanmakuView == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SYNC_NOW:
                    if (mFragment.get().mDanmakuView.isPrepared()) {
                        long videoCurrentPosition = mFragment.get().mIjkVideoView.getCurrentPosition();
                        long danmakuCurrentPosition = mFragment.get().mDanmakuView.getCurrentTime();
                        long diff = danmakuCurrentPosition - videoCurrentPosition;
                        if (diff > 500 && diff < 1000) {
                            mFragment.get().mDanmakuView.pause();
                            sendEmptyMessageDelayed(MSG_SYNC_NOW, diff);
                        } else if (diff > 1000 || diff < -500) {
                            mFragment.get().mDanmakuView.seekTo(videoCurrentPosition);
                            sendEmptyMessage(MSG_SYNC_NOW);
                        } else {
                            if (mFragment.get().mIjkVideoView.isPlaying()) {
                                mFragment.get().mDanmakuView.resume();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mVideoControlView.setTopMediaControlViewVisible(true);
            mVideoControlView.setFullScreenButtonVisible(false);
        } else {
            mVideoControlView.setTopMediaControlViewVisible(false);
            mVideoControlView.setFullScreenButtonVisible(true);
        }

    }


    public interface OnVideoFragmentStateListener {
        void onFullScreenClick();

        void onMediaControlBarVisibleChanged(boolean isShow);
    }
}
