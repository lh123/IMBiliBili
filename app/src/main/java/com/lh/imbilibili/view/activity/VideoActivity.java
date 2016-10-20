package com.lh.imbilibili.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lh.imbilibili.utils.HistoryUtils;
import com.lh.imbilibili.utils.StringUtils;
import com.lh.imbilibili.utils.ToastUtils;
import com.lh.imbilibili.utils.VideoUtils;
import com.lh.imbilibili.view.BaseActivity;
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
 * Created by home on 2016/8/3.
 */
public class VideoActivity extends BaseActivity implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnPlayControlListener, IMediaPlayer.OnPreparedListener {

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

    private boolean mResumePlay = false;
    private boolean mIsFirstLoadVideo = true;

    private boolean mIsDanmakuLoadFinish = false;
    private boolean mIsVideoLoadFinish = false;

    private int mPrePlayerPosition;
    private int mCurrentQuality = 3;
    private long firstBackPressTime = -1;

//    public static void startVideoActivity(Context context, BangumiDetail.Episode episode, String title) {
//        Intent intent = new Intent(context, VideoActivity.class);
//        intent.putExtra("data", episode);
//        intent.putExtra("title", title);
//        context.startActivity(intent);
//    }

    public static void startVideoActivity(Context context, String aid, String cid, String title) {
        Intent intent = new Intent(context, VideoActivity.class);
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
        mPrePlayMsgReceiver = new UpdatePrePlayMsgReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mPrePlayMsgReceiver, new IntentFilter(IjkVideoView.ACTION));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ButterKnife.bind(this);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(getString(com.example.ijkplayer.R.string.pref_key_using_media_codec), true).apply();
        mAid = getIntent().getStringExtra("aid");
        mCid = getIntent().getStringExtra("cid");
        mTitle = getIntent().getStringExtra("title");
        mDanmakuUtils = new DanmakuUtils();
        initIjkPlayer();
        appendVideoMsg(null, StringUtils.format("正在载入(id=%s)", mAid), false);
        appendVideoMsg(null, "正在解析视频信息...", true);
        appendVideoMsg(null, "正在解析弹幕...", true);
        appendVideoMsg(null, "正在解析播放地址...", true);
        mVideoControlView.setVideoView(mIjkVideoView);
        if (!TextUtils.isEmpty(mCid)) {
            appendVideoMsg("正在解析视频信息...", StringUtils.format("成功(av_id=%s cid=%s)", mAid, mCid), false);
            mHandler.sendEmptyMessage(MSG_LOAD_VIDEO);
            mHandler.sendEmptyMessage(MSG_LOAD_DANMAKU);
        } else {
            mHandler.sendEmptyMessage(MSG_LOAD_SOURCE);
        }
    }

    private void initIjkPlayer() {
        mVideoControlView.setVideoTitle(mTitle);
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mVideoControlView.setOnPlayControlListener(this);
    }

    private void loadSourceInfo() {
        sourceInfoCall = RetrofitHelper
                .getInstance()
                .getBangumiService()
                .getSource(mAid, System.currentTimeMillis());
        sourceInfoCall.enqueue(new Callback<BiliBiliResultResponse<List<SourceData>>>() {
            @Override
            public void onResponse(Call<BiliBiliResultResponse<List<SourceData>>> call, Response<BiliBiliResultResponse<List<SourceData>>> response) {
                if (response.body().getCode() == 0) {
                    mSourceDatas = response.body().getResult();
                    SourceData sourceData = mSourceDatas.get(0);
                    mAid = sourceData.getAvId();
                    mCid = sourceData.getCid();
                    HistoryUtils.addHisotry(mCid, sourceData.getEpisodeId());
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
        playInfoCall = RetrofitHelper
                .getInstance()
                .getVideoPlayService()
                .getPlayData(Constant.BUILD, Constant.PLATFORM, mAid, 0, 0, 0, mCid, mCurrentQuality, "json");
        playInfoCall.enqueue(new Callback<VideoPlayData>() {
            @Override
            public void onResponse(Call<VideoPlayData> call, Response<VideoPlayData> response) {
                if (response.body() != null && response.body().getDurl() != null) {
                    appendVideoMsg("正在解析播放地址...", "成功", false);
                    mVideoPlayData = response.body();
                    if (mIsFirstLoadVideo) {
                        mIsFirstLoadVideo = false;
                        mIjkVideoView.setVideoPath(VideoUtils.concatVideo(getApplicationContext(), mVideoPlayData.getDurl()));
                    } else {
                        mIjkVideoView.changeVideoPath(VideoUtils.concatVideo(getApplicationContext(), mVideoPlayData.getDurl()));
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
        mDanmakuUtils.downLoadDanmaku(getApplicationContext(), mCid, new DanmakuUtils.OnDanmakuDownloadListener() {
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
    protected void onResume() {
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPrePlayMsgReceiver);
        CallUtils.cancelCall(sourceInfoCall, playInfoCall);
        mIjkVideoView.stopPlayback();
        mIjkVideoView.release(true);
        mDanmakuUtils.cancel();
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
        mIjkVideoView.togglePlayer();
        startPlaying();
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
        mHandler.sendEmptyMessage(MSG_LOAD_VIDEO);
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

        private WeakReference<VideoActivity> mActivity;

        private VideoHandler(WeakReference<VideoActivity> mActivity) {
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
                case MSG_LOAD_VIDEO:
                    mActivity.get().mIsVideoLoadFinish = false;
                    mActivity.get().loadVideoInfo();
                    break;
                case MSG_LOAD_DANMAKU:
                    mActivity.get().mIsDanmakuLoadFinish = false;
                    mActivity.get().loadDanmaku();
                    break;
                case MSG_LOAD_SOURCE:
                    mActivity.get().loadSourceInfo();
                    break;
                case MSG_START_PLAYING:
                    if (mActivity.get().mIsDanmakuLoadFinish && mActivity.get().mIsVideoLoadFinish) {
                        mActivity.get().startPlaying();
                    }
                    break;
            }
        }
    }

    public class UpdatePrePlayMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            appendVideoMsg(null, intent.getStringExtra("msg"), true);
        }
    }
}
