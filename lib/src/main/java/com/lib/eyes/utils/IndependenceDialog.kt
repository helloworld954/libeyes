package com.lib.eyes.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
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
        @StyleRes theme: Int = R.style.TranslucentDialog,
        inflateAction: (DialogInterface.(T) -> Unit)? = null
    ): DialogFragment = CenterDialog(inflater, inflateAction, cancelable, theme).apply {
        show(activity.supportFragmentManager, UUID.randomUUID().toString())
    }

    fun <T : ViewBinding> showBottomSheet(
        activity: FragmentActivity,
        inflater: Inflater<T>,
        cancelable: Boolean = true,
        @StyleRes theme: Int = R.style.TranslucentDialog,
        inflateAction: (DialogInterface.(T) -> Unit)? = null
    ): DialogFragment = BottomSheet(inflater, inflateAction, cancelable, theme).apply {
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
    private val inflater: Inflater<T>,
    private val bindAction: (Dialog.(T) -> Unit)? = null,
    private val cancelable: Boolean = true,
    @StyleRes private val theme: Int = R.style.TranslucentDialog
) : AppCompatDialogFragment() {
    private lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = this.inflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = cancelable
        dialog?.let {
            bindAction?.invoke(it, binding)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return FullWidthAppCompatDialog(requireContext(), theme)
    }
}

class BottomSheet<T : ViewBinding>(
    private val inflater: Inflater<T>,
    private val bindAction: (Dialog.(T) -> Unit)? = null,
    private val cancelable: Boolean = true,
    @StyleRes private val theme: Int = R.style.TranslucentDialog
) : BottomSheetDialogFragment() {
    private lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = this.inflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = cancelable
        dialog?.let {
            bindAction?.invoke(it, binding)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
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
