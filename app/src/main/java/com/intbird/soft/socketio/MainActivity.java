package com.intbird.soft.socketio;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.intbird.soft.socketiolib.cnative.Logger;
import com.intbird.soft.socketiolib.components.SocketIOActivity;
import com.intbird.soft.socketiolib.components.SocketIOUriParser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String url_remote = "socketio://intbird.net:996";
    private static final String url_local = "socketio://192.168.2.200:8080";
    private static final String url = url_remote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        TextView textView = findViewById(R.id.label);
        textView.setText("扫码功能暂时未做\n确保电脑已经打开" + url + " 网址");

        findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSocketActivity(MainActivity.this, url);

            }
        });

        findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
    }

    private void startSocketActivity(Context context, String codeResult) {
        if (codeResult.startsWith(SocketIOUriParser.Schema)) {
            try {
                Intent intent = new Intent(context, SocketIOActivity.class);
                intent.putExtra("url", Uri.parse(codeResult).buildUpon().scheme("http").build().toString());
                context.startActivity(intent);
            } catch (Exception e) {
                Logger.INSTANCE.e(TAG, "onScanQRCodeSuccess: " + Log.getStackTraceString(e));
            }
        }
    }
}
