package com.study.kafka.kafkaNio.producer.demo1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class MyKafkaChannel {

    public static final String  CHAR_SET = "UTF-8";

    private  String id;

    private  SelectionKey key;
    private  SocketChannel socketChannel;

    private  String msg;

    public MyKafkaChannel() {
    }

    public MyKafkaChannel(String id, SelectionKey key) {
        this.id = id;
        this.key = key;
        socketChannel = (SocketChannel) key.channel();
    }

    public  void write(){
        try {

            //把写的内容转化为byteBuffer，待改进
            ByteBuffer bf = ByteBuffer.allocate(msg.getBytes().length);
            bf.clear();
            bf.put(msg.getBytes(CHAR_SET));
            bf.flip();

            //这里一定要判断，连接是否成功，因为设置的连接时非阻塞的，是异步返回连接标识的
            //kafka使用的selector.select方法实现，但是要调用 selector.wakeup方法防治selector.select阻塞
            while(!socketChannel.finishConnect()){
              System.out.println("还没建立连接，等一会");
            }
            //写事件前，需要注册读事件，这样服务端拿到写的数据处理后，客户端才能接受到服务端返回的数据
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
            
            socketChannel.write(bf);//写数据给服务端
            bf=null;


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void close(){
        if(null != socketChannel){
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean isOpen(){
        try {
            return socketChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
