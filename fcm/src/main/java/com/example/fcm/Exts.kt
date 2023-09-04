package com.example.fcm

import android.Manifest
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.utils.PermissionRequest
import com.lib.eyes.utils.PermissionRequestState

private fun PermissionRequest.askNotificationPermission(
    activity: FragmentActivity,
    callback: (PermissionRequestState) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.request(activity, Manifest.permission.POST_NOTIFICATIONS, callback)
    } else {
        callback.invoke(PermissionRequestState.ACCEPTED)
    }
}