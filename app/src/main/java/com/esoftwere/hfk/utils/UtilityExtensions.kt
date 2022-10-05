package com.esoftwere.hfk.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.esoftwere.hfk.R
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Hides Soft Input Keyboard
 */
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Clear AppCompatEditText/EditText
 */
fun AppCompatEditText.clear() {
    text?.clear()
}

/**
 * Concat Less than 10 value with prefix 0
 */
fun Long.formatLongDecimal(): String {
    return if (this in 0..9) "0$this" else this.toString()
}

/**
 * Load ImageView With Glide Extension
 */
fun AppCompatImageView.loadImageFromUrl(
    url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_placeholder
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(placeholder)
        .into(this)
}

fun CircleImageView.loadImageFromUrl(
    url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_placeholder
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(placeholder)
        .into(this)
}

fun ShapeableImageView.loadImageFromUrl(
    url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_placeholder
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(placeholder)
        .into(this)
}


/**
 * invoke on AppCompatEditText -> imeOptions "actionDone"
 */
fun AppCompatEditText.onActionDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        }
        false
    }
}

/**
 * Toggle Visibility Of View Widgets
 */
fun View.toggleVisibility() {
    if (this.isVisible) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

/**
 * Enable/Disable all view elements in single go
 */
fun View.setAllEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) children.forEach { child -> child.setAllEnabled(enabled) }
}

/**
 * Add INR Symbol To Price Data
 */
fun String?.addINRSymbolToPrice(): String {
    if (ValidationHelper.optionalBlankText(this).isNotEmpty()) {
        return "₹ $this /-"
    }

    return "-"
}

fun String?.addINRSymbolToPriceWithoutSuffix(): String {
    if (ValidationHelper.optionalBlankText(this).isNotEmpty()) {
        return "₹ $this"
    }

    return "-"
}

/**
 * append command after every 3 chars
 */
fun String.formatDecimalSeparator(): String {
    return this
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

/**
 * Remove comma for last digit
 */
fun String.removeLstCommaFromString(): String {
    if (this.endsWith(",", true)) {
        return this.substring(0, this.length - 1)
    }

    return this
}

/**
 * Disable AppCompatButton
 */
fun AppCompatButton.disable() {
    setBackgroundResource(R.drawable.drawable_curved_solid_gray)
    isClickable = false
    isEnabled = false
}

/**
 * Enable AppCompatButton
 */
fun AppCompatButton.enable() {
    setBackgroundResource(R.drawable.drawable_curved_solid_primary)
    isClickable = true
    isEnabled = true
}
