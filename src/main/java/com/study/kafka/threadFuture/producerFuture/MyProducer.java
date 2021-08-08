package com.study.kafka.threadFuture.producerFuture;

import org.apache.kafka.common.utils.KafkaThread;

import java.util.concurrent.Future;

public class MyProducer {


    private MyMetaDataFuture myMetaDataFuture;

    public Future<String> send(String msg){

        return  doSend(msg);
    }

    private Future doSend(String msg){
        MyRequestResult requestResult = new MyRequestResult();
        MyMetaDataFuture myMetaDataFuture = new MyMetaDataFuture(msg,requestResult);
        this.myMetaDataFuture = myMetaDataFuture ;
        //一定是守护线程才可，否则的话主线程先跑，就处于一直等待状态
        new KafkaThread("ioThreadName", new DemoSender(), true).start();
        return myMetaDataFuture;
    }

    private void doSomeThings(){
        System.out.println(Thread.currentThread().getName()+".....接受请求信息为："+myMetaDataFuture.getMsg());
        System.out.println(".....接受请求信息为："+myMetaDataFuture.getMsg());

        System.out.println(".....do otherThing ");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(".....do otherThing done");
        myMetaDataFuture.getResult().done();

    }


    class  DemoSender implements Runnable{

        @Override
        public void run() {
            doSomeThings();
        }
    }

}

