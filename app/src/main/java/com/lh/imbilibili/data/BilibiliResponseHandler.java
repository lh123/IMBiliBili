package com.lh.imbilibili.data;

import com.lh.imbilibili.model.BiliBiliResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by home on 2017/1/24.
 */

public class BilibiliResponseHandler {
    public static <T extends BiliBiliResponse<R>,R> BilibiliResultFunction<T,R> handlerResult(){
        return new BilibiliResultFunction<>();
    }

    private static class BilibiliResultFunction<T extends BiliBiliResponse<R>, R> implements Function<T, ObservableSource<R>> {

        @Override
        public ObservableSource<R> apply(T t) throws Exception {
            if (t.isSuccess()) {
                return Observable.just(t.getResult());
            } else {
                return Observable.error(new ApiException(t.getCode(), t.getMessage()));
            }
        }
    }
}
