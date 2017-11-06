package com.sun.xiaotian.chatroom;

import com.sun.xiaotian.chatroom.message.TextMessage;
import com.sun.xiaotian.chatroom.util.MessageParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Client implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    private static AtomicLong num = new AtomicLong(0l);

    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }


    private final long id = num.incrementAndGet();

    public void run() {
        Random random = new Random(37);
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        try {
            while (true) {
                SocketChannel clientSocket = SocketChannel.open();
                clientSocket.connect(new InetSocketAddress(host, port));

                while (!clientSocket.finishConnect()) {
                    TimeUnit.MICROSECONDS.sleep(100);
                }

                TextMessage message = new TextMessage(id, new Date(), null, random.nextInt() + "");
                byte[] messageBytes = MessageParse.toJson(message).getBytes();
                writeBuffer.put(messageBytes, 0, messageBytes.length);
                writeBuffer.flip();
                clientSocket.write(writeBuffer);
                writeBuffer.clear();

                TimeUnit.SECONDS.sleep(random.nextInt(Math.abs(10)));
                clientSocket.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getId() {
        return id;
    }
}
