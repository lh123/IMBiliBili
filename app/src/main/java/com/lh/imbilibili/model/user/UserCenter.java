package com.lh.imbilibili.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhui on 2016/10/15.
 * 用户中心数据
 */

public class UserCenter {
    private int relation;
    private Setting setting;
    private Image images;
    private Card card;
    private CenterList<Archive> archive;
    private CenterList<Community> community;
    private CenterList<Season> season;
    private CenterList<Favourite> favourite;
    @SerializedName("coin_archive")
    private CenterList<Archive> coinArchive;
    private CenterList<Game> game;

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public CenterList<Archive> getArchive() {
        return archive;
    }

    public void setArchive(CenterList<Archive> archive) {
        this.archive = archive;
    }

    public CenterList<Community> getCommunity() {
        return community;
    }

    public void setCommunity(CenterList<Community> community) {
        this.community = community;
    }

    public CenterList<Season> getSeason() {
        return season;
    }

    public void setSeason(CenterList<Season> season) {
        this.season = season;
    }

    public CenterList<Favourite> getFavourite() {
        return favourite;
    }

    public void setFavourite(CenterList<Favourite> favourite) {
        this.favourite = favourite;
    }

    public CenterList<Archive> getCoinArchive() {
        return coinArchive;
    }

    public void setCoinArchive(CenterList<Archive> coinArchive) {
        this.coinArchive = coinArchive;
    }

    public CenterList<Game> getGame() {
        return game;
    }

