package com.esoftwere.hfk.socket

import org.json.JSONObject

interface SocketListener {
    fun onReceive(str: String?, jSONObject: JSONObject?)
}