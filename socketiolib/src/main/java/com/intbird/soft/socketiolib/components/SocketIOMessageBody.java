package com.intbird.soft.socketiolib.components;

import android.text.TextUtils;

import com.intbird.soft.socketiolib.SocketIOSender;

import java.io.Serializable;

public class SocketIOMessageBody implements Serializable {
    public String identifyId;
    public String message;
    public String messageKey;
    public String uri;

    public SocketIOMessageBody() {
    }

    public SocketIOMessageBody(String identifyId, String message) {
        this.identifyId = identifyId;
        this.message = message;
    }

    public SocketIOMessageBody(String identifyId, String message, String uri) {
        this.identifyId = identifyId;
        this.message = message;
        this.uri = uri;
    }

    public SocketIOMessageBody setMessageKey(String messageKey, String messageContent) {
        this.messageKey = messageKey;
        SocketIOSender.putMessageByKey(messageKey, messageContent);
        return this;
    }

    public SocketIOMessageBody setMessage(String message) {
        this.message = message;
        return this;
    }

    public SocketIOMessageBody copyIfNotEmpty(SocketIOMessageBody socketIOMessageBody) {
        if (null == socketIOMessageBody) {
            return this;
        }
        if (!TextUtils.isEmpty(socketIOMessageBody.identifyId)) {
            this.identifyId = socketIOMessageBody.identifyId;
        }
        if (!TextUtils.isEmpty(socketIOMessageBody.messageKey)) {
            this.messageKey = socketIOMessageBody.messageKey;
        }
        if (!TextUtils.isEmpty(socketIOMessageBody.message)) {
            this.message = socketIOMessageBody.message;
        }
        if (!TextUtils.isEmpty(socketIOMessageBody.uri)) {
            this.uri = socketIOMessageBody.uri;
        }
        return this;
    }

    public SocketIOMessageBody doFinalWithMessage() {
        if (!TextUtils.isEmpty(messageKey)) {
            this.message = SocketIOSender.getMessageByKey(messageKey);
            this.messageKey = "";
        }
        return this;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "identifyId='" + identifyId + '\'' +
                ", message='" + message + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}