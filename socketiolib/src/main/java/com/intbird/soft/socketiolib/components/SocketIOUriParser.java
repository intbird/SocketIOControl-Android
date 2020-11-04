package com.intbird.soft.socketiolib.components;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.intbird.soft.socketiolib.SocketIOSender;
import com.intbird.soft.socketiolib.cnative.Logger;
import com.intbird.soft.socketiolib.plugin.screenshot.ScreenShotActivity;
import com.intbird.soft.socketiolib.utils.Utils;

import org.json.JSONObject;

public class SocketIOUriParser {

    public static final String Schema = "socketio";

    public static final String Remote = "remote";
    public static final String Client = "client";

    private static final String TAG = "SocketIOUriParser";
    private static final String EMPTY = "";

    public JSONObject valSendMessage(SocketIOMessageBody socketIOMessageBody) {
        try {
            if (null != socketIOMessageBody) {
                socketIOMessageBody.doFinalWithMessage();
            }
            JSONObject jsonObject = new JSONObject();
            if (!TextUtils.isEmpty(socketIOMessageBody.identifyId)) {
                jsonObject.put("identifyId", socketIOMessageBody.identifyId);
            }
            if (!TextUtils.isEmpty(socketIOMessageBody.message)) {
                jsonObject.put("message", socketIOMessageBody.message);
            }
            if (!TextUtils.isEmpty(socketIOMessageBody.uri)) {
                jsonObject.put("uri", socketIOMessageBody.uri);
            }
            Logger.INSTANCE.d(TAG, "sendMessage: " + jsonObject.toString());
            return jsonObject;
        } catch (Exception ex) {
            Logger.INSTANCE.d(TAG, ex.getMessage());
        }
        return new JSONObject();
    }

    public String valShowMessage(Object... objects) {
        Object object = null != objects && objects.length > 0 ? objects[0] : "";
        Logger.INSTANCE.d(TAG, "parseMessage: " + object.toString());
        try {
            try {
                SocketIOMessageBody socketIOMessageBody = new SocketIOMessageBody();

                JSONObject jsonObject = new JSONObject(object.toString());
                if (jsonObject.has("identifyId")) {
                    socketIOMessageBody.identifyId = jsonObject.getString("identifyId");
                }
                if (jsonObject.has("message")) {
                    socketIOMessageBody.message = jsonObject.getString("message");
                }
                if (jsonObject.has("uri")) {
                    socketIOMessageBody.uri = jsonObject.getString("uri");
                }

                String showMessage = socketIOMessageBody.toString();
                if (TextUtils.isEmpty(socketIOMessageBody.uri)) {
                    return showMessage;
                } else {
                    try {
                        return parserRemoteUri(socketIOMessageBody.uri, socketIOMessageBody.identifyId, showMessage);
                    } catch (Exception ex) {
                        Logger.INSTANCE.d(TAG, ex.getMessage());
                        return EMPTY;
                    }
                }
            } catch (Exception ex) {
                Logger.INSTANCE.d(TAG, ex.getMessage());
            }
            return object.toString();
        } catch (Exception ex) {
            Logger.INSTANCE.d(TAG, ex.getMessage());
        }
        return EMPTY;
    }

    public String parserRemoteUri(String originUri, String identifyId, String errorMessage) {
        Uri uri = Uri.parse(originUri);
        if (!TextUtils.equals(uri.getScheme(), Schema)) {
            return errorMessage;
        }
        if (!TextUtils.equals(uri.getHost(), Remote)) {
            return errorMessage;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return errorMessage;
        }
        switch (path) {
            case "/clipboardSet":
                Utils.clipSetText(SocketIOSender.getContext(), uri.getQueryParameter("value"));
                return "success:" + path;
            case "/clipboardGet":
                String clipboardText = Utils.clipGetText(SocketIOSender.getContext());
                SocketIOSender.sendMessage(new SocketIOMessageBody(identifyId, "", SocketIOUriParser.buildClientUri(path, "value=" + clipboardText)));
                return "success:" + path;
            case "/screenshot":
                if (null != SocketIOSender.getContext()) {
                    Intent intent = new Intent(SocketIOSender.getContext(), ScreenShotActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SocketIOSender.getContext().startActivity(intent);
                }
                return "success:" + path;
            default:
                Logger.INSTANCE.d(TAG, path + " not found");
                return "";
        }
    }

    public static String buildClientUri(String path) {
        return buildClientUri(path, "");
    }

    public static String buildClientUri(String path, String valuePair) {
        return Schema + "://" + Client + path + (!TextUtils.isEmpty(valuePair) ? "?" : "") + valuePair;
    }
}

