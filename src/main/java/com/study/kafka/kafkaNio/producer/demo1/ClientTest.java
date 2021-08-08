package com.study.kafka.kafkaNio.producer.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *  使用MySelector实现
 */
public class ClientTest {

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
        //2、绑定要发送的数据
        for (int i = 0; i < 2; i++) {
            String msg = "要发送的第" + i + "条数据";
            selector.send(String.valueOf(i), msg);
        }
        //3、实际发送数据
        selector.poll();


    }
}
