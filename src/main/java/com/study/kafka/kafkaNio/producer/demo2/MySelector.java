package com.study.kafka.kafkaNio.producer.demo2;


import com.study.kafka.kafkaNio.producer.demo0.MySocketClient;
import com.study.kafka.kafkaNio.producer.demo0.SocketClientReadProcess;
import org.apache.kafka.common.network.KafkaChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 封装 java.nio.channels.Selector nioSelector类，实现接口MySelectable
 */
public class MySelector implements MySelectable, AutoCloseable{

    /**
     * 记录已建立网络链接的请求
     */
    private final Map<String, MyKafkaChannel> channels;

    /**
     * 记录关闭网络链接的请求
     */
    private final Map<String, MyKafkaChannel> closingChannels;

    private final List<String> connected;

    private final Set<SelectionKey> immediatelyConnectedKeys;
    /**
     * 引入的是java1.5的nioSelector，最终还是要它来完成网络操作，装饰者模式
     */
    private final Selector nioSelector;

    public MySelector() {

        try {
            this.nioSelector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.channels = new HashMap<>();

        immediatelyConnectedKeys = new HashSet<>();

        this.closingChannels = new HashMap<>();

        this.connected = new ArrayList<>();
    }


    /**
     *
     * @param id  每个新的网络链接建立一个新的自增长的id值标记
     * @param address 请求的地址
     * @param sendBufferSize 新的链接要发送的消息的大小
     * @param receiveBufferSize  新的链接接收的消息的大小
     * @throws IOException
     */
    @Override
    public void connect(String id, InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException {
        //1、检查
        if (this.channels.containsKey(id))
            throw new IllegalStateException("There is already a connection for id " + id);
        if (this.closingChannels.containsKey(id))
            throw new IllegalStateException("There is already a connection for id " + id + " that is still being closed");

        //2、创建socketChannel对象
        SocketChannel socketChannel = SocketChannel.open();
        SelectionKey key = null;

        try {
            //3、配置socketChannel对象参数
            socketChannel.configureBlocking(false);
            Socket socket = socketChannel.socket();
            //客户端开启，就是客户端在规定的时间(和操作系统有关)去刺探服务端是否存活，服务端开启也会在指定的时间去刺探客户端是否存活
            //socket.setKeepAlive(true);
            if (sendBufferSize != MySelectable.USE_DEFAULT_BUFFER_SIZE)
                socket.setSendBufferSize(sendBufferSize);
            if (receiveBufferSize != MySelectable.USE_DEFAULT_BUFFER_SIZE)
                socket.setReceiveBufferSize(receiveBufferSize);
            //是否开启Nagle算法，该算法是为了提高较慢的广域网传输效率，减小小分组的报文个数
           socket.setTcpNoDelay(true);

            //4、建立网络连接
            boolean connected = false;
            try {
                connected =  socketChannel.connect(address);
            } catch (UnresolvedAddressException e) {
                throw new IOException("Can't resolve address: " + address, e);
            }

            //5、把socketChannel注册到多路复用器上
             key = socketChannel.register(nioSelector, SelectionKey.OP_CONNECT);
             MyKafkaChannel myChannel = new MyKafkaChannel(id,key); //kafka在这里使用自定义的kafkaChannel ，并且初始化了PlaintextTransportLayer
             key.attach(myChannel);
             this.channels.put(id, myChannel);
             //6、如果已经建立连接，设置 多路复用器感兴趣的时间没包含它： SelectionKey key
            if (connected) {
                // OP_CONNECT won't trigger for immediately connected channels
                System.out.println("Immediately connected to node "+id);
                immediatelyConnectedKeys.add(key);
                //意味着这个选择键没有任何兴趣操作。 Selector.select()将忽略它。
                key.interestOps(0);
            }


        }catch (RuntimeException e){
            immediatelyConnectedKeys.remove(key);
            channels.remove(id);
            socketChannel.close();
            throw e;
        }


    }

    @Override
    public void send(String id, String msg) {
        MyKafkaChannel channel = channels.get(id);
        //说明已经有数据待写入了，给key注册写事件
        channel.setMsg(msg);
    }

    /**
     * demo1 中直接发消息，可能会出现错误，
     * @throws IOException
     */
    @Override
    public void poll(long timeOut ) throws IOException {

        if(timeOut <0){
            throw  new RuntimeException("超时时间不能小于0");
        }

        int numReadyKeys = select(timeOut);

        if (numReadyKeys > 0 || !immediatelyConnectedKeys.isEmpty()){
            Set<SelectionKey> readyKeys = this.nioSelector.selectedKeys();

            //处理所有初始化建立连接没有立即返回连接成功的情况
            pollSelectionKeys(readyKeys,false);
            readyKeys.clear();

            pollSelectionKeys(immediatelyConnectedKeys,true);
            immediatelyConnectedKeys.clear();
        }
        channels.clear();
        return;
    }


    void pollSelectionKeys(Set<SelectionKey> selectionKeys,boolean isImmediatelyConnected) throws IOException{

        for (SelectionKey key : selectionKeys) {
           MyKafkaChannel channel = (MyKafkaChannel) key.attachment();
           //判断key是否和服务端已经建立连接
           if(isImmediatelyConnected || key.isConnectable()){
               if(channel.finishConnect()){ //若已经建立连接，注册读事件
                   connected.add(channel.getId());
               }else{
                   continue;
               }
           }
           attemptRead(key,channel);
           attemptWrite(key,channel);
           //关闭通道
          /* channel.close();
           closingChannels.put(channel.getId(),channel);*/
        }


    }

    private void attemptRead(SelectionKey key, MyKafkaChannel channel) throws IOException{

        if(!key.isReadable()){
            return;
        }

        System.out.println("======可读数据");

        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //(ByteBuffer) sk.attachment();
        sc.read(buffer);
        buffer.flip();

        // 将字节转化为为UTF-8的字符串
        String receivedString = Charset.forName(MySocketClient.CHAR_SET).newDecoder().decode(buffer).toString();

        // 控制台打印出来
        System.out.println("接收到来自服务器 " + sc.socket().getRemoteSocketAddress() + "的信息: " + receivedString);

        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
    }


    private void attemptWrite(SelectionKey key, MyKafkaChannel channel) throws IOException{
        if(!key.isWritable()){
            return;
        }
        System.out.println("======可写数据");
        channel.write();

        //写完后不在注册写事件
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }




    private int select(long timeoutMs) throws IOException {
        if (timeoutMs < 0L)
            throw new IllegalArgumentException("timeout should be >= 0");

        if (timeoutMs == 0L)
            return this.nioSelector.selectNow();
        else
            return this.nioSelector.select(timeoutMs);
    }

    @Override
    public void wakeup() {

        this.nioSelector.wakeup();
    }

    /**
     * 1、关闭所有已经建立的连接
     * 2、关闭多路复用器
     */
    @Override
    public void close() {

        List<String> connections = new ArrayList<>(channels.keySet());
        try {
            for (String id : connections)
                close(id);
        } finally {
            try {
                nioSelector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close(String id) {
        MyKafkaChannel channel = channels.get(id);
        channel.close();


    }

    @Override
    public List<String> connected() {
        return null;
    }

    @Override
    public boolean isChannelReady(String id) {
        MyKafkaChannel channel = this.channels.get(id);
        return channel != null && channel.isOpen();

    }


}
