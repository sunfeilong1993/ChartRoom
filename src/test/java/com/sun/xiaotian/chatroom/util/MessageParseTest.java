package com.sun.xiaotian.chatroom.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.xiaotian.chatroom.message.Message;
import com.sun.xiaotian.chatroom.message.TextMessage;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MessageParseTest {

    private final static Logger logger = LoggerFactory.getLogger(MessageParseTest.class);

    List<Message> messages;

    @Before
    public void before() {
        messages = new ArrayList<Message>();
        messages.add(new TextMessage(1l, new Date(), null, "ew1"));
        messages.add(new TextMessage(2l, new Date(), null, "ew2"));
        messages.add(new TextMessage(3l, new Date(), null, "ew3"));
    }

    @Test
    public void 测试对象解析和反解析() throws Exception {
        Message message = messages.get(0);
        String json = JsonMessageParse.writeToJson(message);
        Message message1 = JsonMessageParse.readFromJson(json);
        logger.info(json);
        assertTrue(message.equals(message1));
    }


    @Test
    public void 测试列表序列化和反序列化() throws Exception {
        String json = JsonMessageParse.writeToListJson(messages);
        List<Message> result = JsonMessageParse.readFromListJson(json);
        logger.info(json);
        assertTrue(messages.get(0).equals(result.get(0)));
        assertTrue(messages.get(0).equals(result.get(0)));
        assertTrue(messages.get(0).equals(result.get(0)));
    }

}