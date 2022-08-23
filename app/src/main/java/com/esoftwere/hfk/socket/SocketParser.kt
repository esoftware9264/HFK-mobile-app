package com.esoftwere.hfk.socket

import android.app.Activity
import android.util.Log
import org.json.JSONObject

class SocketParser {
    private var socketEvent: String? = null
    private var socketMsg: JSONObject? = null
    private var mActivity: Activity? = null
    private var mSocketParserListener: SocketParserListener? = null

    companion object {
        private const val TAG = "SocketParser"
    }

    @Synchronized
    fun createSocket(queryParams: String?) {
        try {
            SocketConstants.mSocketUtility!!.createSocket(queryParams, object : SocketListener {
                override fun onReceive(event: String?, jsonObject: JSONObject?) {
                    try {
                        socketEvent = event
                        socketMsg = jsonObject
                        if (socketMsg != null) {
                            Log.e("onReceive", "SocketParseHandler: $socketMsg")
                            Log.e("OnReceive", "Event: $event")

                             parseSocket()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "ExceptionCause: " + e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("CreateSocketException", "ExceptionCause: " + e.message)
        }
    }

    fun initSocketParserListener(activity: Activity?, socketParserListener: SocketParserListener) {
        mActivity = activity
        mSocketParserListener = socketParserListener
    }

    private fun parseSocket() {
        if (mActivity != null) {
            mActivity!!.runOnUiThread {
                if (mSocketParserListener != null) {
                    mSocketParserListener!!.onParseSocket(socketEvent, socketMsg)
                }
            }
        }
    }


}