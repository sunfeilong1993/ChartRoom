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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 聊天服务器
 */
public class ChatServer extends Thread {

    /**
     * 服务器端口，默认是 8383
     */
    private int port = 8383;

    private final ChannelDataReader channelDataReader = new ChannelDataReader();
    private final ChannelDataWriter channelDataWriter = new ChannelDataWriter();

    private final static Logger logger = LogManager.getLogger(ChatServer.class);

    private DataStorage dataStorage;
    private SendDataRecord sendDataRecord;

    public ChatServer(int port) {
        if (port > 0) {
            this.port = port;
        }
        dataStorage = new MemoryDataStorage();
        sendDataRecord = new SendDataRecord();
    }

    public void run() {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(port));
            server.configureBlocking(false);

            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);

            Selector readSelector = Selector.open();

            Selector sendSelector = Selector.open();

            new Thread(() -> {
                while (true) {
                    try {
                        selector.select();
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey nextKey = keyIterator.next();
                            keyIterator.remove();
                            if (nextKey.isAcceptable()) {
                                ServerSocketChannel channel = (ServerSocketChannel) nextKey.channel();
                                SocketChannel client = channel.accept();
                                client.configureBlocking(false);
                                client.register(selector, SelectionKey.OP_READ, true);
                            }
                            if (nextKey.isReadable()) {
                                boolean first = (boolean) nextKey.attachment();
                                if (!first) {
                                    break;
                                }
                                SocketChannel client = (SocketChannel) nextKey.channel();
                                //读取信息判断客户端类型
                                ClientSendData clientSendData = channelDataReader.readFromClientSocket(client);
                                if (clientSendData.getClientType() == TypeInfo.CLIENT_ACCEPT) {
                                    //从服务器接收信息
                                    client.register(readSelector, SelectionKey.OP_WRITE, clientSendData);
                                } else if (clientSendData.getClientType() == TypeInfo.CLIENT_SEND) {
                                    //向服务器发送信息
                                    client.register(sendSelector, SelectionKey.OP_READ, clientSendData);
                                } else {
                                    throw new ChatRoomException("不支持的客户端类型");
                                }
                                nextKey.attach(false);
                            }
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    try {
                        readSelector.select(1000);
                        Set<SelectionKey> selectionKeys = readSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey nextKey = keyIterator.next();
                            keyIterator.remove();
                            if (nextKey.isWritable()) {
                                SocketChannel accept_client = (SocketChannel) nextKey.channel();
                                ClientSendData clientSendData = (ClientSendData) nextKey.attachment();
                                int index = sendDataRecord.getIndex(clientSendData.getClientId());

                                ServerSendData serverSendData = new ServerSendData();
                                while (dataStorage.messageCount() > 0 && index < dataStorage.messageCount()) {
                                    serverSendData.add(dataStorage.getByIndex(index));
                                    index++;
                                }
                                if(serverSendData.getMessages().size() == 0){
                                    break;
                                }
                                sendDataRecord.addOrUpdate(clientSendData.getClientId(), index - 1, dataStorage.getByIndex(index - 1).getAcceptTime());
                                channelDataWriter.writeToServerSocket(accept_client, serverSendData);
                            }
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    try {
                        sendSelector.select(1000);
                        Set<SelectionKey> selectionKeys = sendSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey next = keyIterator.next();
                            keyIterator.remove();
                            if (next.isReadable()) {
                                ClientSendData clientSendData = (ClientSendData) next.attachment();
                                if (clientSendData == null) {
                                    SocketChannel client = ((SocketChannel) next.channel());
                                    clientSendData = channelDataReader.readFromClientSocket(client);
                                }
                                if (clientSendData == ClientSendData.NULL) {
                                    break;
                                }
                                Message message = clientSendData.getMessage();
                                message.setAcceptTime(new Date());
                                dataStorage.add(message);
                                next.attach(null);
                            }
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }).start();

//            while (true) {
//                SocketChannel acceptSocket = server.accept();
//                if (acceptSocket == null) {
//                    continue;
//                }
//                while (!acceptSocket.finishConnect()) {
//                    TimeUnit.MICROSECONDS.sleep(100);
//                }
//                ClientSendData clientSendData = channelDataReader.readFromClientSocket(acceptSocket);
//                if (clientSendData.getClientType() == TypeInfo.CLIENT_ACCEPT) {
//                    long clientId = clientSendData.getClientId();
//                    int index = sendDataRecord.getIndex(clientId);
//                    ServerSendData serverSendData = new ServerSendData();
//                    while (dataStorage.messageCount() > 0 && index < dataStorage.messageCount()) {
//                        serverSendData.add(dataStorage.getByIndex(index));
//                        index++;
//                    }
//                    channelDataWriter.writeToServerSocket(acceptSocket, serverSendData);
//                } else if (clientSendData.getClientType() == TypeInfo.CLIENT_SEND) {
//                    Message message = clientSendData.getMessage();
//                    message.setAcceptTime(new Date());
//                    dataStorage.add(message);
//                } else {
//                    throw new ChatRoomException("不支持的客户端类型");
//                }
//                acceptSocket.close();
//            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
