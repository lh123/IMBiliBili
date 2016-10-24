package com.lh.imbilibili.model.bangumi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by home on 2016/7/30.
 */
public class Tag implements Parcelable {
    private String cover;
    @SerializedName("tag_id")
    private String tagId;
    @SerializedName("tag_name")
    private String tagName;

    public Tag() {
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cover);
        dest.writeString(this.tagId);
        dest.writeString(this.tagName);
    }

    protected Tag(Parcel in) {
        this.cover = in.readString();
        this.tagId = in.readString();
        this.tagName = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}
