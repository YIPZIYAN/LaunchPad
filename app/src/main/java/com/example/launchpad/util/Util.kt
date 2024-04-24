package com.example.launchpad.util

import android.content.DialogInterface
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

fun Fragment.toast(text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.snackbar(text: String) {
    Snackbar.make(view!!, text, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.dialog(
    title: String,
    msg: String,
    txtNegative: String? = "No",
    txtPositive: String? = "Yes",
    onNegativeClick: ((DialogInterface, Int) -> Unit)? = null,
    onPositiveClick: ((DialogInterface, Int) -> Unit)? = null
) {
    val builder = MaterialAlertDialogBuilder(context!!)
        .setTitle(title)
        .setMessage(msg)
    if (onNegativeClick != null) {
        builder.setNegativeButton(txtNegative) { dialog, which ->
            onNegativeClick(dialog, which)
        }
    } else {
        builder.setNegativeButton(txtNegative, null)
    }

    if (onPositiveClick != null) {
        builder.setPositiveButton(txtPositive) { dialog, which ->
            onPositiveClick(dialog, which)
        }
    } else {
        builder.setPositiveButton(txtPositive, null)
    }

    builder.show()
}

fun Fragment.displayErrorHelper(view: TextInputLayout, errorMsg: String) {
    view.requestFocus()
    view.error = errorMsg
    view.errorIconDrawable = null
}
