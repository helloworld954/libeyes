package com.libeye.admob.templates;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.libeye.admob.R;

public class BannerView extends FrameLayout {
    private BannerSize size;

    private ShimmerFrameLayout mAdsFrame;

    public BannerView(@NonNull Context context) {
        super(context);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attributeSet) {
        TypedArray attributes =
                context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.BannerView, 0, 0);

        try {
            size = BannerSize.values()[attributes.getResourceId(R.styleable.BannerView_gnt_banner_size, BannerSize.ANCHOR.ordinal())];
        } finally {
            attributes.recycle();
        }

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mAdsFrame = (ShimmerFrameLayout) inflater.inflate(R.layout.gnt_small_shimmer_view, null, false);
        this.addView(mAdsFrame);
    }

    public void setAdView(AdView adView) {
        mAdsFrame.hideShimmer();
        this.removeAllViews();
        mAdsFrame = null;

        this.addView(adView);
    }

    public AdSize getAdSize() {
        Activity activity = requireActivity();
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        if (size == BannerSize.INLINE) {
            return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(getContext(), adWidth);
        } else {
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
        }
    }

    protected Activity requireActivity() {
        if(getContext() instanceof Activity) return (Activity) getContext();

        throw new RuntimeException("Context is not activity in BannerView");
    }
}
