package com.lh.imbilibili.widget.media;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StringUtils;

import java.lang.ref.WeakReference;

/**
 * Created by lh on 2016/8/6.
 * 视频播放界面
 */
@SuppressWarnings("FieldCanBeLocal")
public class VideoControlView extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public static final int MSG_HIDE_UI = 1;
    public static final int MSG_HIDE_VOLUME_BAR = 2;
    public static final int MSG_HIDE_BRIGHTNESS_BAR = 3;
    public static final int MSG_HIDE_MEDIA_CONTROL = 4;
    public static final int MSG_UPDATE_MEDIA_CONTROL_VIEW = 5;
    public static final int MSG_HIDE_GESTUREINFO_VIEW = 6;

    private static final long TIME_OUT = 2000;
    private static final long MEDIA_CONTROL_TIME_OUT = 3000;

    private MediaPlayerControl mMediaControl;

    private LinearLayout mVolumeBar;
    private ProgressBar mPbVolumeLevel;
    private LinearLayout mBrightnessBar;
    private ProgressBar mPbBrightnessLevel;

    private View mMediaControlView;
    private ViewGroup mTopControlView;
    private ViewGroup mBottomControlView;
    private SeekBar mSeekBar;
    private ImageView mIvPlayPause;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private TextView mTvTitle;
    private TextView mTvSource;
    private TextView mTvQualitySelect;
    private TextView mTvVideoInfo;
    private TextView mTvDanmakuShowHide;
    private ImageView mIvBack;
    private ImageView mIvFullScreen;

    private LinearLayout mGestureInfoGroup;
    private TextView mGestureInfoText;
    private TextView mGestureInfoText1;

    private AudioManager mAudioManager;
    private ControlHandler mHandler;

    private GestureDetector mGestureDetector;
    private GestureType mGestureType = GestureType.None;

    private PopupWindow mQualityPopuWindow;

    private boolean mShowing;

    private int mVolume;
    private int mMaxVolume;
    private int mCurrentPosition;

    private float mTotalVolumeOffset;
    private float mTotalBrightnessOffset;
    private float mTotalPlayPositionOffset;

    private int mCurrentQuality;

    private OnPlayControlListener mOnPlayControlListener;
    private OnMediaControlViewVisibleChangeListener mOnMediaControlViewVisibleChangeListener;
    private boolean mScrollingSeekBar = false;
    private int mBeforeScrollPosition;

    private boolean mUserPlusSource;

    public VideoControlView(Context context) {
        this(context, null, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShowing = false;
        mUserPlusSource = false;
        mCurrentPosition = -1;
        setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(context);
        initMediaControlView(inflater);
        initMediaLevelView(context, inflater);
        initGestureInfoView(inflater);
        initGesture(context);
    }

    private void initMediaControlView(LayoutInflater inflater) {
        mMediaControlView = inflater.inflate(R.layout.player_control_view, this, false);
        mTopControlView = (ViewGroup) mMediaControlView.findViewById(R.id.top_contorol);
        mBottomControlView = (ViewGroup) mMediaControlView.findViewById(R.id.bottom_control);
        mSeekBar = (SeekBar) mMediaControlView.findViewById(R.id.seekbar);
        mIvPlayPause = (ImageView) mMediaControlView.findViewById(R.id.play_pause_toggle);
        mTvCurrentTime = (TextView) mMediaControlView.findViewById(R.id.current_time);
        mTvTotalTime = (TextView) mMediaControlView.findViewById(R.id.total_time);
        mTvTitle = (TextView) mMediaControlView.findViewById(R.id.title);
        mTvSource = (TextView) mMediaControlView.findViewById(R.id.source);
        mTvQualitySelect = (TextView) mMediaControlView.findViewById(R.id.quality_select);
        mIvBack = (ImageView) mMediaControlView.findViewById(R.id.back);
        mTvVideoInfo = (TextView) mMediaControlView.findViewById(R.id.video_info);
        mTvDanmakuShowHide = (TextView) mMediaControlView.findViewById(R.id.show_hide_danmaku);
        mIvFullScreen = (ImageView) mMediaControlView.findViewById(R.id.btn_full_screen);

        @SuppressLint("InflateParams") ViewGroup popuView = (ViewGroup) inflater.inflate(R.layout.popu_quality_select_view, null);
        for (int i = 0; i < popuView.getChildCount(); i++) {
            popuView.getChildAt(i).setOnClickListener(this);
        }

        mQualityPopuWindow = new PopupWindow(popuView, mTvQualitySelect.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
        mQualityPopuWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.black_60_alpha)));
        mQualityPopuWindow.setOutsideTouchable(true);
        mSeekBar.setOnSeekBarChangeListener(this);
        addView(mMediaControlView);
        mTvVideoInfo.setOnClickListener(this);
        mIvPlayPause.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvQualitySelect.setOnClickListener(this);
        mTvSource.setOnClickListener(this);
        mTvDanmakuShowHide.setOnClickListener(this);
        mIvFullScreen.setOnClickListener(this);
    }

    private void initMediaLevelView(Context context, LayoutInflater inflater) {
        mHandler = new ControlHandler(this);
        if (!isInEditMode()) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        View mediaLevelView = inflater.inflate(R.layout.player_media_level_bar, this, false);
        mVolumeBar = (LinearLayout) mediaLevelView.findViewById(R.id.volume_bar);
        mPbVolumeLevel = (ProgressBar) mediaLevelView.findViewById(R.id.volume_level);
        mBrightnessBar = (LinearLayout) mediaLevelView.findViewById(R.id.brightness_bar);
        mPbBrightnessLevel = (ProgressBar) mediaLevelView.findViewById(R.id.brightness_level);
        mPbVolumeLevel.setMax(mMaxVolume);
        addView(mediaLevelView);
    }

    private void initGestureInfoView(LayoutInflater inflater) {
        mGestureInfoGroup = (LinearLayout) inflater.inflate(R.layout.player_gesture_info, this, false);
        mGestureInfoText = (TextView) mGestureInfoGroup.findViewById(R.id.text);
        mGestureInfoText1 = (TextView) mGestureInfoGroup.findViewById(R.id.text1);
        addView(mGestureInfoGroup);
    }

    private void initGesture(Context context) {
        mGestureDetector = new GestureDetector(context, new MyVideoViewGestureListener());
    }

    public void setVideoView(MediaPlayerControl videoView) {
        mMediaControl = videoView;
    }

    public void setVideoTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setOnPlayControlListener(OnPlayControlListener l) {
        mOnPlayControlListener = l;
    }

    public void setOnMediaControlViewVisibleChangeListener(OnMediaControlViewVisibleChangeListener listener) {
        mOnMediaControlViewVisibleChangeListener = listener;
    }

    public void setCurrentVideoQuality(int quality) {
        mCurrentQuality = quality;
        mTvQualitySelect.setText(qualityCodeForString(mCurrentQuality));
    }

    public void setCurrentDanmakuState(boolean isShow) {
        mTvDanmakuShowHide.setText(isShow ? "隐藏" : "显示");
    }

    public void setTopMediaControlViewVisible(boolean state) {
        mTopControlView.setVisibility(state ? VISIBLE : GONE);
    }

    public void setFullScreenButtonVisible(boolean state) {
        mIvFullScreen.setVisibility(state ? VISIBLE : GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        getParent().requestDisallowInterceptTouchEvent(true);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mGestureType == GestureType.Volume) {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_VOLUME_BAR, TIME_OUT);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
            } else if (mGestureType == GestureType.Brightness) {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_BRIGHTNESS_BAR, TIME_OUT);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
            } else if (mGestureType == GestureType.FastBackwardOrForward) {
                mMediaControl.seekTo(mCurrentPosition);
                mCurrentPosition = -1;
                mBeforeScrollPosition = -1;
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
            } else if (mGestureType == GestureType.SingleTapConfirmed) {
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_MEDIA_CONTROL, MEDIA_CONTROL_TIME_OUT);
            }
            mTotalPlayPositionOffset = 0;
            mTotalVolumeOffset = 0;
            mTotalBrightnessOffset = 0;
            mGestureType = GestureType.None;
        }
        return true;
    }

    private void showMediaControlView() {
        mShowing = true;
        mHandler.sendEmptyMessage(MSG_UPDATE_MEDIA_CONTROL_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_MEDIA_CONTROL, MEDIA_CONTROL_TIME_OUT);
        mMediaControlView.setVisibility(VISIBLE);
        mTvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        if (mOnMediaControlViewVisibleChangeListener != null) {
            mOnMediaControlViewVisibleChangeListener.onMediaControlViewVisibleChange(mShowing);
        }
    }

    private void hideMediaControlView() {
        mShowing = false;
        mHandler.removeMessages(MSG_UPDATE_MEDIA_CONTROL_VIEW);
        mMediaControlView.setVisibility(GONE);
        mTvTitle.setEllipsize(TextUtils.TruncateAt.END);
        if (mOnMediaControlViewVisibleChangeListener != null) {
            mOnMediaControlViewVisibleChangeListener.onMediaControlViewVisibleChange(mShowing);
        }
    }

    private void hideGestureInfoView() {
        mGestureInfoGroup.setVisibility(GONE);
    }

    public boolean isShowing() {
        return mShowing;
    }

    private void updateMediaControlView() {
        mIvPlayPause.setImageLevel(mMediaControl.isPlaying() ? 1 : 0);
        int total = mMediaControl.getDuration();
        int currentPosition = mMediaControl.getCurrentPosition();
        mTvCurrentTime.setText(stringForTime(currentPosition));
        mTvTotalTime.setText(stringForTime(total));
        mSeekBar.setProgress(currentPosition);
        mSeekBar.setMax(total);
        int secondProgress = mMediaControl.getBufferPercentage() * total / 100;
        mSeekBar.setSecondaryProgress(secondProgress);
    }

    private void playOrPause() {
        if (mMediaControl.isPlaying()) {
            mIvPlayPause.setImageLevel(0);
            mMediaControl.pause();
            if (mOnPlayControlListener != null) {
                mOnPlayControlListener.onVideoPause();
            }
        } else {
            mIvPlayPause.setImageLevel(1);
            mMediaControl.start();
            if (mOnPlayControlListener != null) {
                mOnPlayControlListener.onVideoStart();
            }
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return StringUtils.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return StringUtils.format("%02d:%02d", minutes, seconds);
        }
    }

    private String qualityCodeForString(int quality) {
        if (quality == 1) {
            return "流畅";
        } else if (quality == 2) {
            return "高清";
        } else {
            return "超清";
        }
    }

    private void setFastBackwardForwardGestureInfo(int afterTime, int preTime, int totalTime) {
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mGestureInfoGroup.setVisibility(VISIBLE);
        mGestureInfoText1.setVisibility(VISIBLE);
        mGestureInfoText.setText("");
        mGestureInfoText.append(stringForTime(afterTime));
        mGestureInfoText.append("/");
        mGestureInfoText.append(stringForTime(totalTime));
        mGestureInfoText1.setText(StringUtils.format("%+d秒", (afterTime - preTime) / 1000));
    }

    private void setVolumeGestureInfo(int percent) {
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mGestureInfoGroup.setVisibility(VISIBLE);
        mGestureInfoText1.setVisibility(GONE);
        if (percent <= 0) {
            mGestureInfoText.setText("静音");
        } else {
            mGestureInfoText.setText(StringUtils.format("音量:%d%%", percent));
        }
    }

    private void setBrightnessGestureInfo(int percent) {
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mGestureInfoGroup.setVisibility(VISIBLE);
        mGestureInfoText1.setVisibility(GONE);
        if (percent <= 0) {
            mGestureInfoText.setText("最低亮度");
        } else {
            mGestureInfoText.setText(StringUtils.format("亮度:%d%%", percent));
        }
    }

    private void setVolume(float percent) {
        mHandler.removeMessages(MSG_HIDE_VOLUME_BAR);
        mVolumeBar.setVisibility(View.VISIBLE);
        float offsetVolume = percent * mMaxVolume * 1.2f;
        mTotalVolumeOffset += offsetVolume;
        if (mTotalVolumeOffset > 1) {
            mVolume += mTotalVolumeOffset;
            mTotalVolumeOffset -= Math.floor(mTotalVolumeOffset);
        } else if (mTotalVolumeOffset < -1) {
            mVolume += Math.ceil(mTotalVolumeOffset);
            mTotalVolumeOffset -= Math.ceil(mTotalVolumeOffset);
        }
        if (mVolume > mMaxVolume) {
            mVolume = mMaxVolume;
        } else if (mVolume < 0) {
            mVolume = 0;
        }
        setVolumeGestureInfo(100 * mVolume / mMaxVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        mPbVolumeLevel.setProgress(mVolume);
    }

    private void setBrightness(float percent) {
        System.out.println(percent);
        mHandler.removeMessages(MSG_HIDE_BRIGHTNESS_BAR);
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            mHandler.removeMessages(MSG_HIDE_UI);
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            float brightness = lp.screenBrightness;
            mTotalBrightnessOffset += percent * 1.2;
            float gate = 1.0f / 15;
            if (mTotalBrightnessOffset > gate) {
                brightness += mTotalBrightnessOffset;
                mTotalBrightnessOffset = 0;
            } else if (mTotalBrightnessOffset < -gate) {
                brightness += mTotalBrightnessOffset;
                mTotalBrightnessOffset = 0;
            }
            if (brightness > 1) {
                brightness = 1;
            } else if (brightness < 0) {
                brightness = 0;
            }
            lp.screenBrightness = brightness;
            mBrightnessBar.setVisibility(View.VISIBLE);
            setBrightnessGestureInfo((int) (brightness * 100));
            mPbBrightnessLevel.setProgress((int) (brightness * 100));
            activity.getWindow().setAttributes(lp);
        }
    }

    private void hideVolumeBar() {
        mVolumeBar.setVisibility(GONE);
    }

    private void hideBrightnessBar() {
        mBrightnessBar.setVisibility(GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mMediaControl != null && mScrollingSeekBar) {
            int totalTime = mMediaControl.getDuration();
            setFastBackwardForwardGestureInfo(progress, mBeforeScrollPosition, totalTime);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mScrollingSeekBar = true;
        mHandler.removeMessages(MSG_HIDE_GESTUREINFO_VIEW);
        mHandler.removeMessages(MSG_UPDATE_MEDIA_CONTROL_VIEW);
        mHandler.removeMessages(MSG_HIDE_MEDIA_CONTROL);
        mBeforeScrollPosition = mMediaControl.getCurrentPosition();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mScrollingSeekBar = false;
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_GESTUREINFO_VIEW, TIME_OUT);
        if (mShowing) {
            mHandler.sendEmptyMessage(MSG_UPDATE_MEDIA_CONTROL_VIEW);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_MEDIA_CONTROL, MEDIA_CONTROL_TIME_OUT);
        }
        if (mMediaControl != null) {
            int pos = seekBar.getProgress();
            mMediaControl.seekTo(pos);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smooth_quality:
            case R.id.high_quality:
            case R.id.super_high_quality:
                mQualityPopuWindow.dismiss();
                mHandler.sendEmptyMessage(MSG_HIDE_MEDIA_CONTROL);
                if (mOnPlayControlListener != null) {
                    mOnPlayControlListener.onQualitySelect(Integer.parseInt((String) v.getTag()));
                }
                break;
            case R.id.play_pause_toggle:
                playOrPause();
                break;
            case R.id.btn_full_screen:
                if (mOnPlayControlListener != null) {
                    mOnPlayControlListener.onFullScreenClick();
                }
                break;
            case R.id.back:
                ((Activity) getContext()).finish();
                break;
            case R.id.video_info:
                if (mMediaControl != null) {
                    mMediaControl.showMediaInfo();
                }
                break;
            case R.id.quality_select:
                mQualityPopuWindow.showAsDropDown(v);
                break;
            case R.id.source:
                if (mOnPlayControlListener != null) {
                    mUserPlusSource = !mUserPlusSource;
                    mOnPlayControlListener.onSourceChange();
                    if (mUserPlusSource) {
                        mTvSource.setText("三方");
                    } else {
                        mTvSource.setText("官方");
                    }
                }
                break;
            case R.id.show_hide_danmaku:
                if (mOnPlayControlListener != null) {
                    mOnPlayControlListener.onDanamkuShowOrHideClick();
                }
                break;
        }
    }

    private enum GestureType {
        None,
        Volume,
        Brightness,
        FastBackwardOrForward,
        SingleTapConfirmed,
        DoubleTap
    }

    public interface OnPlayControlListener {
        void onVideoPause();

        void onVideoStart();

        void onQualitySelect(int quality);

        void onSourceChange();

        void onDanamkuShowOrHideClick();

        void onFullScreenClick();
    }

    public interface OnMediaControlViewVisibleChangeListener {
        void onMediaControlViewVisibleChange(boolean isShow);
    }

    private static class ControlHandler extends Handler {
        private WeakReference<VideoControlView> mVideoControlView;

        private ControlHandler(VideoControlView view) {
            mVideoControlView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mVideoControlView.get() != null) {
                switch (msg.what) {
                    case MSG_HIDE_UI:
                        break;
                    case MSG_HIDE_VOLUME_BAR:
                        mVideoControlView.get().hideVolumeBar();
                        break;
                    case MSG_HIDE_BRIGHTNESS_BAR:
                        mVideoControlView.get().hideBrightnessBar();
                        break;
                    case MSG_HIDE_MEDIA_CONTROL:
                        if (!mVideoControlView.get().mQualityPopuWindow.isShowing()) {
                            mVideoControlView.get().hideMediaControlView();
                        }
                        break;
                    case MSG_UPDATE_MEDIA_CONTROL_VIEW:
                        mVideoControlView.get().updateMediaControlView();
                        if (mVideoControlView.get().mMediaControl.isPlaying() && mVideoControlView.get().isShowing()) {
                            sendEmptyMessageDelayed(MSG_UPDATE_MEDIA_CONTROL_VIEW, 1000);
                        }
                        break;
                    case MSG_HIDE_GESTUREINFO_VIEW:
                        mVideoControlView.get().hideGestureInfoView();
                        break;
                }
            }
        }
    }

    private class MyVideoViewGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mGestureType == GestureType.None) {
                mGestureType = GestureType.SingleTapConfirmed;
                mHandler.removeMessages(MSG_HIDE_MEDIA_CONTROL);
                if (mShowing) {
                    hideMediaControlView();
                } else {
                    showMediaControlView();
                }
                mGestureType = GestureType.None;
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mGestureType == GestureType.None) {
                mGestureType = GestureType.DoubleTap;
                playOrPause();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mGestureType == GestureType.None) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    mGestureType = GestureType.FastBackwardOrForward;
                } else {
                    if (e1.getX() > getWidth() * 2.0 / 3) {//右边
                        mGestureType = GestureType.Volume;
                    } else if (e1.getX() < getWidth() / 3.0) {//左边
                        mGestureType = GestureType.Brightness;
                    } else {
                        mGestureType = GestureType.None;
                        return false;
                    }
                }
            }
            if (mGestureType == GestureType.Volume) {
                setVolume(distanceY / getHeight());
            } else if (mGestureType == GestureType.Brightness) {
                setBrightness(distanceY / getHeight());
            } else if (mGestureType == GestureType.FastBackwardOrForward) {
                if (mCurrentPosition < 0) {
                    mCurrentPosition = mMediaControl.getCurrentPosition();
                    mBeforeScrollPosition = mCurrentPosition;
                }
                float percent = distanceX / getWidth();
                mTotalPlayPositionOffset += percent * 90;
                if (mTotalPlayPositionOffset > 1) {
                    mCurrentPosition -= 1000 * Math.floor(mTotalPlayPositionOffset);
                    mTotalPlayPositionOffset -= Math.floor(mTotalPlayPositionOffset);
                } else if (mTotalPlayPositionOffset < -1) {
                    mCurrentPosition -= 1000 * Math.ceil(mTotalPlayPositionOffset);
                    mTotalPlayPositionOffset -= Math.ceil(mTotalPlayPositionOffset);
                }
                if (mCurrentPosition > mMediaControl.getDuration()) {
                    mCurrentPosition = mMediaControl.getDuration();
                } else if (mCurrentPosition < 0) {
                    mCurrentPosition = 0;
                }
                setFastBackwardForwardGestureInfo(mCurrentPosition, mBeforeScrollPosition, mMediaControl.getDuration());
            }
            return true;
        }
    }
}
