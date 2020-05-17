package com.intbird.soft.socketiolib.cnative

object Logger {
    private const val TAG = "CNLogger"

    external fun d(tag: String, message: String)
    external fun e(tag: String, message: String)

    init {
        System.loadLibrary("native-logger-lib")
    }
}