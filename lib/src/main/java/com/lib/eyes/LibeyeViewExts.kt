package com.lib.eyes

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.TextView

fun EditText.text() = this.text.toString()
fun TextView.text() = this.text.toString()

fun Activity.openStore(alterPackageName: String? = null) {
    val packageName = alterPackageName ?: this.packageName

    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
        )
    }
}