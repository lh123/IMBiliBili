package com.lh.imbilibili.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by home on 2016/7/31.
 */
public class CircleTransformation extends BitmapTransformation {
    public CircleTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int orginWidth = toTransform.getWidth();
        int orginHeight = toTransform.getHeight();
        Bitmap bitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        canvas.drawCircle(outWidth / 2.0f, outHeight / 2.0f, outWidth / 2.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect srcRect = new Rect(0, 0, orginWidth, orginHeight);
        Rect dstRect = new Rect(0, 0, outWidth, outHeight);
        canvas.drawBitmap(toTransform, srcRect, dstRect, paint);
        return bitmap;
    }

    @Override
    public String getId() {
        return "CircleTransformation";
    }
}
