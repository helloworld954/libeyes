package com.lib.eyes.formaldialogs

import android.view.View
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.Const
import com.lib.eyes.R
import com.lib.eyes.configs.GlobalConfig
import com.lib.eyes.databinding.DialogLoadingBinding
import com.lib.eyes.databinding.DialogRatingBinding
import com.lib.eyes.openStore
import com.lib.eyes.text
import com.lib.eyes.utils.DataStore
import com.lib.eyes.utils.FirebaseLog
import com.lib.eyes.utils.IndependenceDialog
import java.util.concurrent.CancellationException
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

object DialogFactory {
    fun createLoadingDialog(
        fragmentActivity: FragmentActivity
    ): DialogFragment {
        return IndependenceDialog.show(
            fragmentActivity, DialogLoadingBinding::inflate, false
        )
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
                    dialogBinding.btnLater.setOnClickListener { dismiss() }
                    dialogBinding.btnClose.setOnClickListener { dismiss() }
                    dialogBinding.btnSubmit.setOnClickListener {
                        if (dialogBinding.imgThumb.isVisible) {
                            dismiss()
                        } else {
                            fragmentActivity.openStore()

                            dialogBinding.tvMainLabel.setText(R.string.thank_for_submit)
                            dialogBinding.btnSubmit.setText(R.string.done)

                            dialogBinding.ratingBar.visibility = View.GONE
                            dialogBinding.etComment.visibility = View.INVISIBLE
                            dialogBinding.btnLater.visibility = View.GONE
                            dialogBinding.imgThumb.visibility = View.VISIBLE

                            DataStore.put(FIRST_RATE, true)
                            FirebaseLog.logEvent(
                                "rating_comment",
                                dialogBinding.etComment.text()
                            )
                        }
                    }

                    dialogBinding.ratingBar.setOnRatingChangeListener { _, rating, _ ->
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