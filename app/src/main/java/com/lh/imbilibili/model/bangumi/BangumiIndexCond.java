package com.lh.imbilibili.model.bangumi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuhui on 2016/9/3.
 */
public class BangumiIndexCond {

    private List<Category> category;
    private String[] years;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public String[] getYears() {
        return years;
    }

    public void setYears(String[] years) {
        this.years = years;
    }

    public static class Category{
        private String cover;
        @SerializedName("tag_id")
        private String tagId;
        @SerializedName("tag_name")
        private String tagName;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }
    }
}
