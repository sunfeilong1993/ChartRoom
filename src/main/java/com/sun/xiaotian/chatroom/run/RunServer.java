package com.sun.xiaotian.chatroom.run;

import com.sun.xiaotian.chatroom.server.Server;

public class RunServer {
    public static void main(String[] args) {
        new Server(9999).start();
    }
}
