package com.lh.imbilibili.utils;

import com.lh.imbilibili.data.helper.CommonHelper;
import com.lh.imbilibili.model.BilibiliDataResponse;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/8.
 */

public class HistoryUtils {
    public static void addHistory(String id) {
        CommonHelper.getInstance()
                .getHistoryService()
                .addHistory(id)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BilibiliDataResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BilibiliDataResponse bilibiliDataResponse) {

                    }
                });
    }

    public static void addHisotry(String cid, String eposideId) {

    }
}
