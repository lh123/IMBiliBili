package com.lh.imbilibili.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by home on 2016/7/31.
 */
public class StringUtils {
    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    public static String formateNumber(int value) {
        if (value < 0) {
            return "NaN";
        } else {
            if (value >= 10000) {
                return format("%.1f万", value / 10000f);
            } else if (value > 0) {
                return format("%d", value);
            } else {
                return "-";
            }
        }
    }

    public static String formateNumber(String num) {
        int value = str2int(num);
        if (value < 0) {
            return "NaN";
        } else {
            if (value >= 10000) {
                return format("%.1f万", value / 10000f);
            } else {
                return format("%d", value);
            }
        }
    }

    public static String str2Weekday(String str) {
        String[] weekdays = new String[]{"日", "一", "二", "三", "四", "五", "六"};
        int index = str2int(str);
        if (index >= 0 && index <= 6) {
            return weekdays[index];
        } else {
            return "NaN";
        }
    }

    public static int str2int(String str) {
        int value = 0;
        try {
            value = Integer.valueOf(str);
        } catch (NumberFormatException e) {
            value = -1;
        }
        return value;
    }

    /**
     * @param time time
     * @return 秒前 分钟前 小时前 yyyy-MM-dd HH:mm
     */
    public static String formateDateRelative(long time) {
        long cTime = System.currentTimeMillis() / 1000;
        long interval = Math.abs(cTime - time);
        int diff = 0;
        if (interval > 24 * 60 * 60) { //大于24小时
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(time * 1000));
        } else if (interval >= 60 * 60) { //大于1小时
            diff = (int) (interval / (60 * 60));
            return format("%d小时前", diff);
        } else if (interval >= 60) {//大于1分钟
            diff = (int) (interval / 60);
            return format("%d分钟前", diff);
        } else {
            diff = (int) interval;
            return format("%d秒前", diff);
        }
    }

    /**
     * @param time time
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formateDateActu(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(time));
    }

    /**
     * @param time time
     * @return yyyy年MM月dd日
     */
    public static String formateDateCN(long time) {
        return new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(new Date(time));
    }
}
