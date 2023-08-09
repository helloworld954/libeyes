package com.example.wireframe.templates;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class AdmobTemplateView<AV, NA> extends FrameLayout {
    protected int templateType;
    protected NativeTemplateStyle styles;

    protected NA nativeAd;
    protected AV nativeAdView;

    protected TextView primaryView;
    protected TextView secondaryView;
    protected RatingBar ratingBar;
    protected TextView tertiaryView;
    protected ImageView iconView;
    protected Button callToActionView;
    protected ConstraintLayout background;

    protected static final String MEDIUM_TEMPLATE = "medium_template";
    protected static final String SMALL_TEMPLATE = "small_template";

    public AdmobTemplateView(Context context) {
        super(context);
    }

    public AdmobTemplateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public AdmobTemplateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public AdmobTemplateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    public void setStyles(NativeTemplateStyle styles) {
        this.styles = styles;
        this.applyStyles();
    }

    public abstract AV getNativeAdView();

    protected void applyStyles() {

        Drawable mainBackground = styles.getMainBackgroundColor();
        if (mainBackground != null) {
            background.setBackground(mainBackground);
            if (primaryView != null) {
                primaryView.setBackground(mainBackground);
            }
            if (secondaryView != null) {
                secondaryView.setBackground(mainBackground);
            }
            if (tertiaryView != null) {
                tertiaryView.setBackground(mainBackground);
            }
        }

        Typeface primary = styles.getPrimaryTextTypeface();
        if (primary != null && primaryView != null) {
            primaryView.setTypeface(primary);
        }

        Typeface secondary = styles.getSecondaryTextTypeface();
        if (secondary != null && secondaryView != null) {
            secondaryView.setTypeface(secondary);
        }

        Typeface tertiary = styles.getTertiaryTextTypeface();
        if (tertiary != null && tertiaryView != null) {
            tertiaryView.setTypeface(tertiary);
        }

        Typeface ctaTypeface = styles.getCallToActionTextTypeface();
        if (ctaTypeface != null && callToActionView != null) {
            callToActionView.setTypeface(ctaTypeface);
        }

        if (styles.getPrimaryTextTypefaceColor() != null && primaryView != null) {
            primaryView.setTextColor(styles.getPrimaryTextTypefaceColor());
        }

        if (styles.getSecondaryTextTypefaceColor() != null && secondaryView != null) {
            secondaryView.setTextColor(styles.getSecondaryTextTypefaceColor());
        }

        if (styles.getTertiaryTextTypefaceColor() != null && tertiaryView != null) {
            tertiaryView.setTextColor(styles.getTertiaryTextTypefaceColor());
        }

        if (styles.getCallToActionTypefaceColor() != null && callToActionView != null) {
            callToActionView.setTextColor(styles.getCallToActionTypefaceColor());
        }

        float ctaTextSize = styles.getCallToActionTextSize();
        if (ctaTextSize > 0 && callToActionView != null) {
            callToActionView.setTextSize(ctaTextSize);
        }

        float primaryTextSize = styles.getPrimaryTextSize();
        if (primaryTextSize > 0 && primaryView != null) {
            primaryView.setTextSize(primaryTextSize);
        }

        float secondaryTextSize = styles.getSecondaryTextSize();
        if (secondaryTextSize > 0 && secondaryView != null) {
            secondaryView.setTextSize(secondaryTextSize);
        }

        float tertiaryTextSize = styles.getTertiaryTextSize();
        if (tertiaryTextSize > 0 && tertiaryView != null) {
            tertiaryView.setTextSize(tertiaryTextSize);
        }

        Drawable ctaBackground = styles.getCallToActionBackgroundColor();
        if (ctaBackground != null && callToActionView != null) {
            callToActionView.setBackground(ctaBackground);
        }

        Drawable primaryBackground = styles.getPrimaryTextBackgroundColor();
        if (primaryBackground != null && primaryView != null) {
            primaryView.setBackground(primaryBackground);
        }

        Drawable secondaryBackground = styles.getSecondaryTextBackgroundColor();
        if (secondaryBackground != null && secondaryView != null) {
            secondaryView.setBackground(secondaryBackground);
        }

        Drawable tertiaryBackground = styles.getTertiaryTextBackgroundColor();
        if (tertiaryBackground != null && tertiaryView != null) {
            tertiaryView.setBackground(tertiaryBackground);
        }

        invalidate();
        requestLayout();
    }

    protected abstract boolean adHasOnlyStore(NA nativeAd);

    public abstract void setNativeAd(NA nativeAd);

    public abstract void reinflate();

    public abstract void destroyView();

    /**
     * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
     * method does not destroy the template view.
     * https://developers.google.com/admob/android/native-unified#destroy_ad
     */
    public abstract void destroyNativeAd();

    protected abstract void initView(Context context, AttributeSet attributeSet);
}


