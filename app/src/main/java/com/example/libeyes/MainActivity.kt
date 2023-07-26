package com.example.libeyes

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.lib.eyes.BuildConfig
import com.lib.eyes.ggads.InterstitialAds
import com.lib.eyes.ggads.NativeAds
import com.lib.eyes.wireframe.InterstitialInterface
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)
        NativeAds(this).config(this, findViewById(R.id.my_template), BuildConfig.native_main).let { ads ->
            findViewById<TextView>(R.id.tv_main).setOnClickListener {
                Log.i("vanh: MainActivity", "onCreate: click")
                ads.show(callback = object: ShowCallback {
                    override fun onSuccess() {
                        Log.d("vanh: MainActivity", "onSuccess: ")
                    }

                    override fun onFailed() {
                        Log.d("vanh: MainActivity", "onFailed: ")
                    }

                })
            }   
        }
        
        findViewById<Button>(R.id.btn_inter).setOnClickListener { 
            lateinit var ads: InterstitialInterface
            
            ads = InterstitialAds().load(this, BuildConfig.inter_main, object: LoadCallback {
                override fun loadSuccess() {
                    Log.d("vanh: MainActivity", "loadSuccess: ")
                    ads.show(this@MainActivity, object : ShowCallback {
                        override fun onSuccess() {
                            Log.d("vanh: MainActivity", "onSuccess: ")
                        }

                        override fun onFailed() {
                            Log.d("vanh: MainActivity", "onFailed: ")
                        }

                        override fun onClosed() {
                            Log.d("vanh: MainActivity", "onClosed: ")
                        }
                    })
                }

                override fun loadFailed() {
                    Log.d("vanh: MainActivity", "loadFailed: ")
                }
            })
        }
    }
}