package com.study.kafka.nio.demo1_nio.demo1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author:zhangfd
 * @version:1.0
 * @date:2017年12月7日 下午11:22:31
 * @description:
 */
public class SocketClientReadProcess {

    private Selector selector;


    public SocketClientReadProcess(Selector sel) {
        this.selector = sel;
    }

    public void run() {

        try {

            //主要选择器中有可读事件存在
            while ( selector.select()>0) {

                    //获取所有可读事件集合
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        SelectionKey key = selectionKeys.next();
                        System.out.println("selectionKey attachment..."+key.attachment());
                        selectionKeys.remove();//把事件从集合中清楚，避免下次重复处理
                        handleKeys(key);
                    }


            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void handleKeys(SelectionKey sk) throws IOException {
        // 如果该SelectionKey对应的Channel中有可读的数据
        if (sk.isReadable()) {
            // 使用NIO读取Channel中的数据
            SocketChannel sc = (SocketChannel) sk.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //(ByteBuffer) sk.attachment();
            sc.read(buffer);
            buffer.flip();

            // 将字节转化为为UTF-8的字符串
            String receivedString = Charset.forName(MySocketClient.CHAR_SET).newDecoder().decode(buffer).toString();

            // 控制台打印出来
            System.out.println("接收到来自服务器 " + sc.socket().getRemoteSocketAddress() + "的信息: " + receivedString);

            // 为下一次读取作准备
            //sk.interestOps(SelectionKey.OP_READ);
        }else{
            System.out.println("异常事件不做处理......");
        }
    }


}
