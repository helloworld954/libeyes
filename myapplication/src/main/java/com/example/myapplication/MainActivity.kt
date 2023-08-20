package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lib.eyes.formaldialogs.DialogFactory
import com.lib.eyes.formaldialogs.RatingDialogConfig
import com.lib.eyes.formaldialogs.ratingConfig
import com.lib.eyes.utils.DataStore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataStore.setup(this.applicationContext)
        DialogFactory.createRateDialog(
            this,
            config = ratingConfig {
                isForceShow = true
            }
        )
    }
}