package com.lh.imbilibili.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/6.
 */

public class Movie implements Parcelable {
    private String title;
    private String cover;
    private String uri;
    private String param;
    @SerializedName("goto")
    private String go_to;
    @SerializedName("total_count")
    private int totalCount;
    private String desc;
    @SerializedName("screen_date")
    private String screenDate;
    private String area;
    @SerializedName("cover_mark")
    private String coverMark;
    private String actors;
    private String staff;
    private int length;
    private int status;

    public Movie() {
    }

    protected Movie(Parcel in) {
        title = in.readString();
        cover = in.readString();
        uri = in.readString();
        param = in.readString();
        go_to = in.readString();
        totalCount = in.readInt();
        desc = in.readString();
        screenDate = in.readString();
        area = in.readString();
        coverMark = in.readString();
        actors = in.readString();
        staff = in.readString();
        length = in.readInt();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(uri);
        dest.writeString(param);
        dest.writeString(go_to);
        dest.writeInt(totalCount);
        dest.writeString(desc);
        dest.writeString(screenDate);
        dest.writeString(area);
        dest.writeString(coverMark);
        dest.writeString(actors);
        dest.writeString(staff);
        dest.writeInt(length);
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
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

    public String getScreenDate() {
        return screenDate;
    }

    public void setScreenDate(String screenDate) {
        this.screenDate = screenDate;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCoverMark() {
        return coverMark;
    }

    public void setCoverMark(String coverMark) {
        this.coverMark = coverMark;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}