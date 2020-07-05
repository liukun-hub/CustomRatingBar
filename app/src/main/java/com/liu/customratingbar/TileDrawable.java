package com.liu.customratingbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Author: liukun on 2020/7/4.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public class TileDrawable extends BaseDrawable {

    private Drawable mDrawable;
    private int mTileCount = -1;

    public TileDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        invalidateSelf();
    }

    public void setTileCount(int tileCount) {
        mTileCount = tileCount;
    }

    public int getTileCount() {
        return mTileCount;
    }

    @NonNull
    @Override
    public Drawable mutate() {
        mDrawable = mDrawable.mutate();
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas, int width, int height) {
        mDrawable.setAlpha(mAlpha);
        ColorFilter colorFilter = getColorFilterForDrawable();
        if (colorFilter != null) {
            mDrawable.setColorFilter(colorFilter);
        }
        int tileHeight = mDrawable.getIntrinsicHeight();
        float scale = (float) height / tileHeight;
        canvas.scale(scale, scale);

        float scaleWidth = width / scale;
        if (mTileCount < 0) {
            int tileWidth = mDrawable.getIntrinsicWidth();
            for (int x = 0; x < scaleWidth; x += tileWidth) {
                mDrawable.setBounds(x, 0, x + tileWidth, tileHeight);
                mDrawable.draw(canvas);
            }
        } else {
            float tileWidth = scaleWidth / mTileCount;
            for (int i = 0; i < mTileCount; i++) {
                int drawableWidth = mDrawable.getIntrinsicWidth();
                float tileCenter = tileWidth * (i + 0.5f);
                float drawableWidthHalf = (float) drawableWidth / 2;
                mDrawable.setBounds(Math.round(tileCenter - drawableWidthHalf), 0,
                        Math.round(tileCenter + drawableWidthHalf), tileHeight);
                mDrawable.draw(canvas);
            }
        }
    }
}
