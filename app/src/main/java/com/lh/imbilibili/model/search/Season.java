package com.lh.imbilibili.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/6.
 */

public class Season implements Parcelable {
    private String title;
    private String cover;
    private String uri;
    private String param;
    @SerializedName("goto")
    private String go_to;
    private int finish;
    private String index;
    @SerializedName("newest_cat")
    private String newestCat;
    @SerializedName("newest_season")
    private String newestSeason;
    @SerializedName("cat_desc")
    private String catDesc;
    @SerializedName("total_count")
    private int totalCount;
    private String desc;
    private int status;

    public Season() {
    }

    protected Season(Parcel in) {
        title = in.readString();
        cover = in.readString();
        uri = in.readString();
        param = in.readString();
        go_to = in.readString();
        finish = in.readInt();
        index = in.readString();
        newestCat = in.readString();
        newestSeason = in.readString();
        catDesc = in.readString();
        totalCount = in.readInt();
        desc = in.readString();
        status = in.readInt();
    }

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
        }
    };

    public String getGo_to() {
        return go_to;
    }

    public void setGo_to(String go_to) {
        this.go_to = go_to;
    }

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

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getNewestCat() {
        return newestCat;
    }

    public void setNewestCat(String newestCat) {
        this.newestCat = newestCat;
    }

    public String getNewestSeason() {
        return newestSeason;
    }

    public void setNewestSeason(String newestSeason) {
        this.newestSeason = newestSeason;
    }

    public String getCatDesc() {
        return catDesc;
    }

    public void setCatDesc(String catDesc) {
        this.catDesc = catDesc;
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
        dest.writeInt(finish);
        dest.writeString(index);
        dest.writeString(newestCat);
        dest.writeString(newestSeason);
        dest.writeString(catDesc);
        dest.writeInt(totalCount);
        dest.writeString(desc);
        dest.writeInt(status);
    }
}