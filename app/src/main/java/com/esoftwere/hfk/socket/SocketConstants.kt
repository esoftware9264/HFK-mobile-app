package com.esoftwere.hfk.socket

import io.socket.client.Socket

object SocketConstants {
    const val SOCKET_URL = "https://hfk-ws.herokuapp.com"
    const val SOCKET_PORT = "443"

    /*** Socket Emit Event  */
    const val SOCKET_EMIT_CHAT_CONNECT_EVENT = "chat_connect"
    const val SOCKET_EMIT_SEND_MSG_EVENT = "send_chat_msg"
    const val SOCKET_RECEIVE_EVENT = "get message"
    const val SOCKET_EMIT_EVENT_LAST_LOCATION_UPDATE = "last_location_update"
    const val SOCKET_EMIT_EVENT_REMOVE = "remove"

    /*** Socket Receive Event  */
    const val SOCKET_RECEIVE_MSG_EVENT = "receive"
    const val SOCKET_RECEIVE_EVENT_DRIVER_LOCATION_DETAILS = "driver_location_details"

    /*** Socket Handling Objects  */
    var mSocketLibrary: SocketLibrary? = null
    var mSocket: Socket? = null
    var mSocketUtility: SocketUtility? = null
    var mSocketParser: SocketParser? = null
}