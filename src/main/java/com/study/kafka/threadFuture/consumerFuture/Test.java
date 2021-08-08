package com.study.kafka.threadFuture.consumerFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * kafka的consumer实现的future机制，不支持多线程，
 * 要想实现消费者的多线程，有两种方案：
 * 1、多个线程，每个线程创建一个consumer Client；
 * 2、一个消费者线程，多个线程去处理从broker拿到的消息；
 */
public class Test {

    public static void main(String[] args) {
       /*  kafka的消费者这样干的
       MyConsumerNetworkClient client = new MyConsumerNetworkClient();
        Executor ex = Executors.newFixedThreadPool(10);
        for(int i=0;i<10;i++){
            int finalI = i;
            ex.execute(()->send(client,finalI));
        }*/
        Executor ex = Executors.newFixedThreadPool(10);
        for(int i=0;i<10;i++){
            int finalI = i;
            ex.execute(()->send(null,finalI));
        }


    }

    private static void send(MyConsumerNetworkClient client,int i) {
        if(null == client){
            client = new MyConsumerNetworkClient();
        }

        MyRequestFuture<MyClientResponse> future = client.send("我想测试future"+i);

        client.poll(future);

        if(future.isDone()){
            System.out.println("返回结果了： "+future.value().getName()+Thread.currentThread().getName());
        }else {
            System.out.println("玩坏了.....");
        }
    }
}
