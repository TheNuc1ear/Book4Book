package com.infosys.b4b;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean read;

    public Chat(String sender, String receiver, String msg, boolean read) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = msg;
        this.read = read;
    }

    public Chat(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
