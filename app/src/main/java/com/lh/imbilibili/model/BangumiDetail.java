package com.lh.imbilibili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by home on 2016/7/30.
 */
@SuppressWarnings("unused")
public class BangumiDetail implements Parcelable {

    private List<Actor> actor;
    private String alias;
    @SerializedName("allow_bp")
    private String allowBp;
    @SerializedName("allow_download")
    private String allowDownload;
    private String area;
    private int arealimit;
    @SerializedName("bangumi_id")
    private String bangumiId;
    @SerializedName("bangumi_title")
    private String bangumiTitle;
    private String brief;
    private String coins;
    private String copyright;
    private String cover;
    @SerializedName("danmaku_count")
    private String danmakuCount;
    private List<Episode> episodes;
    private String evaluate;
    private String favorites;
    @SerializedName("is_finish")
    private String isFinish;
    @SerializedName("jp_title")
    private String jpTitle;
    @SerializedName("newest_ep_id")
    private String newestEpId;
    @SerializedName("newest_ep_index")
    private String newestEpIndex;
    @SerializedName("play_count")
    private String playCount;
    @SerializedName("play_time")
    private String playTime;
    private Rank rank;
    //    @SerializedName("related_season")
//    private Object relatedSeason;
    @SerializedName("season_id")
    private String seasonId;
    @SerializedName("season_title")
    private String seasonTitle;
    private List<Bangumi> seasons;
    @SerializedName("share_url")
    private String shareUrl;
    @SerializedName("square_cover")
    private String squareCover;
    private String staff;
    private Object tag2s;
    private List<Tag> tags;
    private String title;
    @SerializedName("total_count")
    private String totalCount;
    private int viewRank;
    private String watchingCount;
    private String weekday;

    public BangumiDetail() {
    }

    public List<Actor> getActor() {
        return actor;
    }

