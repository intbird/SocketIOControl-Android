package com.intbird.soft.socketiolib.cnative

import android.util.Log

/**
 * https://github.com/socketio/socket.io-client-cpp
 */
object SocketIOSender {
    private const val TAG = "CNSender"

    init {
        System.loadLibrary("native-socket-io-lib")
    }

    external fun sendMessage(message: String);

    fun receiveMessage(result: String) {
        Log.d(TAG, result)
    }
}