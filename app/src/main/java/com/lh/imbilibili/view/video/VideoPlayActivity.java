package com.lh.imbilibili.view.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.danmakulibrary.BiliBiliDanmakuParser;
import com.lh.danmakulibrary.Danmaku;
import com.lh.danmakulibrary.DanmakuView;
import com.lh.imbilibili.R;
import com.lh.imbilibili.data.ApiException;
import com.lh.imbilibili.data.RetrofitHelper;
import com.lh.imbilibili.model.BiliBiliResultResponse;
import com.lh.imbilibili.model.BilibiliDataResponse;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.video.PlusVideoPlayerData;
import com.lh.imbilibili.model.video.SourceData;
import com.lh.imbilibili.model.video.VideoPlayData;
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
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by home on 2016/8/3.
 * 视频播放界面
 */
public class VideoPlayActivity extends BaseActivity implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnPlayControlListener, IMediaPlayer.OnPreparedListener {

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

    private BangumiDetail.Episode mEpisode;
    private SourceData mSourceData;

    private PlusVideoPlayerData mPlusPlayerData;

    private ArrayList<Danmaku> mDanmakus;

    private VideoHandler mHandler;

    private int mLastPlayPosition;

    private int mCurrentQuality = 3;
    private long firstBackPressTime = -1;

    private boolean mUsePlusSource;

    private StringBuilder mPreMsgBuilder;

    public static void startVideoActivity(Context context, BangumiDetail.Episode episode) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("episode", episode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        mHandler = new VideoHandler(new WeakReference<>(this));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ButterKnife.bind(this);
        mEpisode = getIntent().getParcelableExtra("episode");
        mPreMsgBuilder = new StringBuilder();
        mLastPlayPosition = 0;
        mUsePlusSource = false;
        initIjkPlayer();
        mVideoControlView.setVideoView(mIjkVideoView);
        preparePlay();
    }

    private void initIjkPlayer() {
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mVideoControlView.setVideoTitle(mEpisode.getIndexTitle());
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mVideoControlView.setOnPlayControlListener(this);
    }

