package com.lib.eyes.formaldialogs

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.R
import com.lib.eyes.STAR_FOR_STORE
import com.lib.eyes.databinding.DialogLoadingBinding
import com.lib.eyes.databinding.DialogRatingBinding
import com.lib.eyes.openStore
import com.lib.eyes.text
import com.lib.eyes.utils.DataStore
import com.lib.eyes.utils.FirebaseLog
import com.lib.eyes.utils.IndependenceDialog

object DialogFactory {
    fun createLoadingDialog(
        fragmentActivity: FragmentActivity,
        @ColorInt color: Int? = null
    ): DialogFragment {
        return IndependenceDialog.show(
            fragmentActivity, DialogLoadingBinding::inflate, false
        ) {
            color?.let { c ->
                it.root.indeterminateTintMode = PorterDuff.Mode.SRC_ATOP
                it.root.indeterminateTintList = ColorStateList.valueOf(c)
            }
        }
    }

    fun createRateDialog(
        fragmentActivity: FragmentActivity,
        @StyleRes theme: Int = R.style.RateDialog,
        config: RatingDialogConfig = RatingDialogConfig()
    ) : DialogFragment? {
        return DataStore.get(
            FIRST_RATE, false
        ).let { isRated ->
            if(isRated.not() || config.isForceShow) {
                IndependenceDialog.show(
                    fragmentActivity, DialogRatingBinding::inflate, true,
                    theme = theme
                ) { dialogBinding ->
                    var currentStar = 0f

                    dialogBinding.btnLater.setOnClickListener { dismiss() }
                    dialogBinding.btnClose.setOnClickListener { dismiss() }
                    dialogBinding.btnSubmit.setOnClickListener {
                        if (dialogBinding.imgThumb.isVisible) {
                            dismiss()
                        } else {
                            if (currentStar >= STAR_FOR_STORE) {
                                fragmentActivity.openStore()
                            } else {
                                dialogBinding.tvMainLabel.setText(R.string.thank_for_submit)
                                dialogBinding.btnSubmit.setText(R.string.done)

                                dialogBinding.ratingBar.visibility = View.GONE
                                dialogBinding.etComment.visibility = View.INVISIBLE
                                dialogBinding.btnLater.visibility = View.GONE
                                dialogBinding.imgThumb.visibility = View.VISIBLE

                                DataStore.put(FIRST_RATE, true)
                            }

                            FirebaseLog.logEvent(
                                "rating_${currentStar}_comment",
                                dialogBinding.etComment.text()
                            )
                        }
                    }

                    dialogBinding.ratingBar.setOnRatingChangeListener { _, rating, _ ->
                        currentStar = rating

                        dialogBinding.btnSubmit.apply {
                            isEnabled = rating > 0
                            alpha = if(isEnabled) 1f else 0.2f
                        }
                    }
                }
            } else {
                null
            }
        }
    }

    private const val FIRST_RATE = "first_rated"
}