package com.sun.xiaotian.chatroom.client;

import com.sun.xiaotian.chatroom.TypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReadClient extends Client {

    private final static Logger logger = LoggerFactory.getLogger(ReadClient.class);

    public ReadClient(String host, int port) {
        super("read", host, port);
    }

    @Override
    public void execute() {
        try {
            Random random = new Random(37);
            while (true) {
                SocketChannel clientSocket = SocketChannel.open();
                clientSocket.configureBlocking(false);
                clientSocket.connect(new InetSocketAddress(host, port));

                while (!clientSocket.finishConnect()) {
                    TimeUnit.MICROSECONDS.sleep(100);
                }
                readMessage(clientSocket);
                TimeUnit.SECONDS.sleep(random.nextInt(10));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void readMessage(SocketChannel channel) throws IOException, InterruptedException {
        //发送消息
        ByteBuffer writeBuffer = ByteBuffer.allocate(32);
        //客户端类型
        writeBuffer.putInt(TypeInfo.CLIENT_READ);
        writeBuffer.flip();
        channel.write(writeBuffer);
        writeBuffer.clear();

        //客户端ID
        writeBuffer = ByteBuffer.allocate(64);
        writeBuffer.putLong(id);
        writeBuffer.flip();
        channel.write(writeBuffer);
        writeBuffer.clear();

        while (true){
            ByteBuffer readBuffer = ByteBuffer.allocate(4);
            int length = 0;
            while (readBuffer.position() < 4) {
                length = channel.read(readBuffer);
                if (length <= -1) {
                    return;
                }
            }
            readBuffer.flip();
            int size = readBuffer.getInt();

            readBuffer = ByteBuffer.allocate(size);
            while (readBuffer.position() < size) {
                length = channel.read(readBuffer);
                if (length == -1) {
                    return;
                }
            }
            readBuffer.flip();
            System.out.println(new String(readBuffer.array()));
        }
    }
}
