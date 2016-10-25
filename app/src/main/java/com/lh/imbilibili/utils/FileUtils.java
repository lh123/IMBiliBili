package com.lh.imbilibili.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by liuhui on 2016/7/5.
 */
public class FileUtils {
    public static void closeStream(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void clearFile(File file) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, false);
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(writer);
        }
    }

    public static void writeToFile(File file, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fileWriter);
        }
    }

    public static String readFromFile(File file) {
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuilder.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bufferedReader, inputStreamReader, inputStream);
        }
        return stringBuilder.toString();
    }
}
