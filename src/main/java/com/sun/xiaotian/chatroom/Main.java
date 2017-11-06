package com.sun.xiaotian.chatroom;

public class Main {

    public static void main(String[] args) {
        new Thread(new Server(9999)).start();
        new Thread(new Client("127.0.0.1", 9999)).start();
        new Thread(new Client("127.0.0.1", 9999)).start();
        new Thread(new Client("127.0.0.1", 9999)).start();
    }
}
