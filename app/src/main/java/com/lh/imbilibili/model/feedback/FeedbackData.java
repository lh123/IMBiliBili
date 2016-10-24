package com.lh.imbilibili.model.feedback;

import java.util.List;

/**
 * Created by home on 2016/7/31.
 */
public class FeedbackData {

    private List<Feedback> hots;
    private Page page;
    private List<Feedback> replies;

    public List<Feedback> getHots() {
        return hots;
    }

    public void setHots(List<Feedback> hots) {
        this.hots = hots;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Feedback> getReplies() {
        return replies;
    }

    public void setReplies(List<Feedback> replies) {
        this.replies = replies;
    }

    public static class Page {
        private int acount;
        private int count;
        private int num;
        private int size;

        public int getAcount() {
            return acount;
        }

        public void setAcount(int acount) {
            this.acount = acount;
        }

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
