package com.lh.imbilibili.model.attention;

import com.lh.imbilibili.model.BiliBiliResponse;

/**
 * Created by liuhui on 2016/10/10.
 */

public class FollowBangumiResponse<T> extends BiliBiliResponse<T> {
    private String count;
    private String pages;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
}
