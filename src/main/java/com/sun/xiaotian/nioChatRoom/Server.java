package com.sun.xiaotian.nioChatRoom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class Server extends Thread {
    
    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            serverSocketChannel.configureBlocking(false);

            while (true) {
                SocketChannel acceptSocket = serverSocketChannel.accept();
                ByteBuffer readBuffer = ByteBuffer.allocate(32);
                if (acceptSocket != null) {
                    // do something
                    acceptSocket.read(readBuffer);
                    readBuffer.flip();
                    logger.info(acceptSocket.socket().hashCode() + "\t:" + readBuffer.getInt());
                    readBuffer.clear();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
