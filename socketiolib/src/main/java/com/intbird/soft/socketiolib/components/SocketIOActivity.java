package com.intbird.soft.socketiolib.components;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.intbird.soft.socketiolib.R;
import com.intbird.soft.socketiolib.SocketIOSender;

public class SocketIOActivity extends Activity implements View.OnClickListener, SocketIOHandlerActivity.MessageCallback {
    private static final String TAG = "SocketIOActivityTag";

    private TextView tvMessage;
    private EditText identy;
    private EditText editMessage;
    private Button send;
    private Button buttonTest;

    private Button start;
    private Button stop;

    private String httpUrl;

    private boolean socketIOBinderConnect;
    private Messenger serviceMessage;
    private Messenger activityMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socketio_lib_main_activity);

        SocketIOSender.setContext(this.getApplicationContext());

        tvMessage = findViewById(R.id.textview_message);
        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

        editMessage = findViewById(R.id.edit_message);
        send = findViewById(R.id.button_send);
        send.setOnClickListener(sendMessage);

        identy = findViewById(R.id.identifyId);

        start = findViewById(R.id.startService);
        stop = findViewById(R.id.stopService);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);

        buttonTest = findViewById(R.id.button_test);
        buttonTest.setOnClickListener(testClicker);

        httpUrl = getIntent().getStringExtra("url");

        activityMessenger = new Messenger(new SocketIOHandlerActivity(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    private void unBindService() {
        if (socketIOBinderConnect) {
            if (null != connection) {
                unbindService(connection);
            }
            socketIOBinderConnect = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startService) {
            Intent socketIOService = new Intent(SocketIOActivity.this, SocketIOService.class);
            socketIOService.putExtra("url", httpUrl);
            socketIOService.putExtra("identify", identy.getText().toString());
            startService(socketIOService);
            bindService(socketIOService, connection, BIND_AUTO_CREATE);
        } else if (v.getId() == R.id.stopService) {
            Intent socketIOService = new Intent(SocketIOActivity.this, SocketIOService.class);
            stopService(socketIOService);
            unBindService();
        }
    }

    private View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendMessage(SocketIOHandlerFlag.MSG_SEND_SOCKET_MESSAGE, new SocketIOMessageBody(identy.getText().toString(), editMessage.getText().toString()));
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            serviceMessage = new Messenger(service);
            sendMessage(SocketIOHandlerFlag.MSG_CLIENT_REGISTER, new SocketIOMessageBody(identy.getText().toString(), "onServiceConnected"));
            socketIOBinderConnect = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sendMessage(SocketIOHandlerFlag.MSG_CLIENT_UNREGISTER, new SocketIOMessageBody(identy.getText().toString(), "onServiceDisconnected"));
            socketIOBinderConnect = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    private void sendMessage(int what, SocketIOMessageBody messageBody) {
        try {
            Message msg = Message.obtain(null, what, messageBody);
            msg.replyTo = activityMessenger;
            if (null != serviceMessage) {
                serviceMessage.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showSocketMessage(String message) {
        if (null != message && message.length() > 200) {
            message = message.substring(0, 200) + "...";
        }
        tvMessage.setText(message);
    }

    private View.OnClickListener testClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_test) {
                finish();
                Toast.makeText(SocketIOActivity.this, "aop拦截的activity.on**信息将会发送到页面", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
