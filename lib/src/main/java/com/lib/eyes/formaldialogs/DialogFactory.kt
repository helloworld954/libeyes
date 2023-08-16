package com.lib.eyes.formaldialogs

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.Const
import com.lib.eyes.databinding.DialogLoadingBinding
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
}