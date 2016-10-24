package com.lh.imbilibili.model.home;

import com.lh.imbilibili.model.bangumi.Bangumi;

import java.util.List;

/**
 * Created by liuhui on 2016/7/8.
 */
public class IndexPage {
    private Ad ad;
    private Previous previous;
    private List<Bangumi> serializing;

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public Previous getPrevious() {
        return previous;
    }

    public void setPrevious(Previous previous) {
        this.previous = previous;
    }

    public List<Bangumi> getSerializing() {
        return serializing;
    }

    public void setSerializing(List<Bangumi> serializing) {
        this.serializing = serializing;
    }

    public static class Ad {
        private List<Object> body;
        private List<Banner> head;

        public List<Object> getBody() {
            return body;
        }

        public void setBody(List<Object> body) {
            this.body = body;
        }

        public List<Banner> getHead() {
            return head;
        }

        public void setHead(List<Banner> head) {
            this.head = head;
        }
    }

    public static class Previous {
        private List<Bangumi> list;
        private int season;
        private int year;

        public List<Bangumi> getList() {
            return list;
        }

        public void setList(List<Bangumi> list) {
            this.list = list;
        }

        public int getSeason() {
            return season;
        }

        public void setSeason(int season) {
            this.season = season;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
