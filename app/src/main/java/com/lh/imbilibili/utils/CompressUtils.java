package com.lh.imbilibili.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;

/**
 * Created by liuhui on 2016/9/22.
 */

public class CompressUtils {

    public static byte[] decompressXML(byte[] data){

        byte[] dest = new byte[data.length + 2];
        System.arraycopy(data, 0, dest, 2, data.length);
        dest[0] = 0x78;
        dest[1] = 0x01;
        data = dest;
        Inflater decompresser = new Inflater();
        decompresser.setInput(data);

        byte[] bufferArray = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            int i = 1;
            while (i != 0) {
                i = decompresser.inflate(bufferArray);
                baos.write(bufferArray, 0, i);
            }
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return data;
    }
}
