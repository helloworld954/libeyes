package com.lib.eyes.formaldialogs

data class RatingDialogConfig internal constructor(
    var isForceShow: Boolean = false
)

fun ratingConfig(block: RatingDialogConfig.() -> Unit): RatingDialogConfig =
    RatingDialogConfig().apply(block)