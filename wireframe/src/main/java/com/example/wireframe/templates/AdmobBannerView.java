package com.example.wireframe.templates;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AdmobBannerView<V, S> extends FrameLayout {
    protected BannerSize size;

    public AdmobBannerView(@NonNull Context context) {
        super(context);
    }

    public AdmobBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public AdmobBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public AdmobBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    protected abstract void initView(Context context, AttributeSet attributeSet);

    public abstract void setAdView(V adView);

    public abstract S getAdSize();

    protected Activity requireActivity() {
        if(getContext() instanceof Activity) return (Activity) getContext();

        throw new RuntimeException("Context is not activity in BannerView");
    }
}
