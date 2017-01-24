package com.lh.imbilibili.utils;

import com.lh.imbilibili.data.helper.CommonHelper;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuhui on 2016/10/8.
 */

public class HistoryUtils {
    public static void addHistory(String id) {
        CommonHelper.getInstance()
                .getHistoryService()
                .addHistory(id)
                .ignoreElements()
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public static void addHisotry(String cid, String eposideId) {

    }
}
