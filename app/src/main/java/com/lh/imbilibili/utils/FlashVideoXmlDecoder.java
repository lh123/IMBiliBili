package com.lh.imbilibili.utils;

import android.util.Xml;

import com.lh.imbilibili.model.video.VideoPlayData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by liuhui on 2016/11/19.
 */

public class FlashVideoXmlDecoder {

    public static Observable<VideoPlayData> decodeFromXml(final InputStream inputStream) {
        return Observable.create(new Observable.OnSubscribe<VideoPlayData>() {
            @Override
            public void call(Subscriber<? super VideoPlayData> subscriber) {
                VideoPlayData data = new VideoPlayData();
                List<VideoPlayData.Durl> durls = new ArrayList<>();
                data.setDurl(durls);
                VideoPlayData.Durl durl = null;
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput(inputStream, "utf-8");
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (parser.getName().equals("accept_quality")) {
                                    parser.next();
                                    String str = parser.getText();
                                    String[] split = str.split(",");
                                    int[] qualities = new int[split.length];
                                    for (int i = 0; i < split.length; i++) {
                                        qualities[i] = Integer.parseInt(split[i]);
                                    }
                                    data.setAcceptQuality(qualities);
                                } else if (parser.getName().equals("durl")) {
                                    parser.next();
                                    durl = new VideoPlayData.Durl();
                                } else if (parser.getName().equals("order")) {
                                    parser.next();
                                    durl.setOrder(Integer.parseInt(parser.getText()));
                                } else if (parser.getName().equals("length")) {
                                    parser.next();
                                    durl.setLength(Integer.parseInt(parser.getText()));
                                } else if (parser.getName().equals("size")) {
                                    parser.next();
                                    durl.setSize(Integer.parseInt(parser.getText()));
                                } else if (parser.getName().equals("url")) {
                                    parser.next();
                                    durl.setUrl(parser.getText());
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("durl")) {
                                    durls.add(durl);
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });

    }
}
