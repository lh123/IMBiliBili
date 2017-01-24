package com.lh.imbilibili.utils;

import com.lh.imbilibili.data.Constant;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuhui on 2016/9/26.
 */

public class DanmakuUtils {

    public static Observable<InputStream> downLoadDanmaku(final String cid) {

        return Observable.create(new ObservableOnSubscribe<InputStream>() {
            @Override
            public void subscribe(ObservableEmitter<InputStream> emitter) throws Exception {
                OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                Request request = new Request.Builder().url(Constant.COMMENT_URL + "/" + cid + ".xml")
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .addHeader("Accept-Encoding", "deflate")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.8").build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(CompressUtils.decompressXML(response.body().bytes()));
                    String path = StorageUtils.getAppCachePath();
                    File file = new File(path, "danmaku.xml");
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream fileOutputStream;
                    fileOutputStream = new FileOutputStream(file, false);
                    byte[] bytes = new byte[2048];
                    int length;
                    while ((length = inputStream.read(bytes)) > 0) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    emitter.onNext(new FileInputStream(file));
                    emitter.onComplete();
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
