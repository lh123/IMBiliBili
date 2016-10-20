package com.lh.imbilibili.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/8.
 */

public class UserDetailInfo {
    private int mid;
    private String uname;
    private String face;
    @SerializedName("s_facce")
    private String sFace;
    private int rank;
    private int scores;
    private int coins;
    private int sex;
    private int maxstow;
    private String sign;
    private String jointime;
    private int spacesta;
    private int[] attentions;
    private int identification;
    @SerializedName("level_info")
    private LevelInfo levelInfo;
    @SerializedName("security_level")
    private int securityLevel;
    private String birthday;
    @SerializedName("mobile_verified")
    private int mobileVerified;
    private String telephone;

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

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getsFace() {
        return sFace;
    }

    public void setsFace(String sFace) {
        this.sFace = sFace;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getMaxstow() {
        return maxstow;
    }

    public void setMaxstow(int maxstow) {
        this.maxstow = maxstow;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getJointime() {
        return jointime;
    }

    public void setJointime(String jointime) {
        this.jointime = jointime;
    }

    public int getSpacesta() {
        return spacesta;
    }

    public void setSpacesta(int spacesta) {
        this.spacesta = spacesta;
    }

    public int[] getAttentions() {
        return attentions;
    }

    public void setAttentions(int[] attentions) {
        this.attentions = attentions;
    }

    public int getIdentification() {
        return identification;
    }

    public void setIdentification(int identification) {
        this.identification = identification;
    }

    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    public void setLevelInfo(LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(int mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
