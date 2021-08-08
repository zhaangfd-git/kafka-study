package com.study.kafka.threadFuture.producerFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MyProducer producer = new MyProducer();
        Future future = producer.send("测试");
        System.out.println("我继续干其他事");
        Thread.sleep(500);
        System.out.println("睡醒了，继续干我自己的事......");
        System.out.println("获取主线程刚才发起的请求......." + future.get());
    }
}
