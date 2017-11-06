package com.sun.xiaotian.chatroom;

import com.sun.xiaotian.chatroom.data.ChannelDataReader;
import com.sun.xiaotian.chatroom.data.ChannelDataWriter;
import com.sun.xiaotian.chatroom.data.ClientSendData;
import com.sun.xiaotian.chatroom.data.ServerSendData;
import com.sun.xiaotian.chatroom.exception.ChatRoomException;
import com.sun.xiaotian.chatroom.message.Message;
import com.sun.xiaotian.chatroom.storage.DataStorage;
import com.sun.xiaotian.chatroom.storage.MemoryDataStorage;
import com.sun.xiaotian.chatroom.util.JsonMessageParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends Thread {

    private int port = 0;

    private ExecutorService readThreads = Executors.newFixedThreadPool(10);
    private ExecutorService writeThreadsThreads = Executors.newFixedThreadPool(10);

    private ChannelDataReader channelDataReader = new ChannelDataReader();
    private ChannelDataWriter channelDataWriter = new ChannelDataWriter();

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private DataStorage dataStorage;
    private SendDataRecord sendDataRecord;

    public Server(int port) {
        this.port = port;
        dataStorage = new MemoryDataStorage();
        sendDataRecord = new SendDataRecord();
    }

    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            serverSocketChannel.configureBlocking(false);

            while (true) {
                SocketChannel acceptSocket = serverSocketChannel.accept();
                if (acceptSocket == null) {
                    continue;
                }
                ClientSendData clientSendData = channelDataReader.readFromClientSocket(acceptSocket);
                if (clientSendData.getClientType() == TypeInfo.CLIENT_READ) {
                    long clientId = clientSendData.getClientId();
                    int index = sendDataRecord.getIndex(clientId);
                    ServerSendData serverSendData = new ServerSendData();
                    while (dataStorage.messageCount() > 0 && index < dataStorage.messageCount()) {
                        serverSendData.add(dataStorage.getByIndex(index));
                        index++;
                    }
                    channelDataWriter.writeToServerSocket(acceptSocket, serverSendData);
                } else if (clientSendData.getClientType() == TypeInfo.CLIENT_WRITE) {
                    Message message = clientSendData.getMessage();
                    message.setAcceptTime(new Date());
                    dataStorage.add(message);
                } else {
                    throw new ChatRoomException("不支持的客户端类型");
                }
                acceptSocket.close();
            }
        } catch (ChatRoomException e) {
            logger.info(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    class ReadThread extends Thread {

        private SocketChannel channel;

        public ReadThread(SocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                ByteBuffer readBuffer = ByteBuffer.allocate(8);
                channel.read(readBuffer);
                while (readBuffer.position() < 8) {
                    channel.read(readBuffer);
                }

                int readLength = channel.read(readBuffer);
                while (readLength != -1) {
                    readLength = channel.read(readBuffer);
                }
                readBuffer.flip();
                Message message = JsonMessageParse.readFromJson(new String(readBuffer.array(), 0, readBuffer.limit()));
                message.setAcceptTime(new Date());
                dataStorage.add(message);
                logger.info(message.toString());
                readBuffer.clear();
            } catch (IOException e) {

            }
        }
    }

    class WriteThread extends Thread {

        private Channel channel;

        public WriteThread(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            super.run();
        }
    }

}