    public void setActor(List<Actor> actor) {
        this.actor = actor;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAllowBp() {
        return allowBp;
    }

    public void setAllowBp(String allowBp) {
        this.allowBp = allowBp;
    }

    public String getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(String allowDownload) {
        this.allowDownload = allowDownload;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getArealimit() {
        return arealimit;
    }

    public void setArealimit(int arealimit) {
        this.arealimit = arealimit;
    }

    public String getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(String bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getBangumiTitle() {
        return bangumiTitle;
    }

    public void setBangumiTitle(String bangumiTitle) {
        this.bangumiTitle = bangumiTitle;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDanmakuCount() {
        return danmakuCount;
    }

    public void setDanmakuCount(String danmakuCount) {
        this.danmakuCount = danmakuCount;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getJpTitle() {
        return jpTitle;
    }

    public void setJpTitle(String jpTitle) {
        this.jpTitle = jpTitle;
    }

    public String getNewestEpId() {
        return newestEpId;
    }

    public void setNewestEpId(String newestEpId) {
        this.newestEpId = newestEpId;
    }

    public String getNewestEpIndex() {
        return newestEpIndex;
    }

    public void setNewestEpIndex(String newestEpIndex) {
        this.newestEpIndex = newestEpIndex;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getSeasonTitle() {
        return seasonTitle;
    }

    public void setSeasonTitle(String seasonTitle) {
        this.seasonTitle = seasonTitle;
    }

    public List<Bangumi> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Bangumi> seasons) {
        this.seasons = seasons;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getSquareCover() {
        return squareCover;
    }

    public void setSquareCover(String squareCover) {
        this.squareCover = squareCover;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public Object getTag2s() {
        return tag2s;
    }

    public void setTag2s(Object tag2s) {
        this.tag2s = tag2s;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public int getViewRank() {
        return viewRank;
    }

    public void setViewRank(int viewRank) {
        this.viewRank = viewRank;
    }

    public String getWatchingCount() {
        return watchingCount;
    }

    public void setWatchingCount(String watchingCount) {
        this.watchingCount = watchingCount;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public static class Actor implements Parcelable {
        public static final Creator<Actor> CREATOR = new Creator<Actor>() {
            @Override
            public Actor createFromParcel(Parcel source) {
                return new Actor(source);
            }

            @Override
            public Actor[] newArray(int size) {
                return new Actor[size];
            }
        };
        private String actor;
        @SerializedName("actor_id")
        private int actorId;
        private String role;

        public Actor() {
        }

        protected Actor(Parcel in) {
            this.actor = in.readString();
            this.actorId = in.readInt();
            this.role = in.readString();
        }

        public String getActor() {
            return actor;
        }

        public void setActor(String actor) {
            this.actor = actor;
        }

        public int getActorId() {
            return actorId;
        }

        public void setActorId(int actorId) {
            this.actorId = actorId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.actor);
            dest.writeInt(this.actorId);
            dest.writeString(this.role);
        }
    }

    public static class Episode implements Parcelable {
        public static final Creator<Episode> CREATOR = new Creator<Episode>() {
            @Override
            public Episode createFromParcel(Parcel source) {
                return new Episode(source);
            }

            @Override
            public Episode[] newArray(int size) {
                return new Episode[size];
            }
        };
        @SerializedName("av_id")
        private String avId;
        private String coins;
        private String cover;
        private String danmaku;
        @SerializedName("episode_id")
        private String episodeId;
        private String index;
        @SerializedName("index_title")
        private String indexTitle;
        @SerializedName("is_new")
        private String isNew;
        @SerializedName("is_webplay")
        private String isWebplay;
        private String mid;
        private String page;
        @SerializedName("update_time")
        private String updateTime;

        public Episode() {
        }

        protected Episode(Parcel in) {
            this.avId = in.readString();
            this.coins = in.readString();
            this.cover = in.readString();
            this.danmaku = in.readString();
            this.episodeId = in.readString();
            this.index = in.readString();
            this.indexTitle = in.readString();
            this.isNew = in.readString();
            this.isWebplay = in.readString();
            this.mid = in.readString();
            this.page = in.readString();
            this.updateTime = in.readString();
        }

        public String getAvId() {
            return avId;
        }

        public void setAvId(String avId) {
            this.avId = avId;
        }

        public String getCoins() {
            return coins;
        }

        public void setCoins(String coins) {
            this.coins = coins;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDanmaku() {
            return danmaku;
        }

        public void setDanmaku(String danmaku) {
            this.danmaku = danmaku;
        }

        public String getEpisodeId() {
            return episodeId;
        }

        public void setEpisodeId(String episodeId) {
            this.episodeId = episodeId;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getIndexTitle() {
            return indexTitle;
        }

        public void setIndexTitle(String indexTitle) {
            this.indexTitle = indexTitle;
        }

        public String getIsNew() {
            return isNew;
        }

        public void setIsNew(String isNew) {
            this.isNew = isNew;
        }

        public String getIsWebplay() {
            return isWebplay;
        }

        public void setIsWebplay(String isWebplay) {
            this.isWebplay = isWebplay;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.avId);
            dest.writeString(this.coins);
            dest.writeString(this.cover);
            dest.writeString(this.danmaku);
            dest.writeString(this.episodeId);
            dest.writeString(this.index);
            dest.writeString(this.indexTitle);
            dest.writeString(this.isNew);
            dest.writeString(this.isWebplay);
            dest.writeString(this.mid);
            dest.writeString(this.page);
            dest.writeString(this.updateTime);
        }
    }

    public static class Rank implements Parcelable {
        public static final Creator<Rank> CREATOR = new Creator<Rank>() {
            @Override
            public Rank createFromParcel(Parcel source) {
                return new Rank(source);
            }

            @Override
            public Rank[] newArray(int size) {
                return new Rank[size];
            }
        };
        private List<RankUser> list;
        @SerializedName("total_bp_count")
        private int totalBpCount;
        @SerializedName("week_bp_count")
        private int weekBpCount;

        public Rank() {
        }

        protected Rank(Parcel in) {
            this.list = new ArrayList<RankUser>();
            in.readList(this.list, RankUser.class.getClassLoader());
            this.totalBpCount = in.readInt();
            this.weekBpCount = in.readInt();
        }

        public List<RankUser> getList() {
            return list;
        }

        public void setList(List<RankUser> list) {
            this.list = list;
        }

        public int getTotalBpCount() {
            return totalBpCount;
        }

        public void setTotalBpCount(int totalBpCount) {
            this.totalBpCount = totalBpCount;
        }

        public int getWeekBpCount() {
            return weekBpCount;
        }

        public void setWeekBpCount(int weekBpCount) {
            this.weekBpCount = weekBpCount;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(this.list);
            dest.writeInt(this.totalBpCount);
            dest.writeInt(this.weekBpCount);
        }

        public static class RankUser implements Parcelable {
            public static final Creator<RankUser> CREATOR = new Creator<RankUser>() {
                @Override
                public RankUser createFromParcel(Parcel source) {
                    return new RankUser(source);
                }

                @Override
                public RankUser[] newArray(int size) {
                    return new RankUser[size];
                }
            };
            private String face;
            private String uid;
            private String uname;

            public RankUser() {
            }

            protected RankUser(Parcel in) {
                this.face = in.readString();
                this.uid = in.readString();
                this.uname = in.readString();
            }

            public String getFace() {
                return face;
            }

            public void setFace(String face) {
                this.face = face;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getUname() {
                return uname;
            }

            public void setUname(String uname) {
                this.uname = uname;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.face);
                dest.writeString(this.uid);
                dest.writeString(this.uname);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.actor);
        dest.writeString(this.alias);
        dest.writeString(this.allowBp);
        dest.writeString(this.allowDownload);
        dest.writeString(this.area);
        dest.writeInt(this.arealimit);
        dest.writeString(this.bangumiId);
        dest.writeString(this.bangumiTitle);
        dest.writeString(this.brief);
        dest.writeString(this.coins);
        dest.writeString(this.copyright);
        dest.writeString(this.cover);
        dest.writeString(this.danmakuCount);
        dest.writeTypedList(this.episodes);
        dest.writeString(this.evaluate);
        dest.writeString(this.favorites);
        dest.writeString(this.isFinish);
        dest.writeString(this.jpTitle);
        dest.writeString(this.newestEpId);
        dest.writeString(this.newestEpIndex);
        dest.writeString(this.playCount);
        dest.writeString(this.playTime);
        dest.writeParcelable(this.rank, flags);
        dest.writeString(this.seasonId);
        dest.writeString(this.seasonTitle);
        dest.writeTypedList(this.seasons);
        dest.writeString(this.shareUrl);
        dest.writeString(this.squareCover);
        dest.writeString(this.staff);
        dest.writeTypedList(this.tags);
        dest.writeString(this.title);
        dest.writeString(this.totalCount);
        dest.writeInt(this.viewRank);
        dest.writeString(this.watchingCount);
        dest.writeString(this.weekday);
    }

    protected BangumiDetail(Parcel in) {
        this.actor = in.createTypedArrayList(Actor.CREATOR);
        this.alias = in.readString();
        this.allowBp = in.readString();
        this.allowDownload = in.readString();
        this.area = in.readString();
        this.arealimit = in.readInt();
        this.bangumiId = in.readString();
        this.bangumiTitle = in.readString();
        this.brief = in.readString();
        this.coins = in.readString();
        this.copyright = in.readString();
        this.cover = in.readString();
        this.danmakuCount = in.readString();
        this.episodes = in.createTypedArrayList(Episode.CREATOR);
        this.evaluate = in.readString();
        this.favorites = in.readString();
        this.isFinish = in.readString();
        this.jpTitle = in.readString();
        this.newestEpId = in.readString();
        this.newestEpIndex = in.readString();
        this.playCount = in.readString();
        this.playTime = in.readString();
        this.rank = in.readParcelable(Rank.class.getClassLoader());
        this.seasonId = in.readString();
        this.seasonTitle = in.readString();
        this.seasons = in.createTypedArrayList(Bangumi.CREATOR);
        this.shareUrl = in.readString();
        this.squareCover = in.readString();
        this.staff = in.readString();
        this.tags = in.createTypedArrayList(Tag.CREATOR);
        this.title = in.readString();
        this.totalCount = in.readString();
        this.viewRank = in.readInt();
        this.watchingCount = in.readString();
        this.weekday = in.readString();
    }

    public static final Creator<BangumiDetail> CREATOR = new Creator<BangumiDetail>() {
        @Override
        public BangumiDetail createFromParcel(Parcel source) {
            return new BangumiDetail(source);
        }

        @Override
        public BangumiDetail[] newArray(int size) {
            return new BangumiDetail[size];
        }
    };
}
