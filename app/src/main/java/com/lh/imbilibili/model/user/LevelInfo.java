package com.lh.imbilibili.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/10/15.
 */

public class LevelInfo {
    @SerializedName("current_level")
    private int currentLevel;
    @SerializedName("current_min")
    private int currentMin;
    @SerializedName("current_exp")
    private int currentExp;
    @SerializedName("next_exp")
    private String nextExp;

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

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public String getNextExp() {
        return nextExp;
    }

    public void setNextExp(String nextExp) {
        this.nextExp = nextExp;
    }
}