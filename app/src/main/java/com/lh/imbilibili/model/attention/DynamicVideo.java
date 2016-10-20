package com.lh.imbilibili.model.attention;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuhui on 2016/10/14.
 */

@SuppressWarnings("ALL")
public class DynamicVideo {
    private List<Feed> feeds;
    private Page page;

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public static class Feed {
        private int id;
        @SerializedName("src_id")
        private int srcId;
        @SerializedName("add_id")
        private int addId;
        private int type;
        private long mcid;
        private long ctime;
        private Source source;
        private Addition addition;
        private Content content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSrcId() {
            return srcId;
        }

        public void setSrcId(int srcId) {
            this.srcId = srcId;
        }

        public int getAddId() {
            return addId;
        }

        public void setAddId(int addId) {
            this.addId = addId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getMcid() {
            return mcid;
        }

        public void setMcid(long mcid) {
            this.mcid = mcid;
        }

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public Source getSource() {
            return source;
        }

        public void setSource(Source source) {
            this.source = source;
        }

        public Addition getAddition() {
            return addition;
        }

        public void setAddition(Addition addition) {
            this.addition = addition;
        }

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public static class Source {
            //video type1
            private int mid;
            private String uname;
            private String sex;
            private String sign;
            private String avatar;

            public int getMid() {
                return mid;
            }

            public void setMid(int mid) {
                this.mid = mid;
            }

            public String getUname() {
                return uname;
            }

            public void setUname(String uname) {
                this.uname = uname;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            //bangumi type3
            @SerializedName("season_id")
            private String seasonId;
            private String spid;
            private String title;
            private String brief;
            private String cover;
            private String evaluate;
            @SerializedName("total_count")
            private String totalCount;
            @SerializedName("new_ep")
            private Ep newEp;

            public String getSeasonId() {
                return seasonId;
            }

            public void setSeasonId(String seasonId) {
                this.seasonId = seasonId;
            }

            public String getSpid() {
                return spid;
            }

            public void setSpid(String spid) {
                this.spid = spid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

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

            public String getEvaluate() {
                return evaluate;
            }

            public void setEvaluate(String evaluate) {
                this.evaluate = evaluate;
            }

            public String getTotalCount() {
                return totalCount;
            }

            public void setTotalCount(String totalCount) {
                this.totalCount = totalCount;
            }

            public Ep getNewEp() {
                return newEp;
            }

            public void setNewEp(Ep newEp) {
                this.newEp = newEp;
            }

            public static class Ep {
                @SerializedName("av_id")
                private String avId;
                private String cover;
                private String index;
                @SerializedName("update_time")
                private String updateTime;

                public String getAvId() {
                    return avId;
                }

                public void setAvId(String avId) {
                    this.avId = avId;
                }

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
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
        }

        public static class Addition {
            private int aid;
            private int typeid;
            private String typename;
            private String title;
            private String subtitle;
            private int play;
            private int review;
            @SerializedName("video_review")
            private int videoReview;
            private int favorites;
            private int mid;
            private String author;
            private String link;
            private String keywords;
            private String description;
            private String creat;
            private String pic;
            private int credit;
            private int coins;
            private int money;
            private String duration;
            private int status;
            private int view;
            @SerializedName("view_at")
            private String viewAt;
            @SerializedName("fav_craete")
            private int favCreate;
            @SerializedName("fav_craete_at")
            private int favCreateAt;
            private String flag;

            public int getAid() {
                return aid;
            }

            public void setAid(int aid) {
                this.aid = aid;
            }

            public int getTypeid() {
                return typeid;
            }

            public void setTypeid(int typeid) {
                this.typeid = typeid;
            }

            public String getTypename() {
                return typename;
            }

            public void setTypename(String typename) {
                this.typename = typename;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSubtitle() {
                return subtitle;
            }

            public void setSubtitle(String subtitle) {
                this.subtitle = subtitle;
            }

            public int getPlay() {
                return play;
            }

            public void setPlay(int play) {
                this.play = play;
            }

            public int getReview() {
                return review;
            }

            public void setReview(int review) {
                this.review = review;
            }

            public int getVideoReview() {
                return videoReview;
            }

            public void setVideoReview(int videoReview) {
                this.videoReview = videoReview;
            }

            public int getFavorites() {
                return favorites;
            }

            public void setFavorites(int favorites) {
                this.favorites = favorites;
            }

            public int getMid() {
                return mid;
            }

            public void setMid(int mid) {
                this.mid = mid;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getKeywords() {
                return keywords;
            }

            public void setKeywords(String keywords) {
                this.keywords = keywords;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getCreat() {
                return creat;
            }

            public void setCreat(String creat) {
                this.creat = creat;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public int getCredit() {
                return credit;
            }

            public void setCredit(int credit) {
                this.credit = credit;
            }

            public int getCoins() {
                return coins;
            }

            public void setCoins(int coins) {
                this.coins = coins;
            }

            public int getMoney() {
                return money;
            }

            public void setMoney(int money) {
                this.money = money;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getView() {
                return view;
            }

            public void setView(int view) {
                this.view = view;
            }

            public String getViewAt() {
                return viewAt;
            }

            public void setViewAt(String viewAt) {
                this.viewAt = viewAt;
            }

            public int getFavCreate() {
                return favCreate;
            }

            public void setFavCreate(int favCreate) {
                this.favCreate = favCreate;
            }

            public int getFavCreateAt() {
                return favCreateAt;
            }

            public void setFavCreateAt(int favCreateAt) {
                this.favCreateAt = favCreateAt;
            }

            public String getFlag() {
                return flag;
            }

            public void setFlag(String flag) {
                this.flag = flag;
            }
        }

        public static class Content {
            private String index;

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }
        }
    }

    public static class Page {
        private int count;
        private int num;
        private int size;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
