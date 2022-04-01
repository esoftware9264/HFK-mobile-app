package com.esoftwere.hfk.ui.web_view

import android.content.DialogInterface
import android.net.http.SslError
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.ActivityWebViewBinding
import com.esoftwere.hfk.utils.ValidationHelper

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    private val TAG = "WebViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)

        initToolbar()
        initWebView()
        initWebChromeClient()

        loadWebView(getWebUrl())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getToolbarTitle()
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun getWebUrl(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_WEB_URL))
    }

    private fun getToolbarTitle(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_WEB_TOOLBAR_TITLE))
    }

    private fun initWebView() {
        binding.wvLoadWebContent.let { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                pluginState = WebSettings.PluginState.ON
                allowFileAccess = true
            }

            webView.webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler,
                    error: SslError?
                ) {
                    showCustomDialog(handler)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    /*url?.let { view?.loadUrl(it) }
                    return true*/
                    url?.let { webUrl ->
                        if (webUrl?.startsWith("http:") || webUrl.startsWith("https:")) {
                            return false
                        }
                    }

                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    Log.e(TAG, "Request: ${request?.requestHeaders}")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.e(TAG, "Error: ${error?.description}")
                    }
                    super.onReceivedError(view, request, error)
                }
            }
        }
    }

    private fun initWebChromeClient() {
        binding.wvLoadWebContent.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                binding.pbHorizontalLoader.progress = newProgress
                if (newProgress < MAX_WEB_LOADER_PROGRESS && binding.pbHorizontalLoader.visibility == ProgressBar.GONE) {
                    binding.pbHorizontalLoader.visibility = ProgressBar.VISIBLE
                }

                if (newProgress == MAX_WEB_LOADER_PROGRESS) {
                    binding.pbHorizontalLoader.visibility = ProgressBar.GONE
                }
            }
        }
    }

    private fun loadWebView(url: String) {
        if (url.isNotEmpty()) {
            binding.wvLoadWebContent.loadUrl(url)
        }
    }

    private fun showCustomDialog(handler: SslErrorHandler) {
        val alertMSG = "${getString(R.string.ssl_error_alert_msg)}"

        val deleteAddressDialogBuilder = AlertDialog.Builder(this)
        deleteAddressDialogBuilder.setCancelable(true)
            .setMessage(alertMSG)
            .setPositiveButton(
                getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    handler.proceed()
                })
            .setNegativeButton(
                getString(R.string.no),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    handler.cancel()
                })

        val alert = deleteAddressDialogBuilder.create()
        alert.setTitle(getString(R.string.app_name))
        alert.show()
        val buttonPositive: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.primary))
        val buttonNegative: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.primary))
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    companion object {
        const val MAX_WEB_LOADER_PROGRESS = 100
    }
}