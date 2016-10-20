package com.lh.imbilibili.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by home on 2016/8/2.
 */
public class Feedback {
    private int action;
    private Content content;
    private int count;
    private long ctime;
    private int floor;
    private int like;
    private Member member;
    private int mid;
    private int oid;
    private int parent;
    @SerializedName("parent_str")
    private String parentStr;
    private int rcount;
    private List<Feedback> replies;
    private int root;
    @SerializedName("root_str")
    private String rootStr;
    private int rpid;
    @SerializedName("rpid_str")
    private String rpidStr;
    private int state;
    private int type;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getParentStr() {
        return parentStr;
    }

    public void setParentStr(String parentStr) {
        this.parentStr = parentStr;
    }

    public int getRcount() {
        return rcount;
    }

    public void setRcount(int rcount) {
        this.rcount = rcount;
    }

    public List<Feedback> getReplies() {
        return replies;
    }

    public void setReplies(List<Feedback> replies) {
        this.replies = replies;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public String getRootStr() {
        return rootStr;
    }

    public void setRootStr(String rootStr) {
        this.rootStr = rootStr;
    }

    public int getRpid() {
        return rpid;
    }

    public void setRpid(int rpid) {
        this.rpid = rpid;
    }

    public String getRpidStr() {
        return rpidStr;
    }

    public void setRpidStr(String rpidStr) {
        this.rpidStr = rpidStr;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Content {
        private String device;
        private Object members;
        private String message;
        private int plat;

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public Object getMembers() {
            return members;
        }

        public void setMembers(Object members) {
            this.members = members;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getPlat() {
            return plat;
        }

        public void setPlat(int plat) {
            this.plat = plat;
        }
    }

    public static class Member {
        private String DisplayRank;
        private String avatar;
        @SerializedName("level_info")
        private LevelInfo levelInfo;
        private String mid;
        private NamePlate nameplate;
        private Pendant pendant;
        private int rank;
        private String sex;
        private String sign;
        private String uname;

        public String getDisplayRank() {
            return DisplayRank;
        }

        public void setDisplayRank(String displayRank) {
            DisplayRank = displayRank;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public LevelInfo getLevelInfo() {
            return levelInfo;
        }

        public void setLevelInfo(LevelInfo levelInfo) {
            this.levelInfo = levelInfo;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public NamePlate getNameplate() {
            return nameplate;
        }

        public void setNameplate(NamePlate nameplate) {
            this.nameplate = nameplate;
        }

        public Pendant getPendant() {
            return pendant;
        }

        public void setPendant(Pendant pendant) {
            this.pendant = pendant;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
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

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public static class LevelInfo {
            @SerializedName("current_exp")
            private int currentExp;
            @SerializedName("current_level")
            private int currentLevel;
            @SerializedName("current_min")
            private int currentMin;
            @SerializedName("next_exp")
            private String nextExp;

            public int getCurrentExp() {
                return currentExp;
            }

            public void setCurrentExp(int currentExp) {
                this.currentExp = currentExp;
            }

            public int getCurrentLevel() {
                return currentLevel;
            }

            public void setCurrentLevel(int currentLevel) {
                this.currentLevel = currentLevel;
            }

            public int getCurrentMin() {
                return currentMin;
            }

            public void setCurrentMin(int currentMin) {
                this.currentMin = currentMin;
            }

            public String getNextExp() {
                return nextExp;
            }

            public void setNextExp(String nextExp) {
                this.nextExp = nextExp;
            }
        }

        public static class NamePlate {
            private String condition;
            private String image;
            @SerializedName("image_small")
            private String imageSmall;
            private String level;
            private String name;
            private int nid;

            public String getCondition() {
                return condition;
            }

            public void setCondition(String condition) {
                this.condition = condition;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getImageSmall() {
                return imageSmall;
            }

            public void setImageSmall(String imageSmall) {
                this.imageSmall = imageSmall;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getNid() {
                return nid;
            }

            public void setNid(int nid) {
                this.nid = nid;
            }
        }

        public static class Pendant {
            private int expire;
            private String image;
            private String name;
            private int pid;

            public int getExpire() {
                return expire;
            }

            public void setExpire(int expire) {
                this.expire = expire;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }
        }
    }
}
