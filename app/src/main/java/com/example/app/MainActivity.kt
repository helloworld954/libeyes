package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.lib.eyes.AdsPool
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.BuildConfig
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam
import com.libeye.admob.templates.BannerView
import com.libeye.admob.templates.TemplateView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stubCallback = object: ShowCallback {
            override fun onSuccess() {
            }

            override fun onFailed() {
            }

            override fun onClicked() {
                super.onClicked()
            }

            override fun onClosed(ad: AdsInterface<*>) {
                super.onClosed(ad)
            }
        }

        val stubLoadCallback = object: LoadCallback {

        }

        // Native Ads
        val templateView: TemplateView = findViewById(R.id.template_view)
        AdsPool.loadAndShowImmediately(
            sp = AdMobShowParam.SPAdmobNative(
                showCallback = stubCallback
            ),
            lp = AdMobLoadParam.AdmobNative(
                context = this,
                templateView = templateView,
                nativeId = BuildConfig.native_test,
                lifecycleOwner = ViewTreeLifecycleOwner.get(templateView)
            ),
            fragmentActivityAndColor = null
        )

        // Banner Ads
        val bannerView: BannerView = findViewById(R.id.banner_view)
        AdsPool.loadAndShowImmediately(
            sp = AdMobShowParam.SPAdmobBanner(
                adView = bannerView,
                adId = BuildConfig.banner_test,
                showCallback = stubCallback,
                loadCallback = stubLoadCallback
            ),
            lp = AdMobLoadParam.AdmobBanner,
            fragmentActivityAndColor = null
        )

        // Inter load and show
        findViewById<Button>(R.id.btn_load_and_show).setOnClickListener {
            AdsPool.loadAndShowImmediately(
                sp = AdMobShowParam.SPAdmobInterstitial(
                    activity = this,
                    showCallback = stubCallback
                ),
                lp = AdMobLoadParam.AdmobInterstitial(
                    context = this,
                    interId = BuildConfig.inter_test,
                    interLoadCallback = stubLoadCallback
                ),
                fragmentActivityAndColor = this to null
            )
        }

        // Inter preload and show
        val load = {
            AdsPool.prepareAd<AdMobShowParam.SPAdmobInterstitial>(
                tag = "inter_main",
                loadParam = AdMobLoadParam.AdmobInterstitial(
                    context = this,
                    interId = BuildConfig.inter_test,
                    interLoadCallback = stubLoadCallback
                )
            )
        }.also { it.invoke() }

        findViewById<Button>(R.id.btn_show_inter).setOnClickListener {
            AdsPool.show(
                tag = "inter_main",
                param = AdMobShowParam.SPAdmobInterstitial(
                    activity = this,
                    showCallback = object: ShowCallback {
                        override fun onSuccess() {

                        }

                        override fun onFailed() {
                        }

                        override fun onClosed(ad: AdsInterface<*>) {
                            //reload
//                        load.invoke()

                            //or
                            (ad as AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial).reload(this@MainActivity)
                        }
                    }
                )
            )
        }
    }
}