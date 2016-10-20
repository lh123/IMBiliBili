package com.lh.imbilibili.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by home on 2016/7/30.
 */
public class BlurTransformation extends BitmapTransformation {

    private RenderScript rs;
    private float radius;

    public BlurTransformation(Context context, float radius) {
        super(context);
        this.radius = radius;
        rs = RenderScript.create(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.ARGB_8888, true);
        Allocation input = Allocation.createFromBitmap(
                rs,
                blurredBitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);
        script.setRadius(radius);
        script.forEach(output);
        output.copyTo(blurredBitmap);
        rs.destroy();
        return blurredBitmap;
    }

    @Override
    public String getId() {
        return "BlurTransformation(radius:" + radius + ")";
    }
}
