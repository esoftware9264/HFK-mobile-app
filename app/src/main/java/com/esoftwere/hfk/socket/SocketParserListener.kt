package com.esoftwere.hfk.socket

import org.json.JSONObject

interface SocketParserListener {
    fun onParseSocket(event: String?, parseMessage: JSONObject?)
}