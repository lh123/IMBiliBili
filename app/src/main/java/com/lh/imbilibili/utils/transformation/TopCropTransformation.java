package com.lh.imbilibili.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by home on 2016/7/30.
 */
public class TopCropTransformation extends BitmapTransformation {

    public TopCropTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        int orginWidth = toTransform.getWidth();
        Bitmap bitmap = pool.get(outWidth, outHeight, toTransform.getConfig());
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(outWidth, outHeight, toTransform.getConfig());
        }
        Canvas canvas = new Canvas(bitmap);
        Paint bitmapPaint = new Paint();
        float scaleX = (float) outWidth / orginWidth;
        matrix.setScale(scaleX, scaleX);
        canvas.drawBitmap(toTransform, matrix, bitmapPaint);
        return bitmap;
    }

    @Override
    public String getId() {
        return "TopCropTransformation";
    }
}
