package com.study.kafka.demo4;

import org.apache.kafka.common.utils.KafkaThread;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟kafka的消息发送
 */
public class Test {

   //消息收集器，类似于kafka的RecordAccumulator类
    private static List<String> list = new ArrayList<>();

    public static void main(String[] args) {

        //守护线程，专门处理收集器里的消息
        Thread ioThread = new KafkaThread("test", new DemoThread(list), true);
        ioThread.start();
        //模拟发送消息
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        //模拟另外一个线程发送消息
        try {
            Thread.sleep(100);
        }catch (Exception e){

        }
        list.add("5");
        list.add("6");

        //模拟另外一个线程消费消息
        try {
            Thread.sleep(100);
        }catch (Exception e){

        }

        list.remove("1");
        list.remove("2");
        list.remove("3");
        list.remove("4");
        //模拟另外一个线程发送消息
        try {
            Thread.sleep(100);
        }catch (Exception e){

        }
        list.add("1");

        //保住主线程
        try {
            Thread.sleep(500000);
        }catch (Exception e){

        }
        System.out.println("------------");
    }

}
