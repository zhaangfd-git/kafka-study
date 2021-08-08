package com.study.kafka.nio.demo4kafka;

import org.apache.kafka.common.network.KafkaChannel;
import org.apache.kafka.common.network.Selectable;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Client {


    public static void main(String[] args) throws Exception {



       java.nio.channels.Selector nioSelector = java.nio.channels.Selector.open();


        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Socket socket = socketChannel.socket();
        socket.setKeepAlive(true); //设置会话保持长链接

       /* if (sendBufferSize != Selectable.USE_DEFAULT_BUFFER_SIZE)
            socket.setSendBufferSize(sendBufferSize);
        if (receiveBufferSize != Selectable.USE_DEFAULT_BUFFER_SIZE)
            socket.setReceiveBufferSize(receiveBufferSize);*/

        socket.setTcpNoDelay(true);
        SocketAddress address = null;
        boolean connected = socketChannel.connect(address);
        SelectionKey key =   socketChannel.register(nioSelector, SelectionKey.OP_CONNECT);
        KafkaChannel channel = null;//定义这个kafkachannle干啥？里面封装 PlaintextTransportLayer
        key.attach(channel);
        if (connected) {
            // OP_CONNECT won't trigger for immediately connected channels
            key.interestOps(0); //如果已经链接上，重置key感兴趣的事件为0，标识不监听任何事件
        }
    }

}
