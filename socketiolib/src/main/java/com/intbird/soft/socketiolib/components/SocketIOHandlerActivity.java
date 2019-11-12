package com.intbird.soft.socketiolib.components;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SocketIOHandlerActivity extends Handler {

    public interface MessageCallback {
        void showSocketMessage(String message);
    }

    private WeakReference<MessageCallback> messageCallback;

    public SocketIOHandlerActivity(MessageCallback messageCallback) {
        this.messageCallback = new WeakReference<>(messageCallback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case SocketIOHandlerFlag.MSG_SHOW_SOCKET_MESSAGE:
                if (null != messageCallback && null != messageCallback.get()) {
                    messageCallback.get().showSocketMessage(msg.obj.toString());
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
