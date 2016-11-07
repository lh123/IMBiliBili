package com.lh.imbilibili.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuhui on 2016/10/2.
 */


public class VideoDetail implements Parcelable {

    private String aid;
    private String tid;
    private String tname;
    private int copyright;
    private String pic;
    private String title;
    private long pubdate;
    private long ctime;
    private String desc;
    private int state;
    private int attribute;
    private int duration;
    private String[] tags;
    private Rights rights;
    private Owner owner;
    private Stat stat;
    private List<Page> pages;
    @SerializedName("req_user")
    private ReqUser reqUser;
    private List<VideoDetail> relates;

    public VideoDetail() {
    }

    protected VideoDetail(Parcel in) {
        aid = in.readString();
        tid = in.readString();
        tname = in.readString();
        copyright = in.readInt();
        pic = in.readString();
        title = in.readString();
        pubdate = in.readLong();
        ctime = in.readLong();
        desc = in.readString();
        state = in.readInt();
        attribute = in.readInt();
        duration = in.readInt();
        tags = in.createStringArray();
        rights = in.readParcelable(Rights.class.getClassLoader());
        owner = in.readParcelable(Owner.class.getClassLoader());
        stat = in.readParcelable(Stat.class.getClassLoader());
        pages = in.createTypedArrayList(Page.CREATOR);
        reqUser = in.readParcelable(ReqUser.class.getClassLoader());
        relates = in.createTypedArrayList(VideoDetail.CREATOR);
    }

    public static final Creator<VideoDetail> CREATOR = new Creator<VideoDetail>() {
        @Override
        public VideoDetail createFromParcel(Parcel in) {
            return new VideoDetail(in);
        }

        @Override
        public VideoDetail[] newArray(int size) {
            return new VideoDetail[size];
        }
    };

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public int getCopyright() {
        return copyright;
    }

    public void setCopyright(int copyright) {
        this.copyright = copyright;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getPubdate() {
        return pubdate;
    }

    public void setPubdate(long pubdate) {
        this.pubdate = pubdate;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Rights getRights() {
        return rights;
    }

    public void setRights(Rights rights) {
        this.rights = rights;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public ReqUser getReqUser() {
        return reqUser;
    }

    public void setReqUser(ReqUser reqUser) {
        this.reqUser = reqUser;
    }

    public List<VideoDetail> getRelates() {
        return relates;
    }

    public void setRelates(List<VideoDetail> relates) {
        this.relates = relates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aid);
        dest.writeString(tid);
        dest.writeString(tname);
        dest.writeInt(copyright);
        dest.writeString(pic);
        dest.writeString(title);
        dest.writeLong(pubdate);
        dest.writeLong(ctime);
        dest.writeString(desc);
        dest.writeInt(state);
        dest.writeInt(attribute);
        dest.writeInt(duration);
        dest.writeStringArray(tags);
        dest.writeParcelable(rights, flags);
        dest.writeParcelable(owner, flags);
        dest.writeParcelable(stat, flags);
        dest.writeTypedList(pages);
        dest.writeParcelable(reqUser, flags);
        dest.writeTypedList(relates);
    }

    public static class Rights implements Parcelable {
        private int bp;
        private int elec;
        private int download;
        private int move;
        private int pay;

        public Rights() {
        }

        protected Rights(Parcel in) {
            bp = in.readInt();
            elec = in.readInt();
            download = in.readInt();
            move = in.readInt();
            pay = in.readInt();
        }

        public static final Creator<Rights> CREATOR = new Creator<Rights>() {
            @Override
            public Rights createFromParcel(Parcel in) {
                return new Rights(in);
            }

            @Override
            public Rights[] newArray(int size) {
                return new Rights[size];
            }
        };

        public int getBp() {
            return bp;
        }

        public void setBp(int bp) {
            this.bp = bp;
        }

        public int getElec() {
            return elec;
        }

        public void setElec(int elec) {
            this.elec = elec;
        }

        public int getDownload() {
            return download;
        }

        public void setDownload(int download) {
            this.download = download;
        }

        public int getMove() {
            return move;
        }

        public void setMove(int move) {
            this.move = move;
        }

        public int getPay() {
            return pay;
        }

        public void setPay(int pay) {
            this.pay = pay;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(bp);
            dest.writeInt(elec);
            dest.writeInt(download);
            dest.writeInt(move);
            dest.writeInt(pay);
        }
    }

    public static class Owner implements Parcelable {
        private int mid;
        private String name;
        private String face;

        public Owner() {
        }

        protected Owner(Parcel in) {
            mid = in.readInt();
            name = in.readString();
            face = in.readString();
        }

        public static final Creator<Owner> CREATOR = new Creator<Owner>() {
            @Override
            public Owner createFromParcel(Parcel in) {
                return new Owner(in);
            }

            @Override
            public Owner[] newArray(int size) {
                return new Owner[size];
            }
        };

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

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mid);
            dest.writeString(name);
            dest.writeString(face);
        }
    }

