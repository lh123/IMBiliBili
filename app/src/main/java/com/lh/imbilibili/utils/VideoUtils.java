package com.lh.imbilibili.utils;

import android.content.Context;

import com.lh.imbilibili.model.VideoPlayData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by liuhui on 2016/9/26.
 */

public class VideoUtils {
    public static String concatVideo(Context context, List<VideoPlayData.Durl> durls) {
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
            File file = StorageUtils.getAppCache(context, "video.cancat");
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
        return videoPlayPath;
    }
}
