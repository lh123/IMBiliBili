package com.lh.imbilibili.model.search;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuhui on 2016/10/6.
 */

public class Nav implements Parcelable {
    private String name;
    private int total;
    private int pages;
    private int type;

    public Nav() {
    }

    protected Nav(Parcel in) {
        name = in.readString();
        total = in.readInt();
        pages = in.readInt();
        type = in.readInt();
    }

    public static final Creator<Nav> CREATOR = new Creator<Nav>() {
        @Override
        public Nav createFromParcel(Parcel in) {
            return new Nav(in);
        }

        @Override
        public Nav[] newArray(int size) {
            return new Nav[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(total);
        dest.writeInt(pages);
        dest.writeInt(type);
    }
}
