package com.example.fcm

import com.google.firebase.messaging.FirebaseMessagingService

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}