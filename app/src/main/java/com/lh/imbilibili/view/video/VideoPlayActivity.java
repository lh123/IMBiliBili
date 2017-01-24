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
import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.data.helper.PlusHelper;
import com.lh.imbilibili.data.helper.VideoPlayerHelper;
import com.lh.imbilibili.model.BiliBiliResponse;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.model.video.PlusVideoPlayerData;
import com.lh.imbilibili.model.video.SourceData;
import com.lh.imbilibili.model.video.VideoPlayData;
import com.lh.imbilibili.utils.DanmakuUtils;
import com.lh.imbilibili.utils.DisposableUtils;
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
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
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

    private Disposable mPrepareSub;

    private BangumiDetail.Episode mEpisode;
    private SourceData mSourceData;

    private String[] mSourcesList;
    private int mCurrentSourceIndex;

    private PlusVideoPlayerData mPlusPlayerData;

    private ArrayList<Danmaku> mDanmakus;

    private VideoHandler mHandler;

    private int mLastPlayPosition;

    private int mCurrentQuality = 3;
    private long firstBackPressTime = -1;

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
        mCurrentSourceIndex = 1;
        initIjkPlayer();
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
        mSourcesList = new String[]{"官方", "PLUS"};
        mVideoControlView.setSourceList(mSourcesList, mCurrentSourceIndex);
        mVideoControlView.setVideoView(mIjkVideoView);
    }

    private void preparePlay() {
        DisposableUtils.dispose(mPrepareSub);
        mPreMsgBuilder.append("【完成】\n解析视频信息...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mPrepareSub = CommonHelper.getInstance()
                .getBangumiService()
                .getSource(mEpisode.getEpisodeId(), System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<BiliBiliResponse<List<SourceData>>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(BiliBiliResponse<List<SourceData>> listBiliBiliResponse) {
                        if (listBiliBiliResponse.isSuccess()) {
                            mSourceData = listBiliBiliResponse.getResult().get(0);
                            mPreMsgBuilder.append("【完成】\n" +
                                    "解析视频地址...\n" +
                                    "全舰弹幕填装...");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            return Observable.mergeDelayError(loadVideoInfoAccordSource(), downloadDanmaku(), reportWatch(mSourceData.getCid(), mEpisode.getEpisodeId()));
                        } else {
                            mPreMsgBuilder.append("【失败】");
                            mPrePlayMsg.setText(mPreMsgBuilder);
                            return Observable.error(new ApiException(listBiliBiliResponse.getCode()));
                        }
                    }
                })
                .ignoreElements()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        startPlaying();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private Observable<String> loadVideoInfoAccordSource() {
        if (mCurrentSourceIndex != 0) {
            return loadVideoInfoFromPlus();
        } else {
            return loadVideoInfo();
        }
    }

    private Observable<String> loadVideoInfo() {
        return VideoPlayerHelper.getInstance()
                .getOfficialService()
                .getPlayData(mSourceData.getAvId(), 0, 0, 0, mSourceData.getCid(), mCurrentQuality, "json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<VideoPlayData, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(VideoPlayData videoPlayData) {
                        if (videoPlayData.getDurl() != null && !videoPlayData.getDurl().isEmpty()) {
                            int[] qualities = videoPlayData.getAcceptQuality();
                            List<VideoControlView.QualityItem> qualityItems = new ArrayList<>();
                            for (int quality : qualities) {
                                String name;
                                if (quality == 1) {
                                    name = "流畅";
                                } else if (quality == 2) {
                                    name = "高清";
                                } else if (quality == 3) {
                                    name = "超清";
                                } else {
                                    name = "1080p";
                                }
                                qualityItems.add(new VideoControlView.QualityItem(name, quality));
                            }
                            mVideoControlView.setQualityList(qualityItems);
                            return VideoUtils.concatVideo(videoPlayData.getDurl()).subscribeOn(Schedulers.io());
                        } else {
                            return Observable.error(new ApiException(-1));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        mIjkVideoView.setVideoPath(s);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
    }

    private Observable<String> loadVideoInfoFromPlus() {
        return PlusHelper.getInstance()
                .getPlusService()
                .getPlayData(1, mSourceData.getAvId(), mEpisode.getPage())
                .subscribeOn(Schedulers.io())
                .compose(new ObservableTransformer<PlusVideoPlayerData, PlusVideoPlayerData>() {
                    @Override
                    public ObservableSource<PlusVideoPlayerData> apply(Observable<PlusVideoPlayerData> upstream) {
                        if (mPlusPlayerData != null) {
                            return Observable.just(mPlusPlayerData);
                        } else {
                            return upstream;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<PlusVideoPlayerData, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(PlusVideoPlayerData plusVideoPlayerData) {
                        mPlusPlayerData = plusVideoPlayerData;
                        if (!plusVideoPlayerData.getMode().equals("error")) {
                            List<PlusVideoPlayerData.Data> datas = plusVideoPlayerData.getData();
                            List<VideoControlView.QualityItem> qualityItems = new ArrayList<>();
                            for (int i = 0; i < datas.size(); i++) {
                                if (datas.get(i).getName().contains("超清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("超清", 3));
                                } else if (datas.get(i).getName().contains("高清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("高清", 2));
                                } else if (datas.get(i).getName().contains("低清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("流畅", 1));
                                }
                            }
                            mVideoControlView.setQualityList(qualityItems);
                            int index = 0;
                            for (int i = 0; i < qualityItems.size(); i++) {
                                if (qualityItems.get(i).getId() == mCurrentQuality) {
                                    index = i;
                                    break;
                                }
                            }
                            if (TextUtils.isEmpty(plusVideoPlayerData.getData().get(index).getUrl())) {
                                return VideoUtils.concatPlusVideo(plusVideoPlayerData.getData().get(index).getParts()).subscribeOn(Schedulers.io());
                            } else {
                                return Observable.just(plusVideoPlayerData.getData().get(index).getUrl());
                            }
                        } else {
                            return Observable.error(new ApiException(-1, plusVideoPlayerData.getMode()));
                        }
                    }
                })
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {

                    boolean haveRetry = false;

                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> observable) {
                        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) {
                                if (!haveRetry) {
                                    haveRetry = true;
                                    return PlusHelper.getInstance().getPlusService().updateInfo(mSourceData.getAvId(), 1).subscribeOn(Schedulers.io());
                                } else {
                                    return Observable.error(throwable);
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        mIjkVideoView.setVideoPath(s);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {
                    @Override
                    public ObservableSource<? extends String> apply(Throwable throwable) {
                        mPreMsgBuilder.append("\n海外解析视频地址...");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        return loadVideoInfoFromPlusUnBlock();
                    }
                });
    }

    private Observable<String> loadVideoInfoFromPlusUnBlock() {
        return PlusHelper.getInstance()
                .getPlusService()
                .getPlayDataUnBlock(1, mSourceData.getAvId(), mEpisode.getPage())
                .subscribeOn(Schedulers.io())
                .compose(new ObservableTransformer<PlusVideoPlayerData, PlusVideoPlayerData>() {
                    @Override
                    public ObservableSource<PlusVideoPlayerData> apply(Observable<PlusVideoPlayerData> upstream) {
                        if (mPlusPlayerData != null) {
                            return Observable.just(mPlusPlayerData);
                        } else {
                            return upstream;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<PlusVideoPlayerData, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(PlusVideoPlayerData plusVideoPlayerData) {
                        mPlusPlayerData = plusVideoPlayerData;
                        if (!plusVideoPlayerData.getMode().equals("error")) {
                            List<PlusVideoPlayerData.Data> datas = plusVideoPlayerData.getData();
                            List<VideoControlView.QualityItem> qualityItems = new ArrayList<>();
                            for (int i = 0; i < datas.size(); i++) {
                                if (datas.get(i).getName().contains("超清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("超清", 3));
                                } else if (datas.get(i).getName().contains("高清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("高清", 2));
                                } else if (datas.get(i).getName().contains("低清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("流畅", 1));
                                }
                            }
                            mVideoControlView.setQualityList(qualityItems);
                            int index = 0;
                            for (int i = 0; i < qualityItems.size(); i++) {
                                if (qualityItems.get(i).getId() == mCurrentQuality) {
                                    index = i;
                                    break;
                                }
                            }
                            if (TextUtils.isEmpty(plusVideoPlayerData.getData().get(index).getUrl())) {
                                return VideoUtils.concatPlusVideo(plusVideoPlayerData.getData().get(index).getParts()).subscribeOn(Schedulers.io());
                            } else {
                                return Observable.just(plusVideoPlayerData.getData().get(index).getUrl());
                            }
                        } else {
                            return Observable.error(new ApiException(-1, plusVideoPlayerData.getMode()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        String target = "海外解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        mIjkVideoView.setVideoPath(s);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        String target = "海外解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
    }

    private Observable<InputStream> downloadDanmaku() {
        return DanmakuUtils.downLoadDanmaku(mSourceData.getCid())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) {
                        preparDanmaku(inputStream);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends InputStream>>() {
                    @Override
                    public ObservableSource<? extends InputStream> apply(Throwable throwable) {
                        String target = "全舰弹幕填装...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        return Observable.empty();
                    }
                })
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) {
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

    private Observable<BiliBiliResponse> reportWatch(String cid, String eposideId) {
        return CommonHelper.getInstance()
                .getHistoryService()
                .reportWatch(cid, eposideId, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(Observable.<BiliBiliResponse>empty());
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
        DisposableUtils.dispose(mPrepareSub);
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
    public void onQualitySelect(VideoControlView.QualityItem item) {
        if (item.getId() == mCurrentQuality) {
            return;
        }
        mCurrentQuality = item.getId();
        mPreMsgBuilder.delete(0, mPreMsgBuilder.length());
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mLastPlayPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
        mIjkVideoView.release(true);
        preparePlay();
    }

    @Override
    public void onSourceChange(int index) {
        if (mCurrentSourceIndex == index) {
            return;
        }
        mCurrentSourceIndex = index;
        mPreMsgBuilder.delete(0, mPreMsgBuilder.length());
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
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
