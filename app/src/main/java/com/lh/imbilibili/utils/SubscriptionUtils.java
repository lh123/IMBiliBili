package com.lh.imbilibili.utils;

import rx.Subscription;

/**
 * Created by liuhui on 2016/10/20.
 */

public class SubscriptionUtils {
    public static void unsubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }
}
