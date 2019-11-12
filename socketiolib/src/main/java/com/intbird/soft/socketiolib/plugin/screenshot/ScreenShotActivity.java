package com.intbird.soft.socketiolib.plugin.screenshot;


import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;

import com.intbird.soft.socketiolib.plugin.payload.PayloadActivity;

public class ScreenShotActivity extends PayloadActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestScreenShot();
    }

    public void requestScreenShot() {
        startActivityForResult(createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    private Intent createScreenCaptureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent();
            } catch (Exception ignored) {
            }
        }
        toast("screen shot failed");
        return new Intent();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == RESULT_OK && data != null) {
                    Shotter shotter = new Shotter(ScreenShotActivity.this, resultCode, data);
                    shotter.startScreenShot(new Shotter.OnShotListener() {
                        @Override
                        public void onFinish() {
                            toast("shot finish!");
                            finish(); // don't forget finish activity
                        }
                    });
                } else if (resultCode == RESULT_CANCELED) {
                    toast("shot cancel , please give permission.");
                } else {
                    toast("unknow exceptions!");
                }
            }
        }
    }
}