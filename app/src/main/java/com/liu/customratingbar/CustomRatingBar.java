package com.liu.customratingbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.widget.AppCompatRatingBar;

/**
 * Author: liukun on 2020/7/3.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public class CustomRatingBar extends AppCompatRatingBar {

    private ColorStateList mStartColor;
    private ColorStateList mSubStartColor;
    private ColorStateList mBgColor;

    /**
     * customize start drawable
     */
    private int mStartDrawable;

    /**
     * customize background drawable
     */
    private int mBgDrawable;

    /**
     * right to left
     */
    private boolean mRight2left;

    /**
     * keep the origin color of start drawable
     */
    private boolean mKeepOriginColor;

    /**
     * the scale factor of ratingBar that can change the spacing of start
     */
    private float mScaleFactor;

    /**
     * the spacing of starts
     */
    private float mStartSpacing;

    private StarDrawable mDrawable;
    //    private StarDrawable mDrawable;
    private float mTempRating;

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener onRatingBarChangeListener) {
        mOnRatingBarChangeListener = onRatingBarChangeListener;
        if (mRight2left) {
            mOnRatingBarChangeListener.onRatingChanged(this, getNumStars() - getRating());
        } else {
            mOnRatingBarChangeListener.onRatingChanged(this, getRating());
        }
    }

    private OnRatingBarChangeListener mOnRatingBarChangeListener;


    public CustomRatingBar(Context context) {
        this(context, null);
    }

    public CustomRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRatingBar, defStyleAttr, 0);
        mRight2left = typedArray.getBoolean(R.styleable.CustomRatingBar_right2Left, false);

        if (typedArray.hasValue(R.styleable.CustomRatingBar_starColor)) {
            if (mRight2left) {
                mBgColor = typedArray.getColorStateList(R.styleable.CustomRatingBar_starColor);
            } else {
                mStartColor = typedArray.getColorStateList(R.styleable.CustomRatingBar_starColor);
            }
        }
        if (typedArray.hasValue(R.styleable.CustomRatingBar_bgColor)) {
            if (mRight2left) {
                mStartColor = typedArray.getColorStateList(R.styleable.CustomRatingBar_bgColor);
            } else {
                mBgColor = typedArray.getColorStateList(R.styleable.CustomRatingBar_bgColor);
            }
        }

        if (typedArray.hasValue(R.styleable.CustomRatingBar_subStarColor)) {
            if (!mRight2left) {
                mSubStartColor = typedArray.getColorStateList(R.styleable.CustomRatingBar_subStarColor);
            }
        }

        mKeepOriginColor = typedArray.getBoolean(R.styleable.CustomRatingBar_keepOriginColor, false);
        mScaleFactor = typedArray.getFloat(R.styleable.CustomRatingBar_scaleFactor, 1);
        mStartSpacing = typedArray.getFloat(R.styleable.CustomRatingBar_starSpacing, 0);

        mStartDrawable = typedArray.getResourceId(R.styleable.CustomRatingBar_starDrawable, R.drawable.ic_rating_star_solid);
        if (typedArray.hasValue(R.styleable.CustomRatingBar_bgDrawable)) {
            mBgDrawable = typedArray.getResourceId(R.styleable.CustomRatingBar_bgDrawable, R.drawable.ic_rating_star_solid);
        } else {
            mBgDrawable = mStartDrawable;
        }
        typedArray.recycle();
        mDrawable = new StarDrawable(context, mStartDrawable, mBgDrawable, mKeepOriginColor);
