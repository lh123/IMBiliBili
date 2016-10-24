package com.lh.imbilibili.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by liuhui on 2016/10/24.
 * RxBus
 */

public class RxBus {

    private static RxBus mRxBus;
    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    private RxBus() {
    }

    public static RxBus getInstance() {
        if (mRxBus == null) {
            synchronized (RxBus.class) {
                if (mRxBus == null) {
                    mRxBus = new RxBus();
                }
            }
        }
        return mRxBus;
    }


    public void send(Object o) {
        mBus.onNext(o);
    }

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }
}
