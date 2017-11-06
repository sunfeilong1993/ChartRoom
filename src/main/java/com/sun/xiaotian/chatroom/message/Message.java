package com.sun.xiaotian.chatroom.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "className"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextMessage.class, name = "TextMessage")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Message {

    private long clientId;

    private Date sendTime;

    private Date acceptTime;

    public Message() {

    }

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
