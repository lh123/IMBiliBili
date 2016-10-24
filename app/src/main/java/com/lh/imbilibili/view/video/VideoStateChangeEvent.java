package com.lh.imbilibili.view.video;

import com.lh.imbilibili.model.video.VideoDetail;

/**
 * Created by liuhui on 2016/10/24.
 */

class VideoStateChangeEvent {

    public static final int STATE_PLAY = 1;
    public static final int STATE_STOP = 2;
    public static final int STATE_LOAD_FINISH = 3;

    public int state;
    public VideoDetail videoDetail;

    VideoStateChangeEvent(int state, VideoDetail videoDetail) {
        this.state = state;
        this.videoDetail = videoDetail;
    }
}
