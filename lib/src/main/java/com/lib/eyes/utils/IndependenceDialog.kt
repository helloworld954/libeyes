package com.lib.eyes.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lib.eyes.R
import java.util.UUID

typealias Inflater<T> = (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> T

object IndependenceDialog {
    fun <T : ViewBinding> show(
        activity: FragmentActivity,
        inflater: Inflater<T>,
        cancelable: Boolean = true,
        inflateAction: (DialogInterface.(T) -> Unit)? = null
    ): DialogFragment = CenterDialog(inflater, inflateAction, cancelable).apply {
        show(activity.supportFragmentManager, UUID.randomUUID().toString())
    }

    fun <T : ViewBinding> showBottomSheet(
        activity: FragmentActivity,
        inflater: Inflater<T>,
        cancelable: Boolean = true,
        inflateAction: (DialogInterface.(T) -> Unit)? = null
    ): DialogFragment = BottomSheet(inflater, inflateAction, cancelable).apply {
        show(activity.supportFragmentManager, UUID.randomUUID().toString())
    }

    fun showAlertDialog(
        context: Context,
        title: String? = null,
        message: String? = null,
        negativeMessage: String? = null,
        negativeAction: ((DialogInterface, Int) -> Unit)? = null,
        @DrawableRes negativeIcon: Int? = null,
        positiveMessage: String? = null,
        positiveAction: ((DialogInterface, Int) -> Unit)? = null,
        @DrawableRes positiveIcon: Int? = null,
        neutralMessage: String? = null,
        neutralAction: ((DialogInterface, Int) -> Unit)? = null,
        @DrawableRes neutralIcon: Int? = null,
        cancelable: Boolean = true
    ): AlertDialog.Builder {
        val alertDialog = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)

            setNegativeButton(negativeMessage, negativeAction)
            negativeIcon?.let {
                setNegativeButtonIcon(AppCompatResources.getDrawable(context, it))
            }

            setPositiveButton(positiveMessage, positiveAction)
            positiveIcon?.let {
                setPositiveButtonIcon(AppCompatResources.getDrawable(context, it))
            }

            setNeutralButton(neutralMessage, neutralAction)
            neutralIcon?.let {
                setNeutralButtonIcon(AppCompatResources.getDrawable(context, it))
            }

            setCancelable(cancelable)
        }

        return alertDialog.apply {
            show()
        }
    }
}



class CenterDialog<T : ViewBinding>(
    inflater: Inflater<T>,
    private val bindAction: (Dialog.(T) -> Unit)? = null,
    private val cancelable: Boolean = true
) : AppCompatDialogFragment() {
    private val binding: T by lazy {
        inflater.invoke(layoutInflater, null, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = FullWidthAppCompatDialog(requireContext(), R.style.TranslucentDialog)

        this.isCancelable = cancelable

        dialog.setContentView(binding.root)
        bindAction?.invoke(dialog, binding)

        return dialog
    }
}

class BottomSheet<T : ViewBinding>(
    inflater: Inflater<T>,
    private val bindAction: (Dialog.(T) -> Unit)? = null,
    private val cancelable: Boolean = true
) : BottomSheetDialogFragment() {
    private val binding: T by lazy {
        inflater.invoke(layoutInflater, null, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.TranslucentDialog)

        this.isCancelable = cancelable

        dialog.setContentView(binding.root)
        bindAction?.invoke(dialog, binding)

        return dialog
    }
}

class FullWidthAppCompatDialog(context: Context, theme: Int) : AppCompatDialog(context, theme) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
