package com.lib.eyes.utils

import androidx.core.os.bundleOf
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseLog {
    private val firebaseAnalytics = Firebase.analytics

    fun logEvent(content: String) {
        logEvent(null, "data", content)
    }

    fun logEvent(name: String? = null, contentKey: String, content: String) {
        firebaseAnalytics.logEvent(name ?: "unknown_name", bundleOf(
            contentKey to content
        ))
    }
}
