package com.lh.imbilibili.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/6.
 */
public class Archive implements Parcelable {
    private String title;
    private String cover;
    private String uri;
    private String param;
    @SerializedName("goto")
    private String go_to;
    private int play;
    private int danmaku;
    private String author;
    @SerializedName("total_count")
    private int totalCount;
    private String desc;
    private String duration;
    private int status;

    public Archive() {
    }

    protected Archive(Parcel in) {
        title = in.readString();
        cover = in.readString();
        uri = in.readString();
        param = in.readString();
        go_to = in.readString();
        play = in.readInt();
        danmaku = in.readInt();
        author = in.readString();
        totalCount = in.readInt();
        desc = in.readString();
        duration = in.readString();
        status = in.readInt();
    }

    public static final Creator<Archive> CREATOR = new Creator<Archive>() {
        @Override
        public Archive createFromParcel(Parcel in) {
            return new Archive(in);
        }

        @Override
        public Archive[] newArray(int size) {
            return new Archive[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getGo_to() {
        return go_to;
    }

    public void setGo_to(String go_to) {
        this.go_to = go_to;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public int getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(int danmaku) {
        this.danmaku = danmaku;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(uri);
        dest.writeString(param);
        dest.writeString(go_to);
        dest.writeInt(play);
        dest.writeInt(danmaku);
        dest.writeString(author);
        dest.writeInt(totalCount);
        dest.writeString(desc);
        dest.writeString(duration);
        dest.writeInt(status);
    }
}
