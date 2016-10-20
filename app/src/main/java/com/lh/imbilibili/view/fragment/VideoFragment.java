package com.lh.imbilibili.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.danmakulibrary.BiliBiliDanmakuParser;
import com.lh.danmakulibrary.Danmaku;
import com.lh.danmakulibrary.DanmakuView;
import com.lh.ijkplayer.widget.IjkVideoView;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.SourceData;
import com.lh.imbilibili.model.VideoPlayData;
import com.lh.imbilibili.utils.CallUtils;
import com.lh.imbilibili.utils.DanmakuUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.VideoUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.widget.VideoControlView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by liuhui on 2016/10/3.
 */

public class VideoFragment extends BaseFragment implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnPlayControlListener, IMediaPlayer.OnPreparedListener, VideoControlView.OnMediaControlViewVisibleChangeListener, IMediaPlayer.OnCompletionListener {

    private static final int MSG_SYNC_NOW = 1;
    private static final int MSG_SYNC_AT_TIME = 2;
    private static final int MSG_LOAD_VIDEO = 3;
    private static final int MSG_LOAD_DANMAKU = 4;
    private static final int MSG_LOAD_SOURCE = 5;
    private static final int MSG_START_PLAYING = 6;

    @BindView(R.id.pre_play_msg)
    TextView mPrePlayMsg;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_buffering)
    TextView mTvBuffering;
    @BindView(R.id.videoview)
    IjkVideoView mIjkVideoView;
    @BindView(R.id.video_control_view)
    VideoControlView mVideoControlView;
    @BindView(R.id.danmaku_view)
    DanmakuView mDanmakuView;

    private String mAid;
    private String mCid;

    private String mTitle;
    private List<SourceData> mSourceDatas;
    private VideoPlayData mVideoPlayData;

    private ArrayList<Danmaku> mDanmakus;
    private BiliBiliDanmakuParser mParser;
    private DanmakuUtils mDanmakuUtils;

    private Call<BiliBiliResultResponse<List<SourceData>>> sourceInfoCall;
    private Call<VideoPlayData> playInfoCall;

    private UpdatePrePlayMsgReceiver mPrePlayMsgReceiver;

    private VideoHandler mHandler;
    private OnVideoFragmentStateListener mOnVideoFragmentStateListener;

    private boolean mResumePlay = false;
    private boolean mIsFirstLoadVideo = true;

    private boolean mIsDanmakuLoadFinish = false;
    private boolean mIsVideoLoadFinish = false;

    private int mPrePlayerPosition;
    private int mCurrentQuality = 3;


    public static VideoFragment newInstance(String aid, String cid, String title) {
        VideoFragment fragment = new VideoFragment();
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
        mPrePlayMsgReceiver = new UpdatePrePlayMsgReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mPrePlayMsgReceiver, new IntentFilter(IjkVideoView.ACTION));
        ButterKnife.bind(this, view);
        mDanmakuView.setShowDebugInfo(false);
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit().putBoolean(getString(com.example.ijkplayer.R.string.pref_key_using_media_codec), true).apply();
        mAid = getArguments().getString("aid");
        mCid = getArguments().getString("cid");
        mTitle = getArguments().getString("title");
        mDanmakuUtils = new DanmakuUtils();
        initIjkPlayer();
        appendVideoMsg(null, StringUtils.format("正在载入(id=%s)", mAid), false);
        appendVideoMsg(null, "正在解析视频信息...", true);
        appendVideoMsg(null, "正在解析弹幕...", true);
        appendVideoMsg(null, "正在解析播放地址...", true);
        mVideoControlView.setVideoView(mIjkVideoView);
        mVideoControlView.setFullScreenButtonVisible(true);
        mVideoControlView.setTopMediaControlViewVisible(false);
        if (!TextUtils.isEmpty(mCid)) {
            appendVideoMsg("正在解析视频信息...", StringUtils.format("成功(av_id=%s cid=%s)", mAid, mCid), false);
            mHandler.sendEmptyMessage(MSG_LOAD_VIDEO);
            mHandler.sendEmptyMessage(MSG_LOAD_DANMAKU);
        } else {
            mHandler.sendEmptyMessage(MSG_LOAD_SOURCE);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_videoview;
    }

    private void initIjkPlayer() {
        mVideoControlView.setVideoTitle(mTitle);
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mIjkVideoView.setOnCompletionListener(this);
        mVideoControlView.setOnPlayControlListener(this);
        mVideoControlView.setOnMediaControlViewVisibleChangeListener(this);
    }

    private void loadSourceInfo() {
        sourceInfoCall = RetrofitHelper.getInstance().getBangumiService().getSource(mAid, System.currentTimeMillis());
        sourceInfoCall.enqueue(new Callback<BiliBiliResultResponse<List<SourceData>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<SourceData>>> call, Response<BiliBiliResultResponse<List<SourceData>>> response) {
                if (response.body().getCode() == 0) {
                    mSourceDatas = response.body().getResult();
                    SourceData sourceData = mSourceDatas.get(0);
                    mAid = sourceData.getAvId();
                    mCid = sourceData.getCid();
                    appendVideoMsg("正在解析视频信息...", StringUtils.format("成功(av_id=%s cid=%s)", sourceData.getAvId(), sourceData.getCid()), false);
                    mHandler.sendEmptyMessage(MSG_LOAD_DANMAKU);
                    mHandler.sendEmptyMessage(MSG_LOAD_VIDEO);
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResultResponse<List<SourceData>>> call, Throwable t) {
                appendVideoMsg("正在解析视频信息...", "失败", false);
            }
        });
    }

    private void loadVideoInfo() {
        playInfoCall = RetrofitHelper.getInstance().getVideoPlayService().getPlayData(Constant.BUILD, Constant.PLATFORM, mAid, 0, 0, 0, mCid, mCurrentQuality, "json");
        playInfoCall.enqueue(new Callback<VideoPlayData>() {
            @Override
            public void onResponse(Call<VideoPlayData> call, Response<VideoPlayData> response) {
                if (response.body() != null && response.body().getDurl() != null) {
                    appendVideoMsg("正在解析播放地址...", "成功", false);
                    mVideoPlayData = response.body();
                    if (mIsFirstLoadVideo) {
                        mIsFirstLoadVideo = false;
                        mIjkVideoView.setVideoPath(VideoUtils.concatVideo(getContext().getApplicationContext(), mVideoPlayData.getDurl()));
                    } else {
                        mIjkVideoView.changeVideoPath(VideoUtils.concatVideo(getContext().getApplicationContext(), mVideoPlayData.getDurl()));
                    }
                    mVideoControlView.setCurrentVideoQuality(mCurrentQuality);
                    mIsVideoLoadFinish = true;
                    mHandler.sendEmptyMessage(MSG_START_PLAYING);
                } else {
                    appendVideoMsg("正在解析播放地址...", "失败", false);
                }
            }

            @Override
            public void onFailure(Call<VideoPlayData> call, Throwable t) {
                appendVideoMsg("正在解析播放地址...", "失败", false);
            }
        });
    }

    public void appendVideoMsg(@Nullable String orginMsg, String appendMsg, boolean newLine) {
        String str;
        String preStr = mPrePlayMsg.getText().toString();
        if (orginMsg != null && preStr.contains(orginMsg)) {
            str = preStr.replace(orginMsg, orginMsg + appendMsg);
            mPrePlayMsg.setText(str);
        } else {
            if (newLine) {
                mPrePlayMsg.append("\n");
            }
            mPrePlayMsg.append(appendMsg);
        }
    }


    private void loadDanmaku() {
        mDanmakuUtils.downLoadDanmaku(getContext().getApplicationContext(), mCid, new DanmakuUtils.OnDanmakuDownloadListener() {
            @Override
            public void onSuccess(File file) {
                mIsDanmakuLoadFinish = true;
                try {
                    preparDanmaku(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    appendVideoMsg("正在解析弹幕...", "失败", false);
                } finally {
                    mHandler.sendEmptyMessage(MSG_START_PLAYING);
                }
            }

            @Override
            public void onFail() {
                mIsDanmakuLoadFinish = true;
                appendVideoMsg("正在解析弹幕...", "失败", false);
            }
        });
    }

    private void preparDanmaku(InputStream stream) {
        if (mDanmakuView != null) {
            mParser = new BiliBiliDanmakuParser();
            try {
                mDanmakus = mParser.parse(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDanmakus != null) {
                mDanmakuView.setDanmakuSource(mDanmakus);
                appendVideoMsg("正在解析弹幕...", "成功", false);
            }
        }
    }

    private void startPlaying() {
        mHandler.removeMessages(MSG_SYNC_AT_TIME);
        mIjkVideoView.start();
        mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mResumePlay) {
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
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mPrePlayMsgReceiver);
        CallUtils.cancelCall(sourceInfoCall, playInfoCall);
        mIjkVideoView.stopPlayback();
        mIjkVideoView.release(true);
        mDanmakuUtils.cancel();
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
        mIjkVideoView.togglePlayer();
        startPlaying();
        return true;
    }

    @Override
    public void onQualitySelect(int quality) {
        mCurrentQuality = quality;
        mPrePlayerPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
        mHandler.sendEmptyMessage(MSG_LOAD_VIDEO);
    }

    public void changeVideo(String aid, String cid, String title) {
        mAid = aid;
        mCid = cid;
        mTitle = title;
        mVideoControlView.setVideoTitle(mTitle);
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(MSG_LOAD_VIDEO);
        mHandler.sendEmptyMessage(MSG_LOAD_DANMAKU);
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
        if (!mIsFirstLoadVideo) {
            mHandler.removeMessages(MSG_SYNC_NOW);
            mIjkVideoView.seekTo(mPrePlayerPosition);
            mHandler.sendEmptyMessage(MSG_SYNC_NOW);
        }
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

        private WeakReference<VideoFragment> mFragment;

        private VideoHandler(WeakReference<VideoFragment> fragment) {
            mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFragment.get() == null) {
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
                case MSG_LOAD_VIDEO:
                    mFragment.get().mIsVideoLoadFinish = false;
                    mFragment.get().loadVideoInfo();
                    break;
                case MSG_LOAD_DANMAKU:
                    mFragment.get().mIsDanmakuLoadFinish = false;
                    mFragment.get().loadDanmaku();
                    break;
                case MSG_LOAD_SOURCE:
                    mFragment.get().loadSourceInfo();
                    break;
                case MSG_START_PLAYING:
                    if (mFragment.get().mIsDanmakuLoadFinish && mFragment.get().mIsVideoLoadFinish) {
                        mFragment.get().startPlaying();
                    }
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

    public class UpdatePrePlayMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            appendVideoMsg(null, intent.getStringExtra("msg"), true);
        }
    }

    public interface OnVideoFragmentStateListener {
        void onFullScreenClick();

        void onMediaControlBarVisibleChanged(boolean isShow);
    }
}
