package com.study.kafka.kafkaNio.producer.demo3;

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

            ByteBuffer bf = ByteBuffer.allocate(msg.getBytes().length);
            bf.clear();
            bf.put(msg.getBytes(CHAR_SET));
            bf.flip();

            socketChannel.write(bf);//写数据给服务端
            bf=null;


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public boolean finishConnect() throws IOException {
        boolean connected = socketChannel.finishConnect();
        if (connected)
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        return connected;
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
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
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
