package com.sun.xiaotian.chatroom.data;

import com.sun.xiaotian.chatroom.exception.ChatRoomException;
import com.sun.xiaotian.chatroom.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ChannelDataReader {
    private final static Logger logger = LoggerFactory.getLogger(ChannelDataReader.class);

    private final static DataReadHelper dataReadHelper = new DataReadHelper();

    public ClientSendData readFromClientSocket(SocketChannel channel) {
        ClientSendData clientSendData = new ClientSendData();
        try {
            int clientType = dataReadHelper.readInt(channel);
            if (clientType == -1) {
                logger.info("No Data ...");
                return clientSendData;
            }
            clientSendData.setClientType(clientType);

            long clientId = dataReadHelper.readLong(channel);
            clientSendData.setClientId(clientId);

            int messageSize = dataReadHelper.readInt(channel);
            clientSendData.setMessageSize(messageSize);

            if (messageSize == -1) {
                clientSendData.setMessage(null);
            } else {
                Message message = dataReadHelper.readMessage(channel, messageSize);
                clientSendData.setMessage(message);
            }
        } catch (IOException e) {
            throw new ChatRoomException("读取数据出现错误", e);
        }
        return clientSendData;
    }

    public ServerSendData readFromServerSocket(SocketChannel channel) {
        ServerSendData serverSendData = new ServerSendData();
        try {
            int size = dataReadHelper.readInt(channel);
            while (size != -1) {
                Message message = dataReadHelper.readMessage(channel, size);
                serverSendData.add(message);
                size = dataReadHelper.readInt(channel);
            }
            return serverSendData;
        } catch (IOException e) {
            throw new ChatRoomException("读取数据出现错误", e);
        }
    }
}
