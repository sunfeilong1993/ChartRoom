package com.sun.xiaotian.chatroom.client;

import com.sun.xiaotian.chatroom.TypeInfo;
import com.sun.xiaotian.chatroom.message.Message;
import com.sun.xiaotian.chatroom.message.TextMessage;
import com.sun.xiaotian.chatroom.util.MessageParse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WriteClient extends Client {

    private final Random random = new Random(37);

    public WriteClient(String host, int port) {
        super("write", host, port);
    }

    @Override
    public void execute() {
        try {
            while (true) {
                SocketChannel clientSocket = SocketChannel.open();
                clientSocket.connect(new InetSocketAddress(host, port));

                while (!clientSocket.finishConnect()) {
                    TimeUnit.MICROSECONDS.sleep(100);
                }
                writeMessage(clientSocket, "write");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMessage(SocketChannel channel, String context) throws IOException, InterruptedException {
        //发送消息
        ByteBuffer writeBuffer = ByteBuffer.allocate(32);
        writeBuffer.putInt(TypeInfo.CLIENT_WRITE);
        writeBuffer.flip();
        channel.write(writeBuffer);
        writeBuffer.clear();
        TextMessage message = new TextMessage(id, new Date(), null, random.nextInt() + context);
        byte[] messageBytes = MessageParse.toJson(message).getBytes();
        writeBuffer = ByteBuffer.allocate(messageBytes.length);
        writeBuffer.put(messageBytes, 0, messageBytes.length);
        writeBuffer.flip();
        channel.write(writeBuffer);
        writeBuffer.clear();
        TimeUnit.SECONDS.sleep(random.nextInt(10));
        channel.close();
    }
}
