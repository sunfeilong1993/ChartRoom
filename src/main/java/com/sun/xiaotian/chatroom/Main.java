package com.sun.xiaotian.chatroom;

import com.sun.xiaotian.chatroom.client.ReadClient;
import com.sun.xiaotian.chatroom.client.WriteClient;

public class Main {
    public static void main(String[] args) {
        new Thread(new Server(9999)).start();
        new Thread(new ReadClient("127.0.0.1", 9999)).start();
        new Thread(new WriteClient("127.0.0.1", 9999)).start();
        new Thread(new WriteClient("127.0.0.1", 9999)).start();
        new Thread(new WriteClient("127.0.0.1", 9999)).start();
    }
}
