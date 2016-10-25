package com.lh.imbilibili.utils;

import android.content.Context;

import com.lh.imbilibili.model.video.VideoPlayData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by liuhui on 2016/9/26.
 */

public class VideoUtils {
    public static Observable<String> concatVideo(final Context context, final List<VideoPlayData.Durl> durls) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String videoPlayPath = null;
                if (durls.size() > 1) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ffconcat version 1.0\n");
                    for (int i = 0; i < durls.size(); i++) {
                        stringBuilder.append("file ");
                        stringBuilder.append(durls.get(i).getUrl());
                        stringBuilder.append("\n");
                        stringBuilder.append("duration ");
                        stringBuilder.append(durls.get(i).getLength() / 1000);
                        stringBuilder.append("\n");
                    }
                    File file = new File(StorageUtils.getAppCachePath(), "video.cancat");
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(file);
                        fileWriter.write(stringBuilder.toString());
                        fileWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fileWriter != null) {
                                fileWriter.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    videoPlayPath = file.getAbsolutePath();
                } else if (durls.size() == 1) {
                    videoPlayPath = durls.get(0).getUrl();
                }
                subscriber.onNext(videoPlayPath);
                subscriber.onCompleted();
            }
        });
    }
}
