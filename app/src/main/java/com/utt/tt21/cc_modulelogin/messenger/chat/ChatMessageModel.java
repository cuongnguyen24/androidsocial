package com.utt.tt21.cc_modulelogin.messenger.chat;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private String timestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, String timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
}
