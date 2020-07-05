package com.liu.customratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.Gravity;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

/**
 * Author: liukun on 2020/7/4.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public class StartDrawable extends LayerDrawable {


    public StartDrawable(Context context, int startDrawable, int bgDrawable, boolean mKeepOriginColor) {
        super(new Drawable[]{
                createLayerDrawableWithTintAttrRes(bgDrawable, R.attr.colorControlHighlight, context, mKeepOriginColor),
                createClippedLayerDrawableWithAttrRes(startDrawable, R.attr.colorControlActivated, context, mKeepOriginColor),
                createClippedLayerDrawableWithTintColor(bgDrawable, Color.TRANSPARENT, context),

        });

        setId(0, android.R.id.background);
        setId(1, android.R.id.secondaryProgress);
        setId(2, android.R.id.progress);

    }

    private static Drawable createLayerDrawableWithTintAttrRes(int tileRes, int tintAttrRes, Context
            context, boolean mKeepOriginColor) {
        int tintColor = -1;
        if (!mKeepOriginColor) {
            tintColor = getColorFromAttrRes(tintAttrRes, context);
        }
        return createLayerDrawableWithTintColor(tileRes, tintColor, context);
    }


    private static Drawable createLayerDrawableWithTintColor(int tileRes, int tintColor, Context context) {
        TileDrawable tileDrawable = new TileDrawable(AppCompatResources.getDrawable(context, tileRes));
        tileDrawable.mutate();
        if (tintColor != -1) {
            tileDrawable.setTint(tintColor);
        }
        return tileDrawable;
    }

    private static Drawable createClippedLayerDrawableWithTintColor(int tileResId, int tintColor, Context context) {

        return new ClipDrawable(createLayerDrawableWithTintColor(tileResId, tintColor, context),
                Gravity.LEFT, ClipDrawable.HORIZONTAL);
    }

    private static Drawable createClippedLayerDrawableWithAttrRes(int tileResId, int tintAttrRes,
                                                                  Context context, boolean mKeepOriginColor) {

        return new ClipDrawable(createLayerDrawableWithTintAttrRes(tileResId, tintAttrRes, context
                , mKeepOriginColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
    }

    public float getTileRatio() {
        Drawable drawable = getTileDrawableByLayerId(android.R.id.progress).getDrawable();
        return (float) drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
    }

    public void setStartCount(int count) {
        getTileDrawableByLayerId(android.R.id.background).setTileCount(count);
        getTileDrawableByLayerId(android.R.id.secondaryProgress).setTileCount(count);
        getTileDrawableByLayerId(android.R.id.progress).setTileCount(count);
    }

    private TileDrawable getTileDrawableByLayerId(int id) {
        Drawable layerDrawable = findDrawableByLayerId(id);
        switch (id) {
            case android.R.id.background:
                return (TileDrawable) layerDrawable;
            case android.R.id.secondaryProgress:
            case android.R.id.progress:
                ClipDrawable clipDrawable = (ClipDrawable) layerDrawable;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return (TileDrawable) clipDrawable.getDrawable();
                } else {
                    try {
                        Field mStatedField = clipDrawable.getClass().getDeclaredField("mState");
                        mStatedField.setAccessible(true);
                        Object clipState = mStatedField.get(clipDrawable);
                        Field mDrawableField = clipDrawable.getClass().getDeclaredField("mDrawable");
                        mDrawableField.setAccessible(true);
                        return (TileDrawable) mDrawableField.get(clipDrawable);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            default:
                throw new RuntimeException();
        }
    }

    private static int getColorFromAttrRes(int attrRes, Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{attrRes});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;


    }
}
