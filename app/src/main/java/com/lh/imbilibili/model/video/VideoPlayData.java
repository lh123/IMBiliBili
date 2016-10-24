package com.lh.imbilibili.model.video;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by home on 2016/8/4.
 */
public class VideoPlayData {
    private String from;
    private String result;
    private String format;
    private int timelength;
    @SerializedName("accept_format")
    private String acceptFormat;
    @SerializedName("accept_quality")
    private int[] acceptQuality;
    @SerializedName("seek_param")
    private String seekParam;
    @SerializedName("seek_type")
    private String seekType;
    private List<Durl> durl;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getTimelength() {
        return timelength;
    }

    public void setTimelength(int timelength) {
        this.timelength = timelength;
    }

    public String getAcceptFormat() {
        return acceptFormat;
    }

    public void setAcceptFormat(String acceptFormat) {
        this.acceptFormat = acceptFormat;
    }

    public int[] getAcceptQuality() {
        return acceptQuality;
    }

    public void setAcceptQuality(int[] acceptQuality) {
        this.acceptQuality = acceptQuality;
    }

    public String getSeekParam() {
        return seekParam;
    }

    public void setSeekParam(String seekParam) {
        this.seekParam = seekParam;
    }

    public String getSeekType() {
        return seekType;
    }

    public void setSeekType(String seekType) {
        this.seekType = seekType;
    }

    public List<Durl> getDurl() {
        return durl;
    }

    public void setDurl(List<Durl> durl) {
        this.durl = durl;
    }

    public static class Durl {
        private int order;
        private long length;
        private long size;
        private String url;
        @SerializedName("backup_url")
        private String[] backupUrl;

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String[] getBackupUrl() {
            return backupUrl;
        }

        public void setBackupUrl(String[] backupUrl) {
            this.backupUrl = backupUrl;
        }
    }
}
