package com.lh.imbilibili.model.attention;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/10.
 */

public class FollowBangumi {
    private String brief;
    private String cover;
    private String favorites;
    @SerializedName("is_finish")
    private String isFinish; //1完结
    private int limitGroupId;
    @SerializedName("new_ep")
    private Ep newEp;
    @SerializedName("season_id")
    private String seasonId;
    @SerializedName("season_status")
    private int seasonStatus;
    private String squareCover;
    private String title;
    @SerializedName("total_count")
    private String totalCount;
    @SerializedName("user_season")
    private UserSeason userSeason;
    private String weekday;

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getLimitGroupId() {
        return limitGroupId;
    }

    public void setLimitGroupId(int limitGroupId) {
        this.limitGroupId = limitGroupId;
    }

    public Ep getNewEp() {
        return newEp;
    }

    public void setNewEp(Ep newEp) {
        this.newEp = newEp;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public int getSeasonStatus() {
        return seasonStatus;
    }

    public void setSeasonStatus(int seasonStatus) {
        this.seasonStatus = seasonStatus;
    }

    public String getSquareCover() {
        return squareCover;
    }

    public void setSquareCover(String squareCover) {
        this.squareCover = squareCover;
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

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public static class Ep {
        @SerializedName("episode_id")
        private String episodeId;
        @SerializedName("episode_status")
        private int EpisodeStatus;
        private String index;
        @SerializedName("update_time")
        private String updateTime;

        public String getEpisodeId() {
            return episodeId;
        }

        public void setEpisodeId(String episodeId) {
            this.episodeId = episodeId;
        }

        public int getEpisodeStatus() {
            return EpisodeStatus;
        }

        public void setEpisodeStatus(int episodeStatus) {
            EpisodeStatus = episodeStatus;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class UserSeason {
        private String attention;
        @SerializedName("last_ep_id")
        private String lastEpId;
        @SerializedName("last_ep_index")
        private String lastEpIndex;
        @SerializedName("last_time")
        private String lastTime;

        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
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
    }
}
