package com.lh.imbilibili.model.search;

import java.util.List;

/**
 * Created by liuhui on 2016/10/6.
 */

public class BangumiSearchResult {
    private int pages;
    private List<Season> items;

    public BangumiSearchResult() {
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int page) {
        this.pages = page;
    }

    public List<Season> getItems() {
        return items;
    }

    public void setItems(List<Season> items) {
        this.items = items;
    }
}
