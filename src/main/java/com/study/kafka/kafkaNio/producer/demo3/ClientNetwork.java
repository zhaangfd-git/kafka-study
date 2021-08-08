package com.study.kafka.kafkaNio.producer.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *  使用MySelector实现
 */
public class ClientNetwork {

    private  MySelector selector;

    public ClientNetwork(MySelector selector){
        this.selector = selector;
    }


    public  void  send(String id,String msg){
        selector.send(id,msg);
    }

    public  void  poll(long timeOut){
        try {
            selector.poll(timeOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String id ,InetSocketAddress address){
        try {
            //初始化链接，设置非阻塞，建立网络链接是异步的
            selector.connect(id, address, 4096, 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(InetSocketAddress address){
        connect(null, address);
    }

    public static void main(String[] args) throws IOException {

        MySelector selector = new MySelector();
        //1、模拟向两台服务器发送消息的情况,向两台服务器建立网络链接 初始化
        for (int i = 0; i < 2; i++) {
            InetSocketAddress address = new InetSocketAddress("localhost", 8888 + i);
            try {
                //初始化链接，设置非阻塞，建立网络链接是异步的
                selector.connect(String.valueOf(i), address, 4096, 4096);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        selector.wakeup();
        //2、绑定要发送的数据

        for (int i = 0; i < 2; i++) {
            String msg = "要发送的第" + i + "条数据";
            selector.send(String.valueOf(i), msg);
        }
        //3、实际发送数据
        while(true){
            selector.poll(300);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }
}
