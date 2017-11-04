package com.sun.xiaotian.nioChatRoom;

import com.sun.xiaotian.nioChatRoom.message.Message;
import com.sun.xiaotian.nioChatRoom.storage.DataStorage;
import com.sun.xiaotian.nioChatRoom.storage.MemoryDataStorage;
import com.sun.xiaotian.nioChatRoom.util.MessageParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;


public class Server extends Thread {

    private int port = 0;

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private DataStorage dataStorage;

    public Server(int port) {
        this.port = port;
        dataStorage = new MemoryDataStorage();
    }

    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            serverSocketChannel.configureBlocking(false);
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

            while (true) {
                SocketChannel acceptSocket = serverSocketChannel.accept();
                if (acceptSocket != null) {
                    int read = acceptSocket.read(readBuffer);
                    while (read != -1) {
                        read = acceptSocket.read(readBuffer);
                    }
                    readBuffer.flip();
                    Message message = MessageParse.parseFromJson(new String(readBuffer.array(), 0, readBuffer.limit()));
                    message.setAcceptTime(new Date());
                    dataStorage.add(message);
                    logger.info(message.toString());
                    readBuffer.clear();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
