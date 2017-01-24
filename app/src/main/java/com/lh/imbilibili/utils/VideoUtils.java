package com.lh.imbilibili.utils;

import com.lh.imbilibili.model.video.PlusVideoPlayerData;
import com.lh.imbilibili.model.video.VideoPlayData;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by liuhui on 2016/9/26.
 */

public class VideoUtils {
    public static Observable<String> concatVideo(final List<VideoPlayData.Durl> durls) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
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
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(stringBuilder.toString());
                    fileWriter.flush();
                    fileWriter.close();
                    videoPlayPath = file.getAbsolutePath();
                } else if (durls.size() == 1) {
                    videoPlayPath = durls.get(0).getUrl();
                }
                e.onNext(videoPlayPath);
                e.onComplete();
            }
        });
    }

    public static Observable<String> concatPlusVideo(final List<PlusVideoPlayerData.Data.Part> parts) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String videoPlayPath = null;
                if (parts.size() > 1) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ffconcat version 1.0\n");
                    for (int i = 0; i < parts.size(); i++) {
                        stringBuilder.append("file ");
                        stringBuilder.append(parts.get(i).getUrl());
                        stringBuilder.append("\n");
                        stringBuilder.append("duration ");
                        int length = 0;
                        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
                        Matcher matcher = pattern.matcher(parts.get(i).getLength());
                        if (matcher.find()) {
                            length = Integer.parseInt(matcher.group(1)) * 60 + Integer.parseInt(matcher.group(2));
                        }
                        stringBuilder.append(length);
                        stringBuilder.append("\n");
                    }
                    File file = new File(StorageUtils.getAppCachePath(), "video.cancat");
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(stringBuilder.toString());
                    fileWriter.flush();
                    fileWriter.close();
                    videoPlayPath = file.getAbsolutePath();
                } else if (parts.size() == 1) {
                    videoPlayPath = parts.get(0).getUrl();
                }
                e.onNext(videoPlayPath);
                e.onComplete();
            }
        });
    }
}