    public static class Stat implements Parcelable {
        private int view;
        private int danmaku;
        private int reply;
        private int favorite;
        private int coin;
        private int share;
        @SerializedName("now_rank")
        private int nowRank;
        @SerializedName("his_rank")
        private int hisRank;

        public Stat() {
        }

        protected Stat(Parcel in) {
            view = in.readInt();
            danmaku = in.readInt();
            reply = in.readInt();
            favorite = in.readInt();
            coin = in.readInt();
            share = in.readInt();
            nowRank = in.readInt();
            hisRank = in.readInt();
        }

        public static final Creator<Stat> CREATOR = new Creator<Stat>() {
            @Override
            public Stat createFromParcel(Parcel in) {
                return new Stat(in);
            }

            @Override
            public Stat[] newArray(int size) {
                return new Stat[size];
            }
        };

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getDanmaku() {
            return danmaku;
        }

        public void setDanmaku(int danmaku) {
            this.danmaku = danmaku;
        }

        public int getReply() {
            return reply;
        }

        public void setReply(int reply) {
            this.reply = reply;
        }

        public int getFavorite() {
            return favorite;
        }

        public void setFavorite(int favorite) {
            this.favorite = favorite;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public int getShare() {
            return share;
        }

        public void setShare(int share) {
            this.share = share;
        }

        public int getNowRank() {
            return nowRank;
        }

        public void setNowRank(int nowRank) {
            this.nowRank = nowRank;
        }

        public int getHisRank() {
            return hisRank;
        }

        public void setHisRank(int hisRank) {
            this.hisRank = hisRank;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(view);
            dest.writeInt(danmaku);
            dest.writeInt(reply);
            dest.writeInt(favorite);
            dest.writeInt(coin);
            dest.writeInt(share);
            dest.writeInt(nowRank);
            dest.writeInt(hisRank);
        }
    }

    public static class Page implements Parcelable {
        private int cid;
        private int page;
        private String from;
        private String link;
        @SerializedName("has_alias")
        private int hasAlias;
        private String weblink;
        private String part;
        @SerializedName("rich_vid")
        private String richVid;
        private String vid;

        protected Page(Parcel in) {
            cid = in.readInt();
            page = in.readInt();
            from = in.readString();
            link = in.readString();
            hasAlias = in.readInt();
            weblink = in.readString();
            part = in.readString();
            richVid = in.readString();
            vid = in.readString();
        }

        public static final Creator<Page> CREATOR = new Creator<Page>() {
            @Override
            public Page createFromParcel(Parcel in) {
                return new Page(in);
            }

            @Override
            public Page[] newArray(int size) {
                return new Page[size];
            }
        };

        public Page() {
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getHasAlias() {
            return hasAlias;
        }

        public void setHasAlias(int hasAlias) {
            this.hasAlias = hasAlias;
        }

        public String getWeblink() {
            return weblink;
        }

        public void setWeblink(String weblink) {
            this.weblink = weblink;
        }

        public String getPart() {
            return part;
        }

        public void setPart(String part) {
            this.part = part;
        }

        public String getRichVid() {
            return richVid;
        }

        public void setRichVid(String richVid) {
            this.richVid = richVid;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(cid);
            dest.writeInt(page);
            dest.writeString(from);
            dest.writeString(link);
            dest.writeInt(hasAlias);
            dest.writeString(weblink);
            dest.writeString(part);
            dest.writeString(richVid);
            dest.writeString(vid);
        }
    }

    public static class ReqUser implements Parcelable {
        private int attention;
        private int favorite;

        public ReqUser() {
        }

        protected ReqUser(Parcel in) {
            attention = in.readInt();
            favorite = in.readInt();
        }

        public static final Creator<ReqUser> CREATOR = new Creator<ReqUser>() {
            @Override
            public ReqUser createFromParcel(Parcel in) {
                return new ReqUser(in);
            }

            @Override
            public ReqUser[] newArray(int size) {
                return new ReqUser[size];
            }
        };

        public int getAttention() {
            return attention;
        }

        public void setAttention(int attention) {
            this.attention = attention;
        }

        public int getFavorite() {
            return favorite;
        }

        public void setFavorite(int favorite) {
            this.favorite = favorite;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(attention);
            dest.writeInt(favorite);
        }
    }
}
