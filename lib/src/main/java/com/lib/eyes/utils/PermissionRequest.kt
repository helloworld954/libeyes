package com.lib.eyes.utils

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class PermissionRequestState {
    DECLINED,
    ACCEPTED,
    SHOULD_SHOW_EXPLAIN,
    CANNOT_REQUEST
}

object PermissionRequest {
    fun request(
        activity: FragmentActivity,
        permission: String,
        callback: (PermissionRequestState) -> Unit
    ) {
        val launcher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                callback(
                    if (isGranted) PermissionRequestState.ACCEPTED else {
                        IndependenceDialog.showAlertDialog(
                            context = activity,
                            positiveMessage = "Oke",
                            positiveAction = { dialog, _ ->
                                dialog.dismiss()
                            }
                        )
                        PermissionRequestState.DECLINED
                    }
                )
            }

        when {
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                callback(PermissionRequestState.ACCEPTED)
            }

            shouldShowRequestPermissionRationale(
                activity,
                permission
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                IndependenceDialog.showAlertDialog(
                    context = activity,
                    positiveMessage = "Oke",
                    positiveAction = { dialog, _ ->
                        dialog.dismiss()
                    }
                )
                callback(PermissionRequestState.SHOULD_SHOW_EXPLAIN)
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                launcher.launch(permission)
            }
        }
    }
}
