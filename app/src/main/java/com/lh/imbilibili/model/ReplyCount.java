package com.lh.imbilibili.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by home on 2016/8/3.
 */
public class ReplyCount implements Parcelable {
    private int count;

    public ReplyCount() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
    }

    protected ReplyCount(Parcel in) {
        this.count = in.readInt();
    }

    public static final Creator<ReplyCount> CREATOR = new Creator<ReplyCount>() {
        @Override
        public ReplyCount createFromParcel(Parcel source) {
            return new ReplyCount(source);
        }

        @Override
        public ReplyCount[] newArray(int size) {
            return new ReplyCount[size];
        }
    };
}
