package com.lh.imbilibili.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/6.
 */

public class Up implements Parcelable {
    private String title;
    private String cover;
    private String uri;
    private String param;
    @SerializedName("goto")
    private String go_to;
    @SerializedName("total_count")
    private int totalCount;
    private String sign;
    private int fans;
    private String archives;
    private int status;

    protected Up(Parcel in) {
        title = in.readString();
        cover = in.readString();
        uri = in.readString();
        param = in.readString();
        go_to = in.readString();
        totalCount = in.readInt();
        sign = in.readString();
        fans = in.readInt();
        archives = in.readString();
        status = in.readInt();
    }

    public static final Creator<Up> CREATOR = new Creator<Up>() {
        @Override
        public Up createFromParcel(Parcel in) {
            return new Up(in);
        }

        @Override
        public Up[] newArray(int size) {
            return new Up[size];
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getArchives() {
        return archives;
    }

    public void setArchives(String archives) {
        this.archives = archives;
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
        dest.writeInt(totalCount);
        dest.writeString(sign);
        dest.writeInt(fans);
        dest.writeString(archives);
        dest.writeInt(status);
    }
}
