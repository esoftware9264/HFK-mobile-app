package com.esoftwere.hfk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.esoftwere.hfk.R
import com.wang.avi.AVLoadingIndicatorView

class CustomLoaderDialog (context: Context): Dialog(context) {
    lateinit var aviLoader: AVLoadingIndicatorView

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_custom_loader_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
        initView()
    }

    private fun initView() {
        aviLoader = findViewById(R.id.av_loadingIndicator)
    }

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        super.setOnCancelListener(listener)
        aviLoader.hide()
    }

    override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
        super.setOnShowListener(listener)
        aviLoader.show()
    }
}