//        mDrawable = new StarDrawable(context, mStartDrawable, mBgDrawable, mKeepOriginColor);
//        mDrawable.setStartCount(getNumStars());
        mDrawable.setStarCount(getNumStars());
        setProgressDrawable(mDrawable);
        if (mRight2left) {
            setRating(getNumStars() - getRating());
        }

    }

    @Override
    public void setNumStars(int numStars) {
        super.setNumStars(numStars);
        if (mDrawable != null) {
//            mDrawable.setStartCount(numStars);
            mDrawable.setStarCount(numStars);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = Math.round((height * mDrawable.getTileRatio() * getNumStars() * mScaleFactor) + (getNumStars() - 1) * mStartSpacing);
        setMeasuredDimension(View.resolveSizeAndState(width, widthMeasureSpec, 0), height);
    }


    @Override
    public void setProgressDrawable(Drawable d) {
        super.setProgressDrawable(d);
        applyProgressTint();
    }

    private void applyProgressTint() {
        if (getProgressDrawable() == null) {
            return;
        }

        applyPrimaryProgressTint();
        applySecondaryProgressTint();
        applyProgressBackgroundTint();

    }

    private void applyPrimaryProgressTint() {
        if (mStartColor != null) {
            Drawable target = getTintTargetFromProgressDrawable(android.R.id.progress, true);
            applyTintForDrawable(target, mStartColor);
        }

    }

    private void applyTintForDrawable(Drawable target, ColorStateList tintList) {
        if (target == null) {
            return;
        }
        if (tintList != null) {
            if (target instanceof BaseDrawable) {
                target.setTintList(tintList);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    target.setTintList(tintList);
                }
            }
            // The drawable (or one of its children) may not have been
            // stateful before applying the tint, so let's try again.
            if (target.isStateful()) {
                target.setState(getDrawableState());
            }
        }
    }

    private void applySecondaryProgressTint() {
        if (mSubStartColor != null) {
            Drawable target = getTintTargetFromProgressDrawable(android.R.id.secondaryProgress, false);
            applyTintForDrawable(target, mSubStartColor);
        }
    }

    private void applyProgressBackgroundTint() {
        if (mBgColor != null) {
            Drawable target = getTintTargetFromProgressDrawable(android.R.id.background, false);
            applyTintForDrawable(target, mBgColor);
        }
    }

    /**
     * Returns the drawable to which a tint or tint mode should be applied.
     *
     * @param layerId        id of the layer to modify
     * @param shouldFallback whether the base drawable should be returned
     *                       if the id does not exist
     * @return the drawable to modify
     */
    private Drawable getTintTargetFromProgressDrawable(int layerId, boolean shouldFallback) {
        Drawable layer = null;

        Drawable d = getProgressDrawable();
        if (d != null) {
            d = d.mutate();

            if (d instanceof LayerDrawable) {
                layer = ((LayerDrawable) d).findDrawableByLayerId(layerId);
            }

            if (shouldFallback && layer == null) {
                layer = d;
            }
        }
        return layer;
    }

    @Override
    public synchronized void setSecondaryProgress(int secondaryProgress) {
        super.setSecondaryProgress(secondaryProgress);
        float rating = getRating();
        if (mOnRatingBarChangeListener != null && rating != mTempRating) {
            if (mRight2left) {
                mOnRatingBarChangeListener.onRatingChanged(this, getNumStars() - getRating());
            } else {
                mOnRatingBarChangeListener.onRatingChanged(this, getRating());
            }
            mTempRating = rating;
        }
    }

    /**
     * set the scale factor of the start
     *
     * @param scaleFactor scale Factor
     */
    public void setScaleFactor(float scaleFactor) {
        mScaleFactor = scaleFactor;
        requestLayout();
    }

    /**
     * set the space of starts
     *
     * @param startSpacing start Spacing of horizon
     */
    public void setStartSpacing(float startSpacing) {
        mStartSpacing = startSpacing;
        requestLayout();
    }

    /**
     * A callback that notifies clients when the rating has been changed. This
     * includes changes that were initiated by the user through a touch gesture
     * or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnRatingBarChangeListener {

        /**
         * Notification that the rating has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically. This will not be called continuously
         * while the user is dragging, only when the user finalizes a rating by
         * lifting the touch.
         *
         * @param ratingBar The RatingBar whose rating has changed.
         * @param rating    The current rating. This will be in the range
         *                  0..numStars.
         */
        void onRatingChanged(RatingBar ratingBar, float rating);

    }


}
