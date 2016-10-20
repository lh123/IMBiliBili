package com.lh.imbilibili.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by liuhui on 2016/10/5.
 */

public class SearchResult implements Parcelable {
    private int page;
    private List<Nav> nav;
    private Item items;

    public SearchResult() {
    }

    protected SearchResult(Parcel in) {
        page = in.readInt();
        nav = in.createTypedArrayList(Nav.CREATOR);
        items = in.readParcelable(Item.class.getClassLoader());
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Nav> getNav() {
        return nav;
    }

    public void setNav(List<Nav> nav) {
        this.nav = nav;
    }

    public Item getItems() {
        return items;
    }

    public void setItems(Item items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeTypedList(nav);
        dest.writeParcelable(items, flags);
    }


    public static class Item implements Parcelable {
        private List<Season> season;
        private List<Movie> movie;
        private List<Archive> archive;

        public Item() {
        }

        protected Item(Parcel in) {
            season = in.createTypedArrayList(Season.CREATOR);
            movie = in.createTypedArrayList(Movie.CREATOR);
            archive = in.createTypedArrayList(Archive.CREATOR);
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        public List<Season> getSeason() {
            return season;
        }

        public void setSeason(List<Season> season) {
            this.season = season;
        }

        public List<Movie> getMovie() {
            return movie;
        }

        public void setMovie(List<Movie> movie) {
            this.movie = movie;
        }

        public List<Archive> getArchive() {
            return archive;
        }

        public void setArchive(List<Archive> archive) {
            this.archive = archive;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(season);
            dest.writeTypedList(movie);
            dest.writeTypedList(archive);
        }
    }
}
