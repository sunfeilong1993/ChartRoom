package com.sun.xiaotian.chatroom.client;

import com.sun.xiaotian.chatroom.TypeInfo;
import com.sun.xiaotian.chatroom.data.ChannelDataWriter;
import com.sun.xiaotian.chatroom.data.ClientSendData;
import com.sun.xiaotian.chatroom.message.TextMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WriteClient extends Client {

    private final static Logger logger = LogManager.getLogger(WriteClient.class);

    private ChannelDataWriter channelDataWriter = new ChannelDataWriter();

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
                clientSocket.close();
                TimeUnit.SECONDS.sleep(random.nextInt(10));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMessage(SocketChannel channel, String context) throws IOException, InterruptedException {
        //发送消息
        ClientSendData clientSendData = new ClientSendData();
        clientSendData.setClientType(TypeInfo.CLIENT_WRITE);
        clientSendData.setClientId(id);
        clientSendData.setMessage(new TextMessage(id, new Date(), null, random.nextInt(1000) + context));
        channelDataWriter.writeToClientSocket(channel, clientSendData);
        logger.info("writeData:\t" + clientSendData.getMessage().toString());
    }
}
