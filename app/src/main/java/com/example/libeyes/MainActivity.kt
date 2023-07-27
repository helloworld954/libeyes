package com.example.libeyes

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.lib.eyes.AdsPool
import com.lib.eyes.BuildConfig
import com.lib.eyes.Param
import com.lib.eyes.ShowParam
import com.lib.eyes.utils.PermissionRequest
import com.lib.eyes.utils.PermissionRequestState
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback

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

        MobileAds.initialize(this)
        AdsPool.prepareAd<Param.AdmobNative.IAdmobNative>(
            BuildConfig.native_main, Param.AdmobNative(
                this,
                findViewById(R.id.my_template),
                BuildConfig.native_main,
            )
        ).show(ShowParam.SPAdmobNative(object : ShowCallback {
            override fun onSuccess() {
                Log.d("vanh: MainActivity", "onSuccess: ")
            }

            override fun onFailed() {
                Log.d("vanh: MainActivity", "onFailed: ")
            }
        }))

        AdsPool.show(BuildConfig.banner_main, ShowParam.SPAdmobBanner(
            findViewById(R.id.banner_template),
            BuildConfig.banner_main,
            showCallback = object : ShowCallback {
                override fun onSuccess() {
                    Log.d("vanh: MainActivity", "onSuccess: ")
                }

                override fun onFailed() {
                    Log.d("vanh: MainActivity", "onFailed: ")
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
                    super.loadSuccess()
                    Log.d("vanh: MainActivity", "loadSuccess: ")
                }

                override fun loadFailed() {
                    super.loadFailed()
                    Log.d("vanh: MainActivity", "loadFailed: ")
                }
            }
        ))

        AdsPool.prepareAd<Param.AdmobInterstitial.IAdmobInterstitial>(
            adId = BuildConfig.inter_main,
            param = Param.AdmobInterstitial(
                this, BuildConfig.inter_main, object : LoadCallback {
                    override fun loadSuccess() {
                        Log.d("vanh: MainActivity", "loadSuccess: ")
                    }

                    override fun loadFailed() {
                        Log.d("vanh: MainActivity", "loadFailed: ")
                    }
                }
            ),
            separateTime = 3
        )

        findViewById<Button>(R.id.btn_inter).setOnClickListener {
            AdsPool.showSeparate(BuildConfig.inter_main, ShowParam.SPAdmobInterstitial(this@MainActivity, object : ShowCallback {
                override fun onSuccess() {
                    Log.d("vanh: MainActivity", "onSuccess: ")
                }

                override fun onFailed() {
                    Log.d("vanh: MainActivity", "onFailed: ")
                }

                override fun onClosed() {
                    Log.d("vanh: MainActivity", "onClosed: ")
                }
            }))
        }
    }
}