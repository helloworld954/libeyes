package com.libeye.libeyes

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.libeyes.R
import com.libeye.wireframe.ShowParam
import com.libeye.wireframe.wireframe.ShowCallback
import com.lib.eyes.AdsPool
import com.lib.eyes.BuildConfig
import com.lib.eyes.utils.PermissionRequest
import com.lib.eyes.utils.PermissionRequestState

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

        findViewById<Button>(R.id.btn_inter_immediately).setOnClickListener {
            AdsPool.loadAndShowImmediately(
                BuildConfig.inter_main,
                ShowParam.SPAdmobInterstitial(this@MainActivity, object :
                    ShowCallback {
                    override fun onSuccess() {
                        Log.d("vanh: MainActivity", "onSuccess: ")
                    }

                    override fun onFailed() {
                        Log.d("vanh: MainActivity", "onFailed: ")
                    }

                    override fun onClosed() {
//                    AdsPool.prepareAd<Param.AdmobInterstitial.IAdmobInterstitial>(
//                        adId = BuildConfig.inter_main,
//                        param = param,
//                        separateTime = 3
//                    )
                    }
                }),
                fragmentActivity = this,
                timeout = 5000
            )
        }

//        AdsPool.prepareAd<Param.AdmobNative.IAdmobNative>(
//            BuildConfig.native_main, Param.AdmobNative(
//                this,
//                findViewById(R.id.my_template),
//                BuildConfig.native_main,
//            )
//        ).show(ShowParam.SPAdmobNative(object : ShowCallback {
//            override fun onSuccess() {
//                Log.d("vanh: MainActivity", "onSuccess: AdmobNative")
//            }
//
//            override fun onFailed() {
//                Log.d("vanh: MainActivity", "onFailed: AdmobNative")
//            }
//        }))
//
//        AdsPool.loadAndShowImmediately(
//            BuildConfig.banner_main,
//            ShowParam.SPAdmobBanner(
//                findViewById(R.id.banner_template),
//                BuildConfig.banner_main,
//                showCallback = object : ShowCallback {
//                    override fun onSuccess() {
//                        Log.d("vanh: MainActivity", "onSuccess: ")
//                    }
//
//                    override fun onFailed() {
//                        Log.d("vanh: MainActivity", "onFailed: SPAdmobBanner")
//                    }
//
//                    override fun onClicked() {
//                        super.onClicked()
//                        Log.d("vanh: MainActivity", "onClicked: ")
//                    }
//
//                    override fun onClosed() {
//                        super.onClosed()
//                        Log.d("vanh: MainActivity", "onClosed: ")
//                    }
//                },
//                loadCallback = object : LoadCallback {
//                    override fun loadSuccess() {
//                        Log.d("vanh: MainActivity", "loadSuccess: ")
//                    }
//
//                    override fun loadFailed() {
//                        Log.d("vanh: MainActivity", "loadFailed: SPAdmobBanner")
//                    }
//                }
//            ))
    }
}