package com.example.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("chill: FCMService", "onNewToken: $token")
    }
}