package com.sun.xiaotian.chatroom;

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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;


public class Server extends Thread {

    private int port = 0;

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
            ByteBuffer readBuffer = ByteBuffer.allocate(4);

            while (true) {
                SocketChannel acceptSocket = serverSocketChannel.accept();
                if (acceptSocket != null) {
                    //接收客户端消息
                    readBuffer = ByteBuffer.allocate(4);
                    acceptSocket.read(readBuffer);
                    while (readBuffer.position() < 4) {
                        acceptSocket.read(readBuffer);
                    }
                    readBuffer.flip();
                    int clientType = readBuffer.getInt();
                    readBuffer.clear();

                    if (clientType == TypeInfo.CLIENT_READ) {
                        //
                        readBuffer = ByteBuffer.allocate(8);
                        acceptSocket.read(readBuffer);
                        while (readBuffer.position() < 8) {
                            acceptSocket.read(readBuffer);
                        }
                        readBuffer.flip();
                        long clientId = readBuffer.getLong();

                        //给客户端发送消息
                        int index = sendDataRecord.getIndex(clientId);
                        if (dataStorage.hasMessage() && index <= dataStorage.messageCount()) {
                            Message tempMessage = null;
                            while (index < dataStorage.messageCount()) {
                                tempMessage = dataStorage.getByIndex(index);
                                String tempMessageJsonStr = JsonMessageParse.writeToJson(tempMessage);
                                int strBytes = tempMessageJsonStr.getBytes().length;
                                ByteBuffer writeBuffer = ByteBuffer.allocate(4 + strBytes);
                                writeBuffer.putInt(strBytes);
                                writeBuffer.put(tempMessageJsonStr.getBytes());
                                writeBuffer.flip();
                                acceptSocket.write(writeBuffer);
                                writeBuffer.clear();
                                index++;
                            }
                            sendDataRecord.add(clientId, index, tempMessage.getAcceptTime());
                        }
                    } else if (clientType == TypeInfo.CLIENT_WRITE) {
                        readBuffer = ByteBuffer.allocate(1024);
                        int readLength = acceptSocket.read(readBuffer);
                        while (readLength != -1) {
                            readLength = acceptSocket.read(readBuffer);
                        }
                        readBuffer.flip();
                        Message message = JsonMessageParse.readFromJson(new String(readBuffer.array(), 0, readBuffer.limit()));
                        message.setAcceptTime(new Date());
                        dataStorage.add(message);
                        logger.info(message.toString());
                        readBuffer.clear();
                    } else {
                        throw new ChatRoomException("不能解析的客户端类型");
                    }
                    acceptSocket.close();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
