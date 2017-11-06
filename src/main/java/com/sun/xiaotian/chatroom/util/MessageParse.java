package com.sun.xiaotian.chatroom.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xiaotian.chatroom.exception.ChatRoomException;
import com.sun.xiaotian.chatroom.message.Message;

import java.io.IOException;

public class MessageParse {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public synchronized static Message parseFromJson(String jsonStr) {
        Message message;
        try {
            message = objectMapper.readValue(jsonStr, Message.class);
        } catch (IOException e) {
            throw new ChatRoomException(e.getMessage(), e);
        }
        return message;
    }

    public synchronized static String toJson(Message message) {
        String messageStr;
        try {
            messageStr = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new ChatRoomException(e.getMessage(), e);
        }
        return messageStr;
    }
}
