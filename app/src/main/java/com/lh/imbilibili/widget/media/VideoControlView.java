package com.lh.imbilibili.widget.media;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by lh on 2016/8/6.
 * 视频播放界面
 */
@SuppressWarnings("FieldCanBeLocal")
public class VideoControlView extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {

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
    private TextView mTvSourceSelect;
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

    private PopupWindow mQualityPopupWindow;
    private PopupWindow mSourcePopupWindow;

    private boolean mShowing;

    private int mVolume;
    private int mMaxVolume;
    private int mCurrentPosition;

    private float mTotalVolumeOffset;
    private float mTotalBrightnessOffset;
    private float mTotalPlayPositionOffset;

    private String[] mSourceList;
    private boolean mCurrentDanmakuState;

    private OnPlayControlListener mOnPlayControlListener;
    private OnMediaControlViewVisibleChangeListener mOnMediaControlViewVisibleChangeListener;
    private boolean mScrollingSeekBar = false;
    private int mBeforeScrollPosition;

    private ArrayAdapter<QualityItem> mQualityAdapter;

    public VideoControlView(Context context) {
        this(context, null, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShowing = false;
        mCurrentPosition = -1;
        mCurrentDanmakuState = true;
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
        mTvSourceSelect = (TextView) mMediaControlView.findViewById(R.id.source);
        mTvQualitySelect = (TextView) mMediaControlView.findViewById(R.id.quality_select);
        mIvBack = (ImageView) mMediaControlView.findViewById(R.id.back);
        mTvVideoInfo = (TextView) mMediaControlView.findViewById(R.id.video_info);
        mTvDanmakuShowHide = (TextView) mMediaControlView.findViewById(R.id.show_hide_danmaku);
        mIvFullScreen = (ImageView) mMediaControlView.findViewById(R.id.btn_full_screen);

        ListView qualityListView = new ListView(getContext());
        mQualityAdapter = new ArrayAdapter<>(getContext(), R.layout.video_popup_item, R.id.popup_txt);
        qualityListView.setAdapter(mQualityAdapter);
        qualityListView.setId(R.id.quality_list);
        qualityListView.setOnItemClickListener(this);
        mQualityPopupWindow = new PopupWindow(qualityListView, mTvQualitySelect.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
        mQualityPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.black_alpha60)));
        mQualityPopupWindow.setOutsideTouchable(true);

        ListView sourceListView = new ListView(getContext());
        sourceListView.setId(R.id.source_list);
        sourceListView.setOnItemClickListener(this);
        mSourcePopupWindow = new PopupWindow(sourceListView, mTvSourceSelect.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSourcePopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.black_alpha60)));
        mSourcePopupWindow.setOutsideTouchable(true);

        mSeekBar.setOnSeekBarChangeListener(this);
        addView(mMediaControlView);
        mTvVideoInfo.setOnClickListener(this);
        mIvPlayPause.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvQualitySelect.setOnClickListener(this);
        mTvSourceSelect.setOnClickListener(this);
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

    public void setCurrentDanmakuState(boolean isShow) {
        mTvDanmakuShowHide.setText(isShow ? "隐藏" : "显示");
    }

    public void setTopMediaControlViewVisible(boolean state) {
        mTopControlView.setVisibility(state ? VISIBLE : GONE);
    }

    public void setFullScreenButtonVisible(boolean state) {
        mIvFullScreen.setVisibility(state ? VISIBLE : GONE);
    }

    public void setQualityList(List<QualityItem> qualityList) {
        if (qualityList == null) {
            return;
        }
        if (mQualityAdapter == null || mQualityAdapter.isEmpty()) {
            int currentId = 0;
            int index = 0;
            for (int i = 0; i < qualityList.size(); i++) {
                if (currentId < qualityList.get(i).getId()) {
                    currentId = qualityList.get(i).getId();
                    index = i;
                }
            }
            mTvQualitySelect.setText(qualityList.get(index).getName());
        }
        mTvQualitySelect.setVisibility(VISIBLE);
        mQualityAdapter.clear();
        mQualityAdapter.addAll(qualityList);
        ListView listView = (ListView) mQualityPopupWindow.getContentView();
        listView.setAdapter(mQualityAdapter);
    }

    public void setSourceList(String[] sourceList, int defaultIndex) {
        mTvSourceSelect.setVisibility(VISIBLE);
        mSourceList = sourceList;
        mTvSourceSelect.setText(sourceList[defaultIndex]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.video_popup_item, R.id.popup_txt);
        for (String source : mSourceList) {
            arrayAdapter.add(source);
        }
        ListView listView = (ListView) mSourcePopupWindow.getContentView();
        listView.setAdapter(arrayAdapter);
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
                mQualityPopupWindow.showAsDropDown(v);
                break;
            case R.id.source:
                mSourcePopupWindow.showAsDropDown(v);
                break;
            case R.id.show_hide_danmaku:
                if (mOnPlayControlListener != null) {
                    mCurrentDanmakuState = !mCurrentDanmakuState;
                    setCurrentDanmakuState(mCurrentDanmakuState);
                    mOnPlayControlListener.onDanamkuShowOrHideClick();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnPlayControlListener == null) {
            return;
        }
        if (parent.getId() == R.id.quality_list) {
            mTvQualitySelect.setText(mQualityAdapter.getItem(position).getName());
            mOnPlayControlListener.onQualitySelect(mQualityAdapter.getItem(position));
        } else if (parent.getId() == R.id.source_list) {
            mTvSourceSelect.setText(mSourceList[position]);
            mOnPlayControlListener.onSourceChange(position);
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

        void onQualitySelect(QualityItem item);

        void onSourceChange(int index);

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
                        if (!mVideoControlView.get().mQualityPopupWindow.isShowing() && !mVideoControlView.get().mSourcePopupWindow.isShowing()) {
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

    public static class QualityItem {
        private String name;
        private int id;

        public QualityItem(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
