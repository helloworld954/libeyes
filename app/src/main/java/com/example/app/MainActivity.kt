package com.example.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.lib.eyes.AdsPool
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.BuildConfig
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam
import com.libeye.admob.reload
import com.libeye.admob.templates.BannerView
import com.libeye.admob.templates.TemplateView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdsPool.loadAndShowImmediately(
            lp = AdMobLoadParam.AdmobOpenApp(BuildConfig.openApp_test, this),
            sp = AdMobShowParam.SPAdmobOpenApp(
                this, object: ShowCallback {
                    override fun onSuccess() {
                        initView()
                    }

                    override fun onFailed() {
                        initView()
                    }
                }
            ),
            this
        )
    }

    private fun initView() {
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
                    loadCallback = stubLoadCallback
                ),
            )
        }

        // Inter preload and show
        val load = {
            AdsPool.prepareAd(
                tag = "inter_main",
                loadParam = AdMobLoadParam.AdmobInterstitial(
                    context = this,
                    interId = BuildConfig.inter_test,
                    loadCallback = stubLoadCallback,
                )
            )
        }.also { it.invoke() }

        findViewById<Button>(R.id.btn_show_inter).setOnClickListener {
            AdsPool.show(
                tag = "inter_main",
                param = AdMobShowParam.SPAdmobInterstitial(
                    activity = this,
                    showLoading = true,
                    showCallback = object: ShowCallback {
                        override fun onSuccess() {

                        }

                        override fun onFailed() {
                        }

                        override fun onClosed(ad: AdsInterface<*>) {
                            //reload
//                        load.invoke()

                            //or
                            AdsPool.reload(this@MainActivity, ad)
                        }
                    }
                )
            )
        }
    }
}