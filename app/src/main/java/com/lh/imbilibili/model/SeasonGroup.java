package com.lh.imbilibili.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by home on 2016/8/8.
 */
public class SeasonGroup implements Parcelable {
    private List<Bangumi> list;
    private int season;
    private int year;

    public SeasonGroup() {
    }

    public List<Bangumi> getList() {
        return list;
    }

    public void setList(List<Bangumi> list) {
        this.list = list;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
        dest.writeInt(this.season);
        dest.writeInt(this.year);
    }

    protected SeasonGroup(Parcel in) {
        this.list = in.createTypedArrayList(Bangumi.CREATOR);
        this.season = in.readInt();
        this.year = in.readInt();
    }

    public static final Creator<SeasonGroup> CREATOR = new Creator<SeasonGroup>() {
        @Override
        public SeasonGroup createFromParcel(Parcel source) {
            return new SeasonGroup(source);
        }

        @Override
        public SeasonGroup[] newArray(int size) {
            return new SeasonGroup[size];
        }
    };
}
