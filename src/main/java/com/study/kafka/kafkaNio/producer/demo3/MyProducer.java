package com.study.kafka.kafkaNio.producer.demo3;

import org.apache.kafka.common.utils.KafkaThread;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProducer {

    //使用这个集合模拟kafka的消息收集器Accumulator
    private Map<InetSocketAddress, List<String>> totalMsg;

    public MyProducer(){
        totalMsg = new HashMap<>();
        totalMsg.put(new InetSocketAddress("localhost", 8888),null);
        totalMsg.put(new InetSocketAddress("localhost", 8889),null);
        MySelector selector = new  MySelector ();
        ClientNetwork network = new  ClientNetwork (selector);
        MySender sender = new MySender(network,totalMsg);
        KafkaThread ioThread = new KafkaThread("test", sender, true);
        ioThread.start();
    }

    //模拟消息的发送
    public void addMsg(InetSocketAddress inetSocketAddress,String msg){

        List<String> msgs = totalMsg.get(inetSocketAddress);
        if (null == msgs || msgs.size()<1){
            totalMsg.put(inetSocketAddress,new ArrayList<>());
            msgs = totalMsg.get(inetSocketAddress);
        }
        msgs.add(msg);

    }


    public static void main(String[] args) throws InterruptedException {

        MyProducer test = new MyProducer();
        //向 new InetSocketAddress("localhost", 8888) 只会发一条消息：把两个合并起来发送
        test.addMsg(new InetSocketAddress("localhost", 8888),"要发送的第0条数据");
        test.addMsg(new InetSocketAddress("localhost", 8889),"要发送的第0条数据");
        test.addMsg(new InetSocketAddress("localhost", 8888),"要发送的第000条数据");

        Thread.sleep(3000);
        test.addMsg(new InetSocketAddress("localhost", 8888),"要发送的第1条数据");
        test.addMsg(new InetSocketAddress("localhost", 8889),"要发送的第1条数据");

        Thread.sleep(3000);
        test.addMsg(new InetSocketAddress("localhost", 8888),"要发送的第2条数据");
        test.addMsg(new InetSocketAddress("localhost", 8889),"要发送的第2条数据");
        Thread.sleep(30000);
    }

    public Map<InetSocketAddress, List<String>> getTotalMsg() {
        return totalMsg;
    }

    public void setTotalMsg(Map<InetSocketAddress, List<String>> totalMsg) {
        this.totalMsg = totalMsg;
    }
}
