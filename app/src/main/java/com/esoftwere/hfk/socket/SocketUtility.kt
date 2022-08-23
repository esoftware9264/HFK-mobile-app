package com.esoftwere.hfk.socket

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class SocketUtility {
    /**
     * Returns the SocketIO server URL
     */
    /**
     * This sets the SocketIO server URL Must be set before 'createSocket'
     *
     * @param socketUrl URL of SocketIO server
     */
    var socketUrl: String? = null
    /**
     * Returns the SocketIO server port
     */
    /**
     * This sets the port of the SocketIO server. Must be set before 'createSocket'.
     *
     * @param socketPort Port of SocketIO server. Default value is 3000
     */
    var socketPort: String? = null

    /**
     * Creates the socket in the specified SocketIO server, using socketName. Generally called after successful login to the app.
     *
     * @param queryParams     Identifier by which socket will be created
     * @param socketListener Listener for the socket events.
     * @throws Exception
     */
    @Synchronized
    @Throws(Exception::class)
    fun createSocket(queryParams: String?, socketListener: SocketListener?) {
        if (socketUrl.equals("", ignoreCase = true)) {
            throw Exception(
                "Socket Url is empty",
                Throwable("You must set socket url properly for socket communication")
            )
        }
        val socketUrlWithPort = "$socketUrl:$socketPort"
        Log.e("socketUrlWithPort", socketUrlWithPort)
        Log.e("socketQueryParams", "$queryParams")
        /**
         * Create Socket Reference
         */
        SocketConstants.mSocket = SocketConstants.mSocketLibrary?.startSocket(
            socketUrlWithPort,
            queryParams,
            socketListener!!
        )
    }

    /**
     * Communication through socket
     *
     * @param receiver To whom the message will be sent
     * @param message  The message to be sent
     */
    fun send(receiver: String?, message: String?) {
        val payLoad = JSONObject()
        try {
            /*payLoad.put("customer", receiver);
            payLoad.put("message", message);*/
            payLoad.put("msg", message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        SocketConstants.mSocket?.emit(receiver, payLoad)
    }

    /**
     * Communication through socket
     *
     * @param receiver    To whom the message will be sent
     * @param jsonMessage The message to be sent as JSON Object
     */
    fun send(receiver: String, payload: JSONObject) {
        SocketConstants.mSocket?.emit(receiver, payload)
    }

    /**
     * Removes the socket from the SocketIO server. Generally called after successful logout from the app.
     */
    fun disconnectSocket() {
        if (SocketConstants.mSocket != null) {
            SocketConstants.mSocket!!.disconnect()
        }
    }
}