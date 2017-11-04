package com.sun.xiaotian.nioChatRoom.message;

import com.alibaba.fastjson.annotation.JSONType;

import java.util.Date;


public abstract class Message {

    private long clientId;

    private Date sendTime;

    private Date acceptTime;

    public Message(long clientId, Date sendTime, Date acceptTime) {
        this.clientId = clientId;
        this.sendTime = sendTime;
        this.acceptTime = acceptTime;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "clientId=" + clientId +
                ", sendTime=" + sendTime +
                ", acceptTime=" + acceptTime +
                '}';
    }
}
