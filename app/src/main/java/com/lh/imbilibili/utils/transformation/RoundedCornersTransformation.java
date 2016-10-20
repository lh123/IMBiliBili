package com.lh.imbilibili.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by home on 2016/7/31.
 */
public class RoundedCornersTransformation extends BitmapTransformation {
    private float radius;

    public RoundedCornersTransformation(Context context, float radius) {
        super(context);
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        RectF rectF = new RectF(0, 0, outWidth, outHeight);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect srcRect = new Rect(0, 0, toTransform.getWidth(), toTransform.getHeight());
        Rect dstRect = new Rect(0, 0, outWidth, outHeight);
        canvas.drawBitmap(toTransform, srcRect, dstRect, paint);
        return bitmap;
    }

    @Override
    public String getId() {
        return "RoundedCornersTransformation(radius:" + radius + ")";
    }
}
