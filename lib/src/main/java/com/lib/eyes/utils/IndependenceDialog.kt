package com.lib.eyes.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lib.eyes.R
import com.lib.eyes.databinding.DialogErrorInflateBinding
import kotlinx.parcelize.Parcelize
import java.util.UUID

typealias Inflater<T> = (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> T

private val dialogParamKey by lazy {
    "_dialog_param"
}

object IndependenceDialog {
    fun <T : ViewBinding> show(
        activity: FragmentActivity,
        inflater: Inflater<T>,
        cancelable: Boolean = true,
        @StyleRes theme: Int = R.style.TranslucentDialog,
        inflateAction: (DialogInterface.(T) -> Unit)? = null
    ): DialogFragment = CenterDialog.newInstance(inflater, inflateAction, cancelable, theme).apply {
        show(activity.supportFragmentManager, UUID.randomUUID().toString())
    }

    fun <T : ViewBinding> showBottomSheet(
        activity: FragmentActivity,
        inflater: Inflater<T>,
        cancelable: Boolean = true,
        @StyleRes theme: Int = R.style.TranslucentDialog,
        inflateAction: (DialogInterface.(T) -> Unit)? = null
    ): DialogFragment = BottomSheet.newInstance(inflater, inflateAction, cancelable, theme).apply {
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

@Parcelize
data class DialogConstructorParam<T : ViewBinding>(
    val inflater: Inflater<T>,
    val bindAction: (Dialog.(T) -> Unit)? = null,
    val cancelable: Boolean = true,
    @StyleRes val theme: Int = R.style.TranslucentDialog,
    val onDismissListener: () -> Unit = {}
) : Parcelable

class CenterDialog<T : ViewBinding> : AppCompatDialogFragment() {
    private var binding: T? = null
    private var param: DialogConstructorParam<T>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        param = getParam(savedInstanceState)
    }

    private fun getParam(savedInstanceState: Bundle?): DialogConstructorParam<T>? {
        return savedInstanceState?.getParcelable(dialogParamKey)
            ?: arguments?.getParcelable(dialogParamKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return param?.let {
            binding = it.inflater.invoke(inflater, container, false)
            binding!!.root
        } ?: kotlin.run {
            DialogErrorInflateBinding.inflate(inflater, container, false).root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = param?.cancelable ?: true
        dialog?.let {
            binding?.let { bindingSafety ->
                param?.bindAction?.invoke(it, bindingSafety)
            } ?: kotlin.run {
                dismiss()
            }
        } ?: run {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return FullWidthAppCompatDialog(requireContext(), param?.theme ?: theme)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        param?.onDismissListener?.invoke()
    }

    companion object {
        fun <T : ViewBinding> newInstance(
            inflater: Inflater<T>,
            bindAction: (Dialog.(T) -> Unit)? = null,
            cancelable: Boolean = true,
            @StyleRes theme: Int = R.style.TranslucentDialog,
            onDismissListener: () -> Unit = {}
        ): CenterDialog<T> {
            return CenterDialog<T>().apply {
                arguments = bundleOf(
                    dialogParamKey to DialogConstructorParam(inflater, bindAction, cancelable, theme, onDismissListener)
                )
            }
        }
    }
}

class BottomSheet<T : ViewBinding>: BottomSheetDialogFragment() {
    private var binding: T? = null
    private var param: DialogConstructorParam<T>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        param = getParam(savedInstanceState)
    }

    private fun getParam(savedInstanceState: Bundle?): DialogConstructorParam<T>? {
        return savedInstanceState?.getParcelable(dialogParamKey)
            ?: arguments?.getParcelable(dialogParamKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return param?.let {
            binding = it.inflater.invoke(inflater, container, false)
            binding!!.root
        } ?: kotlin.run {
            DialogErrorInflateBinding.inflate(inflater, container, false).root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = param?.cancelable ?: true
        dialog?.let {
            binding?.let { bindingSafety ->
                param?.bindAction?.invoke(it, bindingSafety)
            } ?: kotlin.run {
                dismiss()
            }
        } ?: run {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), param?.theme ?: theme)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        param?.onDismissListener?.invoke()
    }

    companion object {
        fun <T : ViewBinding> newInstance(
            inflater: Inflater<T>,
            bindAction: (Dialog.(T) -> Unit)? = null,
            cancelable: Boolean = true,
            @StyleRes theme: Int = R.style.TranslucentDialog,
            onDismissListener: () -> Unit = {}
        ): BottomSheet<T> {
            return BottomSheet<T>().apply {
                arguments = bundleOf(
                    dialogParamKey to DialogConstructorParam(inflater, bindAction, cancelable, theme, onDismissListener)
                )
            }
        }
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
