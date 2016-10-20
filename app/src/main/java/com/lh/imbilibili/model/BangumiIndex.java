package com.lh.imbilibili.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by home on 2016/8/11.
 */
public class BangumiIndex implements Parcelable {
    private String count;
    private List<Bangumi> list;
    private String pages;

    public BangumiIndex() {
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<Bangumi> getList() {
        return list;
    }

    public void setList(List<Bangumi> list) {
        this.list = list;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.count);
        dest.writeTypedList(this.list);
        dest.writeString(this.pages);
    }

    protected BangumiIndex(Parcel in) {
        this.count = in.readString();
        this.list = in.createTypedArrayList(Bangumi.CREATOR);
        this.pages = in.readString();
    }

    public static final Creator<BangumiIndex> CREATOR = new Creator<BangumiIndex>() {
        @Override
        public BangumiIndex createFromParcel(Parcel source) {
            return new BangumiIndex(source);
        }

        @Override
        public BangumiIndex[] newArray(int size) {
            return new BangumiIndex[size];
        }
    };
}
