package com.sun.xiaotian.chatroom.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.xiaotian.chatroom.message.Message;
import com.sun.xiaotian.chatroom.message.TextMessage;

public class MessageParse {

    public static Message parseFromJson(String jsonStr) {
        Message message = JSON.parseObject(jsonStr, TextMessage.class);
        return message;
    }

    public static String toJson(Message message) {
        SerializerFeature[] features = new SerializerFeature[] {
                SerializerFeature.WriteClassName
        };
        JSON.toJSONString(message, features);
        return JSON.toJSONString(message);
    }
}
