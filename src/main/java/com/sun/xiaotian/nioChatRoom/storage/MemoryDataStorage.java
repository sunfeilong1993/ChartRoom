package com.sun.xiaotian.nioChatRoom.storage;

import com.sun.xiaotian.nioChatRoom.exception.ChatRomException;
import com.sun.xiaotian.nioChatRoom.message.Message;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 内存数据存储
 */
public class MemoryDataStorage implements DataStorage {

    List<Message> allMessages = Collections.synchronizedList(new LinkedList<Message>());


    public boolean add(Message message) {
        return allMessages.add(message);
    }


    public List<Message> getMessages(int start, int end) {
        if (start < 0 || end > allMessages.size()) {
            throw new ChatRomException("获取消息失败，请确认范围是否正确！");
        }
        return allMessages.subList(start, end);
    }

    public List<Message> getAll() {
        return allMessages;
    }

    public int messageCount() {
        return allMessages.size();
    }
}
