package com.lh.imbilibili.model.bangumi;

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
    @SerializedName("user_season")
    private UserSeason userSeason;
    private int viewRank;
    private String watchingCount;
    private String weekday;

    protected BangumiDetail(Parcel in) {
        actor = in.createTypedArrayList(Actor.CREATOR);
        alias = in.readString();
        allowBp = in.readString();
        allowDownload = in.readString();
        area = in.readString();
        arealimit = in.readInt();
        bangumiId = in.readString();
        bangumiTitle = in.readString();
        brief = in.readString();
        coins = in.readString();
        copyright = in.readString();
        cover = in.readString();
        danmakuCount = in.readString();
        episodes = in.createTypedArrayList(Episode.CREATOR);
        evaluate = in.readString();
        favorites = in.readString();
        isFinish = in.readString();
        jpTitle = in.readString();
        newestEpId = in.readString();
        newestEpIndex = in.readString();
        playCount = in.readString();
        playTime = in.readString();
        rank = in.readParcelable(Rank.class.getClassLoader());
        seasonId = in.readString();
        seasonTitle = in.readString();
        seasons = in.createTypedArrayList(Bangumi.CREATOR);
        shareUrl = in.readString();
        squareCover = in.readString();
        staff = in.readString();
        tags = in.createTypedArrayList(Tag.CREATOR);
        title = in.readString();
        totalCount = in.readString();
        userSeason = in.readParcelable(UserSeason.class.getClassLoader());
        viewRank = in.readInt();
        watchingCount = in.readString();
        weekday = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(actor);
        dest.writeString(alias);
        dest.writeString(allowBp);
        dest.writeString(allowDownload);
        dest.writeString(area);
        dest.writeInt(arealimit);
        dest.writeString(bangumiId);
        dest.writeString(bangumiTitle);
        dest.writeString(brief);
        dest.writeString(coins);
        dest.writeString(copyright);
        dest.writeString(cover);
        dest.writeString(danmakuCount);
        dest.writeTypedList(episodes);
        dest.writeString(evaluate);
        dest.writeString(favorites);
        dest.writeString(isFinish);
        dest.writeString(jpTitle);
        dest.writeString(newestEpId);
        dest.writeString(newestEpIndex);
        dest.writeString(playCount);
        dest.writeString(playTime);
        dest.writeParcelable(rank, flags);
        dest.writeString(seasonId);
        dest.writeString(seasonTitle);
        dest.writeTypedList(seasons);
        dest.writeString(shareUrl);
        dest.writeString(squareCover);
        dest.writeString(staff);
        dest.writeTypedList(tags);
        dest.writeString(title);
        dest.writeString(totalCount);
        dest.writeParcelable(userSeason, flags);
        dest.writeInt(viewRank);
        dest.writeString(watchingCount);
        dest.writeString(weekday);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BangumiDetail> CREATOR = new Creator<BangumiDetail>() {
        @Override
        public BangumiDetail createFromParcel(Parcel in) {
            return new BangumiDetail(in);
        }

        @Override
        public BangumiDetail[] newArray(int size) {
            return new BangumiDetail[size];
        }
    };

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

    public UserSeason getUserSeason() {
        return userSeason;
    }

    public void setUserSeason(UserSeason userSeason) {
        this.userSeason = userSeason;
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

    public static class UserSeason implements Parcelable {
        private String attention;
        private int bp;
        private String lastEpId;
        private String lastEpIndex;
        private String lastTime;

        protected UserSeason(Parcel in) {
            attention = in.readString();
            bp = in.readInt();
            lastEpId = in.readString();
            lastEpIndex = in.readString();
            lastTime = in.readString();
        }

        public static final Creator<UserSeason> CREATOR = new Creator<UserSeason>() {
            @Override
            public UserSeason createFromParcel(Parcel in) {
                return new UserSeason(in);
            }

            @Override
            public UserSeason[] newArray(int size) {
                return new UserSeason[size];
            }
        };

        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
        }

        public int getBp() {
            return bp;
        }

        public void setBp(int bp) {
            this.bp = bp;
        }

        public String getLastEpId() {
            return lastEpId;
        }

        public void setLastEpId(String lastEpId) {
            this.lastEpId = lastEpId;
        }

        public String getLastEpIndex() {
            return lastEpIndex;
        }

        public void setLastEpIndex(String lastEpIndex) {
            this.lastEpIndex = lastEpIndex;
        }

        public String getLastTime() {
            return lastTime;
        }

        public void setLastTime(String lastTime) {
            this.lastTime = lastTime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(attention);
            dest.writeInt(bp);
            dest.writeString(lastEpId);
            dest.writeString(lastEpIndex);
            dest.writeString(lastTime);
        }
    }
}
