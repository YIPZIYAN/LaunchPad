package com.example.launchpad.util

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.launchpad.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.Blob
import io.getstream.avatarview.AvatarView
import java.io.ByteArrayOutputStream

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

fun Fragment.loadingDialog(): Dialog {
    val dialog = Dialog(requireContext())
    dialog.setContentView(R.layout.layout_loading)
    return dialog
}

fun Fragment.dialogCompanyNotRegister(status :Boolean,nav:NavController) {
    if (status) {
        dialog(
            getString(R.string.register_your_company),
            getString(R.string.dialog_company_not_register),
            getString(R.string.remind_me_later),
            getString(R.string.register_now),
            onPositiveClick = { _, _ ->
                nav.navigate(R.id.signUpEnterpriseFragment)

            }
        )
    }
}

fun Fragment.displayErrorHelper(view: TextInputLayout, errorMsg: String) {
    view.requestFocus()
    view.error = errorMsg
    view.errorIconDrawable = null
}

fun Context.intentWithoutBackstack(
    context: Context,
    targetClass: Class<*>,
    extras: Bundle? = null
) {
    val intent = Intent(context, targetClass)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    extras?.let {
        intent.putExtras(it)
    }
    context.startActivity(intent)
}
// ----------------------------------------------------------------------------
// Bitmap Extensions
// ----------------------------------------------------------------------------

// Usage: Crop and resize bitmap (upscale)
fun Bitmap.crop(width: Int, height: Int): Bitmap {
    // Source width, height and ratio
    val sw = this.width
    val sh = this.height
    val sratio = 1.0 * sw / sh

    // Target offset (x, y), width, height and ratio
    val x: Int
    val y: Int
    val w: Int
    val h: Int
    val ratio = 1.0 * width / height

    if (ratio >= sratio) {
        // Retain width, calculate height
        w = sw
        h = (sw / ratio).toInt()
        x = 0
        y = (sh - h) / 2
    } else {
        // Retain height, calculate width
        w = (sh * ratio).toInt()
        h = sh
        x = (sw - w) / 2
        y = 0
    }

    return Bitmap
        .createBitmap(this, x, y, w, h) // Crop
        .scale(width, height) // Resize
}

// Usage: Convert from Bitmap to Firebase Blob
fun Bitmap.toBlob(): Blob {
    ByteArrayOutputStream().use {
        compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, it)
        return Blob.fromBytes(it.toByteArray())
    }
}

// ----------------------------------------------------------------------------
// Firebase Blob Extensions
// ----------------------------------------------------------------------------

// Usage: Convert from Blob to Bitmap
fun Blob.toBitmap(): Bitmap? {
    val bytes = toBytes()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

// ----------------------------------------------------------------------------
// ImageView Extensions
// ----------------------------------------------------------------------------

// Usage: Crop to Firebase Blob
fun AvatarView.cropToBlob(width: Int, height: Int): Blob {
    return drawable?.toBitmapOrNull()?.crop(width, height)?.toBlob() ?: Blob.fromBytes(ByteArray(0))
}

// Usage: Load Firebase Blob
fun ImageView.setImageBlob(blob: Blob) {
    setImageBitmap(blob.toBitmap())
}
