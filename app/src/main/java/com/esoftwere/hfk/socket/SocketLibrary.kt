package com.esoftwere.hfk.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.lang.Exception
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class SocketLibrary {
    private var connectedCallback: Emitter.Listener? = null
    private var disconnetedCallback: Emitter.Listener? = null
    private var reconnectCallback: Emitter.Listener? = null
    private var errorCallback: Emitter.Listener? = null
    private var receiveMSGCallback: Emitter.Listener? = null

    companion object {
        //private Emitter.Listener driverLocationDetailsCallback;
        private const val TAG = "SocketLibrary"
    }

    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }
    })

    @Synchronized
    fun startSocket(serverUrl: String?, queryParams: String?, listen: SocketListener): Socket? {
        return try {
            val options = IO.Options()
            val sc = SSLContext.getInstance("TLS")
            sc.init(null as Array<KeyManager?>?, trustAllCerts, SecureRandom())
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(RelaxedHostNameVerifier())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()

            // default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
            IO.setDefaultOkHttpCallFactory(okHttpClient)

            //Socket Options
           /* if (serverUrl.startsWith("https://")) {
                Log.i(TAG, ">>>>>>>>>> secured <<<<<<<<");
                options.secure = true
                options.port = 443
                options.forceNew = true
                options.multiplex = false
            }*/
            options.secure = true
            options.port = 443
            options.forceNew = true
            options.reconnection = true
            options.multiplex = false
            options.query = queryParams
            val mSocket = IO.socket(serverUrl, options)
            connectedCallback = Emitter.Listener { println("socket >>>>>>>>>> $mSocket") }
            disconnetedCallback = Emitter.Listener { println("disconnected socket") }
            reconnectCallback = Emitter.Listener { println("reconnected socket") }
            errorCallback = Emitter.Listener { args -> println("error :$args") }
            receiveMSGCallback = Emitter.Listener { args ->
                val argument = args[0] as JSONObject
                println("ReceiveMSGListener :$argument")
                listen.onReceive(SocketConstants.SOCKET_RECEIVE_MSG_EVENT, argument)
            }
            mSocket.on(Socket.EVENT_CONNECT, connectedCallback)
                .on(Socket.EVENT_DISCONNECT, disconnetedCallback)
                .on("error", errorCallback)
                .on(SocketConstants.SOCKET_RECEIVE_MSG_EVENT, receiveMSGCallback)
            mSocket.connect()
            mSocket
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "${e.printStackTrace()}")
            null
        } catch (e2: KeyManagementException) {
            Log.e(TAG, "${e2.printStackTrace()}")
            null
        } catch (e3: URISyntaxException) {
            Log.e(TAG, "${e3.printStackTrace()}")
            null
        } catch (e4: Exception) {
            Log.e(TAG, "${e4.printStackTrace()}")
            null
        }
    }

    class RelaxedHostNameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }
}