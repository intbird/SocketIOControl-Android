package com.intbird.soft.socketiolib;

import android.content.Context;
import android.content.Intent;

import com.intbird.soft.socketiolib.components.SocketIOMessageBody;
import com.intbird.soft.socketiolib.components.SocketIOService;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * send all messages by service
 * activity only show service status and message
 * mostly activity is not to be used.
 */
public class SocketIOSender {

    private static WeakReference<Context> context;
    private static Map<String, String> mapData = new HashMap<>();

    public static void setContext(Context context) {
        SocketIOSender.context = new WeakReference<>(context);
    }

    public static Context getContext() {
        if (null != SocketIOSender.context && null != SocketIOSender.context.get()) {
            return SocketIOSender.context.get();
        }
        return null;
    }

    public static String getMessageByKey(String messageKey) {
        return mapData.get(messageKey);
    }

    public static String putMessageByKey(String messageKey, String message) {
        return mapData.put(messageKey, message);
    }



    public static void sendMessage(SocketIOMessageBody socketIOMessageBody) {
        Context context = getContext();
        sendMessage(context, socketIOMessageBody);
    }

    public static void sendMessage(Context context, SocketIOMessageBody socketIOMessageBody) {
        if (null == context) {
            return;
        }
        Intent socketIOService = new Intent(context.getApplicationContext(), SocketIOService.class);
        socketIOService.putExtra("socketIOMessageBody", socketIOMessageBody);
        context.startService(socketIOService);
    }
}
