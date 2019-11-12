package com.intbird.soft.socketiolib.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Utils {
    public static void clipSetText(Context contextRef, String clipText) {
        if (null == contextRef) {
            return;
        }
        Context context = contextRef.getApplicationContext();
        ClipData clipData = ClipData.newPlainText("", clipText);
        ((ClipboardManager) (context.getSystemService(Context.CLIPBOARD_SERVICE))).setPrimaryClip(clipData);
    }

    public static String clipGetText(Context contextRef) {
        if (null == contextRef) {
            return "";
        }
        Context context = contextRef.getApplicationContext();
        ClipData clipData = ((ClipboardManager) (context.getSystemService(Context.CLIPBOARD_SERVICE))).getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        return item.getText().toString();
    }
}