    private void preparePlay() {
        mPreMsgBuilder.append("【完成】\n解析视频信息...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mVideoControlView.setCurrentVideoQuality(mCurrentQuality);
        RetrofitHelper.getInstance()
                .getBangumiService()
                .getSource(mEpisode.getEpisodeId(), System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<BiliBiliResultResponse<List<SourceData>>, Observable<?>>() {
                    @Override
                    public Observable<?> call(BiliBiliResultResponse<List<SourceData>> listBiliBiliResultResponse) {
                        if (listBiliBiliResultResponse.isSuccess()) {
                            mSourceData = listBiliBiliResultResponse.getResult().get(0);
                            mPreMsgBuilder.append("【完成】\n" +
                                    "解析视频地址...\n" +
                                    "全舰弹幕填装...");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            return Observable.mergeDelayError(loadVideoInfoAccordSource(), downloadDanmaku(), reportWatch(mSourceData.getCid(), mEpisode.getEpisodeId()));
                        } else {
                            mPreMsgBuilder.append("【失败】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            return Observable.error(new ApiException(listBiliBiliResultResponse.getCode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        startPlaying();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    private Observable<String> loadVideoInfoAccordSource() {
        if (mUsePlusSource) {
            return loadVideoInfoFromPlus();
        } else {
            return loadVideoInfo();
        }
    }

    private Observable<String> loadVideoInfo() {
        return RetrofitHelper.getInstance()
                .getVideoPlayService()
                .getPlayData(mSourceData.getAvId(), 0, 0, 0, mSourceData.getCid(), 2, "json")
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

    private Observable<String> loadVideoInfoFromPlus() {
        return RetrofitHelper.getInstance()
                .getPlusVideoPlayService()
                .getPlayData(1, mSourceData.getAvId(), mEpisode.getPage())
                .compose(new Observable.Transformer<PlusVideoPlayerData, PlusVideoPlayerData>() {
                    @Override
                    public Observable<PlusVideoPlayerData> call(Observable<PlusVideoPlayerData> plusVideoPlayerDataObservable) {
                        if (mPlusPlayerData != null) {
                            return Observable.just(mPlusPlayerData);
                        } else {
                            return plusVideoPlayerDataObservable;
                        }
                    }
                })
                .flatMap(new Func1<PlusVideoPlayerData, Observable<String>>() {
                    @Override
                    public Observable<String> call(PlusVideoPlayerData plusVideoPlayerData) {
                        mPlusPlayerData = plusVideoPlayerData;
                        if (!plusVideoPlayerData.getMode().equals("error")) {
                            int index = 3 - mCurrentQuality;
                            if (index < 0) {
                                index = 0;
                            } else if (index >= plusVideoPlayerData.getData().size()) {
                                index = plusVideoPlayerData.getData().size() - 1;
                            }
                            if (TextUtils.isEmpty(plusVideoPlayerData.getData().get(index).getUrl())) {
                                return VideoUtils.concatPlusVideo(plusVideoPlayerData.getData().get(index).getParts());
                            } else {
                                return Observable.just(plusVideoPlayerData.getData().get(index).getUrl());
                            }
                        } else {
                            return Observable.error(new ApiException(-1, plusVideoPlayerData.getMode()));
                        }
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {

                    boolean haveRetry = false;

                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                            @Override
                            public Observable<?> call(Throwable throwable) {
                                if (!haveRetry) {
                                    haveRetry = true;
                                    return RetrofitHelper.getInstance().getPlusVideoPlayService().updateInfo(mSourceData.getAvId(), 1);
                                } else {
                                    return Observable.error(throwable);
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
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
        return DanmakuUtils.downLoadDanmaku(mSourceData.getCid())
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


    private void preparDanmaku(InputStream o) {
        if (mDanmakuView != null) {
            BiliBiliDanmakuParser mParser = new BiliBiliDanmakuParser();
            try {
                mDanmakus = mParser.parse(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDanmakus != null) {
                mDanmakuView.setDanmakuSource(mDanmakus);
            }
        }
    }

    private Observable<BilibiliDataResponse> reportWatch(String cid, String eposideId) {
        return RetrofitHelper.getInstance()
                .getHistoryService()
                .reportWatch(cid, eposideId, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(Observable.<BilibiliDataResponse>empty());
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
    protected void onResume() {
        super.onResume();
        if (mLastPlayPosition != 0 && !mIjkVideoView.isPlaying()) {
            mIjkVideoView.seekTo(mLastPlayPosition);
            mIjkVideoView.start();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
//            mDanmakuView.resume();
            mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
        }
    }

    @Override
    protected void onPause() {
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
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mIjkVideoView.release(true);
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
            ToastUtils.showToastShort("再按一次退出");
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
                ToastUtils.showToastShort("再按一次退出");
                firstBackPressTime = secondBackPressTime;
            }
        }
    }

    @Override
    public void onQualitySelect(int quality) {
        mCurrentQuality = quality;
        mLastPlayPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
        mIjkVideoView.release(true);
        preparePlay();
    }

    @Override
    public void onSourceChange() {
        mPreMsgBuilder.delete(0, mPreMsgBuilder.length());
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mUsePlusSource = !mUsePlusSource;
        mLastPlayPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
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
        mHandler.removeMessages(MSG_SYNC_NOW);
        mHandler.sendEmptyMessage(MSG_SYNC_NOW);
    }

    private static class VideoHandler extends Handler {

        private WeakReference<VideoPlayActivity> mActivity;

        private VideoHandler(WeakReference<VideoPlayActivity> mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() == null
                    || mActivity.get().mIjkVideoView == null
                    || mActivity.get().mDanmakuView == null) {
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
