package com.intbird.soft.socketiolib.components;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.intbird.soft.socketiolib.cnative.Logger;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        if (connected || TextUtils.isEmpty(ipAddress)) {
            return;
        }

        try {
            Logger.INSTANCE.d(TAG, ipAddress);
            IO.Options options = ipAddress.startsWith("https")
                    ? SSLOpts(ipAddress)
                    : new IO.Options();

            socket = IO.socket(ipAddress, options);
        } catch (Exception e) {
            Logger.INSTANCE.d(TAG, Log.getStackTraceString(e));
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

    /**
     * https://github.com/socketio/socket.io-client-java
     *
     * @return sslOpts
     */
    private IO.Options SSLOpts(String url) throws Exception {

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return true;
            }
        };

        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        }};

        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(hostnameVerifier)
                .build();
        // default settings for all sockets
//        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
//        IO.setDefaultOkHttpCallFactory(okHttpClient);

        // set as an option
        IO.Options opts = new IO.Options();
        opts.path = "/socket/socket.io/";
        opts.callFactory = okHttpClient;
        opts.webSocketFactory = okHttpClient;
        return opts;
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
            Logger.INSTANCE.d(TAG, message);
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