    public void setGame(CenterList<Game> game) {
        this.game = game;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public static class Card {
        private String mid;
        private String name;
        private boolean approve;
        private String sex;
        private String rank;
        private String face;
        @SerializedName("DisplayRank")
        private String displayRank;
        private long regtime;
        private String birthday;
        private String place;
        private String description;
        private int article;
        private int[] attentions;
        private int fans;
        private int friend;
        private int attention;
        private String sign;
        @SerializedName("level_info")
        private LevelInfo levelInfo;
        @SerializedName("official_verify")
        private OfficialVerify officialVerify;

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isApprove() {
            return approve;
        }

        public void setApprove(boolean approve) {
            this.approve = approve;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        public String getDisplayRank() {
            return displayRank;
        }

        public void setDisplayRank(String displayRank) {
            this.displayRank = displayRank;
        }

        public long getRegtime() {
            return regtime;
        }

        public void setRegtime(long regtime) {
            this.regtime = regtime;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getArticle() {
            return article;
        }

        public void setArticle(int article) {
            this.article = article;
        }

        public int[] getAttentions() {
            return attentions;
        }

        public void setAttentions(int[] attentions) {
            this.attentions = attentions;
        }

        public int getFans() {
            return fans;
        }

        public void setFans(int fans) {
            this.fans = fans;
        }

        public int getFriend() {
            return friend;
        }

        public void setFriend(int friend) {
            this.friend = friend;
        }

        public int getAttention() {
            return attention;
        }

        public void setAttention(int attention) {
            this.attention = attention;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public LevelInfo getLevelInfo() {
            return levelInfo;
        }

        public void setLevelInfo(LevelInfo levelInfo) {
            this.levelInfo = levelInfo;
        }

        public OfficialVerify getOfficialVerify() {
            return officialVerify;
        }

        public void setOfficialVerify(OfficialVerify officialVerify) {
            this.officialVerify = officialVerify;
        }
    }

    public static class Image {
        private String imgUrl;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }

    public static class CenterList<T> {
        private int count;
        private ArrayList<T> item;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<T> getItem() {
            return item;
        }

        public void setItem(ArrayList<T> item) {
            this.item = item;
        }
    }

    public static class Archive {
        /*"title":"[锵锵三人行] 20161014期 许子东:杜特尔特为何对中国频频示好？",
                    "cover":"http://i0.hdslb.com/bfs/archive/7b2920660b186de121306fa8781f5e434ba93686.png",
                    "uri":"bilibili://video/6683575",
                    "param":"6683575",
                    "goto":"av",
                    "play":629,
                    "danmaku":10*/
        private String title;
        private String cover;
        private String uri;
        private String param;
        @SerializedName("goto")
        private String go_to;
        private int play;
        private int danmaku;

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

        public String getGo_to() {
            return go_to;
        }

        public void setGo_to(String go_to) {
            this.go_to = go_to;
        }

        public int getPlay() {
            return play;
        }

        public void setPlay(int play) {
            this.play = play;
        }

        public int getDanmaku() {
            return danmaku;
        }

        public void setDanmaku(int danmaku) {
            this.danmaku = danmaku;
        }
    }

    public static class Community {
        /*"id":3034,
                    "name":"新番",
                    "desc":"新番动漫资源，资讯，吐槽，追番党的大家庭",
                    "thumb":"http://img.yo9.com/02bad680c57611e5803700163e000cde",
                    "post_count":2509,
                    "member_count":115177,
                    "post_nickname":"弹幕",
                    "member_nickname":"追番党"*/
        private int id;
        private String name;
        private String desc;
        private String thumb;
        @SerializedName("post_count")
        private int postCount;
        @SerializedName("member_count")
        private int memberCount;
        @SerializedName("post_nickname")
        private String postNickname;
        @SerializedName("member_nickname")
        private String memberNickname;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public int getPostCount() {
            return postCount;
        }

        public void setPostCount(int postCount) {
            this.postCount = postCount;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public String getPostNickname() {
            return postNickname;
        }

        public void setPostNickname(String postNickname) {
            this.postNickname = postNickname;
        }

        public String getMemberNickname() {
            return memberNickname;
        }

        public void setMemberNickname(String memberNickname) {
            this.memberNickname = memberNickname;
        }
    }

    public static class Season {
        /*"title":"3月的狮子",
                "cover":"http://i0.hdslb.com/bfs/bangumi/7bfd5b9a4aabee8df09df12939d2f32c2f41a0d7.jpg",
                "uri":"bilibili://bangumi/season/5523",
                "param":"5523",
                "goto":"bangumi",
                "newest_ep_index":"2",
                "total_count":"22",
                "attention":"0"*/
        private String title;
        private String cover;
        private String uri;
        private String param;
        @SerializedName("goto")
        private String go_to;
        @SerializedName("newest_ep_index")
        private String newestEpIndex;
        @SerializedName("total_count")
        private String totalCount;
        private String attention;

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

        public String getGo_to() {
            return go_to;
        }

        public void setGo_to(String go_to) {
            this.go_to = go_to;
        }

        public String getNewestEpIndex() {
            return newestEpIndex;
        }

        public void setNewestEpIndex(String newestEpIndex) {
            this.newestEpIndex = newestEpIndex;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public String getAttention() {
            return attention;
        }

        public void setAttention(String attention) {
            this.attention = attention;
        }
    }

    public static class Favourite {
        /*"fid":7220618,
                    "mid":6460141,
                    "name":"默认收藏夹",
                    "max_count":200,
                    "cur_count":4,
                    "atten_count":0,
                    "state":0,
                    "ctime":1438922096*/
        private int fid;
        private int mid;
        private String name;
        @SerializedName("max_count")
        private int maxCount;
        @SerializedName("cur_count")
        private int curCount;
        @SerializedName("atten_count")
        private int attenCount;
        private long ctime;
        private List<Video> videos;

        public int getFid() {
            return fid;
        }

        public void setFid(int fid) {
            this.fid = fid;
        }

        public int getMid() {
            return mid;
        }

        public void setMid(int mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        public int getCurCount() {
            return curCount;
        }

        public void setCurCount(int curCount) {
            this.curCount = curCount;
        }

        public int getAttenCount() {
            return attenCount;
        }

        public void setAttenCount(int attenCount) {
            this.attenCount = attenCount;
        }

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public List<Video> getVideos() {
            return videos;
        }

        public void setVideos(List<Video> videos) {
            this.videos = videos;
        }

        public static class Video {
            /* "aid":4289448,
                            "pic":"http://i1.hdslb.com/bfs/archive/72d2c10340b01d0da3a4d3708359e5324019d4d2.jpg"*/
            private int aid;
            private String pic;

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public int getAid() {
                return aid;
            }

            public void setAid(int aid) {
                this.aid = aid;
            }
        }
    }

    public static class Game {
        /*"uri":"com.netease.onmyoji.bili",
                    "id":55,
                    "name":"阴阳师",
                    "icon":"http://i0.hdslb.com/bfs/game/36ee7db87a0d27aa5bb4672fffc22fb5ff8ec68c.png",
                    "summary":"唯美如樱，百鬼物语"*/
        private String uri;
        private int id;
        private String name;
        private String icon;
        private String summary;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    public static class Setting {
        /*"tags":0,
            "fav_video":0,
            "coins_video":0,
            "bangumi":0,
            "played_game":0,
            "groups":0*/
        private int tags;
        @SerializedName("fav_video")
        private int favVideo;
        @SerializedName("coins_video")
        private int coinsVideo;
        private int bangumi;
        @SerializedName("played_game")
        private int playedGame;
        private int groups;

        public int getTags() {
            return tags;
        }

        public void setTags(int tags) {
            this.tags = tags;
        }

        public int getFavVideo() {
            return favVideo;
        }

        public void setFavVideo(int favVideo) {
            this.favVideo = favVideo;
        }

        public int getBangumi() {
            return bangumi;
        }

        public void setBangumi(int bangumi) {
            this.bangumi = bangumi;
        }

        public int getPlayedGame() {
            return playedGame;
        }

        public void setPlayedGame(int playedGame) {
            this.playedGame = playedGame;
        }

        public int getGroups() {
            return groups;
        }

        public void setGroups(int groups) {
            this.groups = groups;
        }

        public int getCoinsVideo() {
            return coinsVideo;
        }

        public void setCoinsVideo(int coinsVideo) {
            this.coinsVideo = coinsVideo;
        }
    }
}
