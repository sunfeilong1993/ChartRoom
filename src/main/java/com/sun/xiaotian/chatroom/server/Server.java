package com.sun.xiaotian.chatroom.server;

import com.sun.xiaotian.chatroom.SendDataRecord;
import com.sun.xiaotian.chatroom.TypeInfo;
import com.sun.xiaotian.chatroom.data.ChannelDataReader;
import com.sun.xiaotian.chatroom.data.ChannelDataWriter;
import com.sun.xiaotian.chatroom.data.ClientSendData;
import com.sun.xiaotian.chatroom.data.ServerSendData;
import com.sun.xiaotian.chatroom.exception.ChatRoomException;
import com.sun.xiaotian.chatroom.message.Message;
import com.sun.xiaotian.chatroom.storage.DataStorage;
import com.sun.xiaotian.chatroom.storage.MemoryDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
                while (!acceptSocket.finishConnect()) {
                    TimeUnit.MICROSECONDS.sleep(100);
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
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
