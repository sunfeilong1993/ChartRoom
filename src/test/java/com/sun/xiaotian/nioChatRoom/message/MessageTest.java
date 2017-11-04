package com.sun.xiaotian.nioChatRoom.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MessageTest {

    private final static Logger logger = LoggerFactory.getLogger(MessageTest.class);

    @Test
    public void 测试Jackson序列化反序列化() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Message> messages = new ArrayList<Message>();
        messages.add(new TextMessage(1l, new Date(), null, "ew1"));
        messages.add(new TextMessage(2l, new Date(), null, "ew2"));
        messages.add(new TextMessage(3l, new Date(), null, "ew3"));

        String jsonStr = objectMapper.writerFor(new TypeReference<List<Message>>(){}).writeValueAsString(messages);

        List<Message> result = objectMapper.readerFor(new TypeReference<List<Message>>(){}).readValue(jsonStr);

        assertTrue(messages.size() == result.size());
        assertTrue(messages.get(0).getClientId() == result.get(0).getClientId());

    }
}