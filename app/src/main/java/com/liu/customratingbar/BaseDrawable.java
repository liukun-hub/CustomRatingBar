package com.liu.customratingbar;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author: liukun on 2020/7/4.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public abstract class BaseDrawable extends Drawable {

    int mAlpha = 0xFF;
    private ColorFilter mColorFilter;
    private ColorStateList mTintList;

    private PorterDuffColorFilter mTintFilter;
    private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
    private CustomConstantState mConstantState = new CustomConstantState();


    @Override

    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.height() == 0 || bounds.width() == 0) {
            return;
        }
        int saveCount = canvas.save();
        canvas.translate(bounds.left, bounds.top);
        onDraw(canvas, bounds.width(), bounds.height());
        // now the canvas is back in the same state it was before the initial call to save().
        canvas.restoreToCount(saveCount);
    }

    protected abstract void onDraw(Canvas canvas, int width, int height);

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha != alpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }


    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mColorFilter = colorFilter;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setTint(int tintColor) {
        setTintList(ColorStateList.valueOf(tintColor));
    }

    @Override
    public void setTintList(@Nullable ColorStateList tintList) {
        mTintList = tintList;
        if (updateTintFilter()) {
            invalidateSelf();
        }
    }

    @Override
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        mTintMode = tintMode;
        if (updateTintFilter()) {
            invalidateSelf();
        }
    }

    @Override
    public boolean isStateful() {
        return super.isStateful();
    }

    @Nullable
    @Override
    public ConstantState getConstantState() {
        return mConstantState;
    }

    private boolean updateTintFilter() {
        if (mTintList == null || mTintMode == null) {
            boolean hadTintFilter = mTintFilter != null;
            mTintFilter = null;
            return hadTintFilter;
        }
        int tintColor = mTintList.getColorForState(getState(), Color.TRANSPARENT);
        mTintFilter = new PorterDuffColorFilter(tintColor, mTintMode);
        return true;
    }

    public ColorFilter getColorFilterForDrawable() {
        return mColorFilter != null ? mColorFilter : mTintFilter;
    }

    private class CustomConstantState extends ConstantState {

        @NonNull
        @Override
        public Drawable newDrawable() {
            return BaseDrawable.this;
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }
    }
}
