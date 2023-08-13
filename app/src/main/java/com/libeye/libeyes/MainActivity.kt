package com.libeye.libeyes

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.libeyes.R
import com.lib.eyes.AdsPool
import com.lib.eyes.BuildConfig
import com.lib.eyes.utils.PermissionRequest
import com.lib.eyes.utils.PermissionRequestState
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionRequest.request(this, Manifest.permission.READ_CONTACTS) {
            when (it) {
                PermissionRequestState.ACCEPTED -> {
                    setupView()
                }

                PermissionRequestState.DECLINED -> TODO()
                PermissionRequestState.SHOULD_SHOW_EXPLAIN -> TODO()
                PermissionRequestState.CANNOT_REQUEST -> TODO()
            }
        }

    }

    private fun setupView() {
        setContentView(R.layout.activity_main)

        val param = AdMobLoadParam.AdmobInterstitial(
            this, BuildConfig.inter_main,
            interLoadCallback = object: LoadCallback {
                override fun loadFailed() {
                    super.loadFailed()
                }

                override fun loadSuccess() {
                    super.loadSuccess()
                }
            },
            separateTime = 2
        )
        AdsPool.prepareAd<AdMobShowParam.SPAdmobInterstitial>(
            "taggg",
            param
        )

        findViewById<TextView>(R.id.tv_main).setOnClickListener {
            AdsPool.show("taggg", AdMobShowParam.SPAdmobInterstitial(
                this, showCallback = object: ShowCallback {
                    override fun onSuccess() {
                    }

                    override fun onFailed() {
                    }

                    override fun onClosed() {
                        AdsPool.prepareAd<AdMobShowParam.SPAdmobInterstitial>(
                            "taggg",
                            param
                        )
                    }
                }
            ))
        }

        findViewById<Button>(R.id.btn_inter_immediately).setOnClickListener {
            AdsPool.loadAndShowImmediately(
                lp = AdMobLoadParam.AdmobInterstitial(
                    this, BuildConfig.inter_main,
                    interLoadCallback = object: LoadCallback {

                    },
                    timeout = 5000
                ),
                sp = AdMobShowParam.SPAdmobInterstitial(
                    this,
                    showCallback = object: ShowCallback {
                        override fun onSuccess() {
                            Log.d("vanh: MainActivity", "onSuccess: ")
                        }

                        override fun onFailed() {
                            Log.d("vanh: MainActivity", "onFailed: ")
                        }

                        override fun onClosed() {
                            Log.d("vanh: MainActivity", "onClosed: ")
                        }
                    }
                ),
                fragmentActivity = this
            )
        }

        AdsPool.loadAndShowImmediately(
            lp = AdMobLoadParam.AdmobNative(
                this,
                findViewById(R.id.my_template),
                nativeId = BuildConfig.native_main,
                lifecycleOwner = this
            ),
            sp = AdMobShowParam.SPAdmobNative(
                showCallback = object: ShowCallback {
                    override fun onSuccess() {
                        Log.d("vanh: MainActivity", "onSuccess: native")
                    }

                    override fun onFailed() {
                        Log.d("vanh: MainActivity", "onFailed: native")
                    }
                }
            )
        )

        AdsPool.loadAndShowImmediately(
            lp = AdMobLoadParam.AdmobBanner,
            sp = AdMobShowParam.SPAdmobBanner(
                findViewById(R.id.banner_template),
                BuildConfig.banner_main,
                showCallback = object : ShowCallback {
                    override fun onSuccess() {
                        Log.d("vanh: MainActivity", "onSuccess: ")
                    }

                    override fun onFailed() {
                        Log.d("vanh: MainActivity", "onFailed: SPAdmobBanner")
                    }

                    override fun onClicked() {
                        super.onClicked()
                        Log.d("vanh: MainActivity", "onClicked: ")
                    }

                    override fun onClosed() {
                        super.onClosed()
                        Log.d("vanh: MainActivity", "onClosed: ")
                    }
                },
                loadCallback = object : LoadCallback {
                    override fun loadSuccess() {
                        Log.d("vanh: MainActivity", "loadSuccess: ")
                    }

                    override fun loadFailed() {
                        Log.d("vanh: MainActivity", "loadFailed: SPAdmobBanner")
                    }
                }
            ))
    }
}