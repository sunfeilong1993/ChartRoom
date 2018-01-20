package com.sun.xiaotian.chatroom.client;

import com.sun.xiaotian.chatroom.TypeInfo;
import com.sun.xiaotian.chatroom.data.ChannelDataReader;
import com.sun.xiaotian.chatroom.data.ChannelDataWriter;
import com.sun.xiaotian.chatroom.data.ClientSendData;
import com.sun.xiaotian.chatroom.data.ServerSendData;
import com.sun.xiaotian.chatroom.message.Message;
import com.sun.xiaotian.chatroom.message.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReadClient extends Client {

    private ChannelDataReader channelDataReader = new ChannelDataReader();
    private ChannelDataWriter channelDataWriter = new ChannelDataWriter();

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

                writeClientInfo(clientSocket);
                readMessage(clientSocket);
                TimeUnit.SECONDS.sleep(random.nextInt(10));
                clientSocket.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void writeClientInfo(SocketChannel channel) {
        ClientSendData clientSendData = new ClientSendData();
        clientSendData.setClientId(id);
        clientSendData.setClientType(TypeInfo.CLIENT_READ);
        clientSendData.setMessage(TextMessage.NULL);
        channelDataWriter.writeToClientSocket(channel, clientSendData);
    }

    private void readMessage(SocketChannel channel) throws IOException, InterruptedException {
        ServerSendData serverSendData = channelDataReader.readFromServerSocket(channel);
        List<Message> messages = serverSendData.getMessages();
        if (messages != null) {
            messages.forEach(message -> {
                System.out.println(message.toString());
            });
        }
    }
}
