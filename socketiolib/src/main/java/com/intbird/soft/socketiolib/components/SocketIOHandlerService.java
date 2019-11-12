package com.intbird.soft.socketiolib.components;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SocketIOHandlerService extends Handler {

    public interface MessageCallback {
        void sendSocketMessage(SocketIOMessageBody messageBody);
    }

    private ArrayList<Messenger> mClients = new ArrayList<>();

    private WeakReference<MessageCallback> messageCallback;

    public SocketIOHandlerService(MessageCallback messageCallback) {
        this.messageCallback = new WeakReference<>(messageCallback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case SocketIOHandlerFlag.MSG_CLIENT_REGISTER:
                mClients.add(msg.replyTo);
                break;
            case SocketIOHandlerFlag.MSG_CLIENT_UNREGISTER:
                mClients.remove(msg.replyTo);
                break;
            case SocketIOHandlerFlag.MSG_SEND_SOCKET_MESSAGE:
                if (null != messageCallback && null != messageCallback.get()) {
                    if (msg.obj instanceof SocketIOMessageBody) {
                        messageCallback.get().sendSocketMessage((SocketIOMessageBody)msg.obj);
                    }
                }
                break;
            case SocketIOHandlerFlag.MSG_SHOW_SOCKET_MESSAGE:
                for (int i = mClients.size() - 1; i >= 0; i--) {
                    Message message = Message.obtain(null, SocketIOHandlerFlag.MSG_SHOW_SOCKET_MESSAGE, msg.obj);
                    try {
                        mClients.get(i).send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        mClients.remove(i);
                    }
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

}
