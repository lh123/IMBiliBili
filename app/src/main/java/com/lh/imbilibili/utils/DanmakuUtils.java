package com.lh.imbilibili.utils;

import android.content.Context;

import com.lh.imbilibili.IMBilibiliApplication;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.RetrofitHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuhui on 2016/9/26.
 */

public class DanmakuUtils {
    private Call call;

    public void downLoadDanmaku(final Context context, String cid, final OnDanmakuDownloadListener listener) {
        OkHttpClient okHttpClient = RetrofitHelper.getInstance().getOkhttpClient();
        final Request request = new Request.Builder().url(Constant.COMMENT_URL + "/" + cid + ".xml")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Encoding", "deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8").build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IMBilibiliApplication.getApplication().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFail();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(CompressUtils.decompressXML(response.body().bytes()));
                    final File file = StorageUtils.getAppCache(context, "danmaku.xml");
                    FileOutputStream fileOutputStream = null;
                    fileOutputStream = new FileOutputStream(file, false);
                    byte[] bytes = new byte[2048];
                    int length;
                    while ((length = inputStream.read(bytes)) > 0) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    IMBilibiliApplication.getApplication().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(file);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    IMBilibiliApplication.getApplication().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail();
                        }
                    });
                }
            }
        });
    }

    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    public interface OnDanmakuDownloadListener {
        void onSuccess(File file);
        void onFail();
    }
}
