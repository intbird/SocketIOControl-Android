package com.intbird.soft.socketiolib.components;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOService extends Service implements SocketIOHandlerService.MessageCallback {

    private static final String TAG = "SocketIOServiceTag";

    final String ON_MESSAGE = "message";
    final String EMIT_MESSAGE = "message";

    private SocketIOMessageBody messageBody = new SocketIOMessageBody();

    private boolean connected;
    private Socket socket;
    private Messenger messenger = new Messenger(new SocketIOHandlerService(this));

    private SocketIOUriParser socketIOUriParser = new SocketIOUriParser();

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        if (intent.hasExtra("socketIOMessageBody")) {
            SocketIOMessageBody socketIOMessageBody = (SocketIOMessageBody) intent.getSerializableExtra("socketIOMessageBody");
            messageBody.copyIfNotEmpty(socketIOMessageBody);
            sendMessage(messageBody);
        }
        connect(url);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void connect(String ipAddress) {
        if (connected) {
            return;
        }
        try {
            socket = IO.socket(ipAddress);
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }

        if (null == socket) {
            return;
        }
        socket.on(Socket.EVENT_CONNECT, this.onConnectListener);
        socket.on(Socket.EVENT_DISCONNECT, this.onDisConnectListener);
        socket.on(ON_MESSAGE, this.onMessageListener);
        socket.connect();
        connected = true;
    }

    private void disconnect() {
        if (null != socket) {
            socket.off(Socket.EVENT_CONNECT);
            socket.off(Socket.EVENT_DISCONNECT);
            socket.off(ON_MESSAGE);
            socket.disconnect();
            connected = false;
        }
    }

    private Emitter.Listener onConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            showMessage("connect: " + socket.id());
        }
    };

    private Emitter.Listener onMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            showMessage("message: " + socketIOUriParser.valShowMessage(objects));
        }
    };

    private Emitter.Listener onDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            showMessage("disconnect:" + socket.id());
        }
    };

    private void sendMessage(SocketIOMessageBody message) {
        if (!connected) {
            return;
        }
        socket.emit(EMIT_MESSAGE, socketIOUriParser.valSendMessage(message));
    }

    private void showMessage(String message) {
        Message msg = Message.obtain(null, SocketIOHandlerFlag.MSG_SHOW_SOCKET_MESSAGE, message);
        try {
            if (null != messenger) {
                messenger.send(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendSocketMessage(SocketIOMessageBody messageBody) {
        sendMessage(messageBody);
    }
}
