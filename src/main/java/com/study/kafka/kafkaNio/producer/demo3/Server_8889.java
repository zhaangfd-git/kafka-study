package com.study.kafka.kafkaNio.producer.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server_8889 {
    private static int PORT = 8889;
    private static ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    private static ByteBuffer sendBuffer = ByteBuffer.allocate(256);


    public static void main(String args[]) throws Exception {
        Selector selector = Selector.open();
        // Open a listener on each port, and register each one
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress("localhost", PORT);
        ssc.bind(address);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Going to listen on " + PORT);
        while (selector.select() > 0) {
            Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
            while (selectionKeys.hasNext()) {
                SelectionKey key = selectionKeys.next();
                selectionKeys.remove();
                handleKeys(selector, key);
            }
        }


    }

    private static void handleKeys(Selector selector, SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel sscNew = (ServerSocketChannel) key.channel();
            SocketChannel sc = sscNew.accept();
            sc.configureBlocking(false);
            System.out.println(Thread.currentThread().getName()+"----isAcceptable");
            // Add the new connection to the selector
            sc.register(selector, SelectionKey.OP_READ);
            //key.interestOps(SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            System.out.println(Thread.currentThread().getName()+"----isReadable");
            String msg = new String();
            SocketChannel sc = (SocketChannel) key.channel();
            int code = 0;
            while ((code = sc.read(echoBuffer)) > 0) {
                byte b[] = new byte[echoBuffer.position()];
                echoBuffer.flip();
                echoBuffer.get(b);
                msg += new String(b, "UTF-8");
            }
            //client关闭时，收到可读事件，code = -1
            if (code == -1 || msg.toUpperCase().indexOf("BYE") > -1) {
                sc.close();
            } else {
                //code=0，消息读完或者echoBuffer空间不够时，部分消息内容下一次select后收到
                echoBuffer.clear();
            }
            System.out.println("msg: " + msg + " from: " + sc + "code:  " + code);
            //注册可写通知
            sc.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);


        } else if (key.isWritable()) {
            SocketChannel client = (SocketChannel) key.channel();
            //工作线程做的事，不影响主线程，等业务线程处理完后把结果返回到主线程，主线程再把结果推给服务端
            new Thread(()-> Server_8889.write(client)).start();
            //写就绪相对有一点特殊，一般来说，你不应该注册写事件。写操作的就绪条件为底层缓冲区有空闲空间，而写缓冲区绝大部分时间都是有空闲空间的，所以当你注册写事件后，写操作一直是就绪的，选择处理线程全占用整个CPU资源。所以，只有当你确实有数据要写时再注册写操作，并在写完以后马上取消注册。
            key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
        }
    }

    private synchronized   static void write(SocketChannel client)  {
        try {


            String sendTxt = Thread.currentThread().getName()+"Message from Server";
            sendBuffer.clear();
            sendBuffer.put(sendTxt.getBytes());
            sendBuffer.flip();
            int code = 0;


            while ((code=client.write(sendBuffer)) != 0) {
            }
            if (code == -1) { //不能直接关闭client，否则客户端会一直读取数据
                client.close();
            } else {
                sendBuffer.clear();
            }
            System.out.println(Thread.currentThread().getName()+"Send message to client ");
        }catch (Exception e){

            System.out.println(e);
        }

    }
}