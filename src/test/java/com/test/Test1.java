package com.test;

import org.apache.kafka.clients.producer.internals.ProducerBatch;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Test1 {

    public static void main(String[] args) {

       /* ConcurrentMap<TopicPartition, Deque<ProducerBatch>> batches = new ConcurrentHashMap<>();
        TopicPartition topicPartition1 = new TopicPartition("1",1);


        TopicPartition topicPartition2 = new TopicPartition("1",2);



        TopicPartition topicPartition3 = new TopicPartition("2",3);

        TopicPartition topicPartition4 = new TopicPartition("2",4);

        TopicPartition topicPartition5 = new TopicPartition("2",4);

        System.out.println(topicPartition4.equals(topicPartition5));
*/

        AtomicReference<FinalState> finalState = new AtomicReference<>(null);
        final FinalState tryFinalState = FinalState.SUCCEEDED ;
        AtomicReference<String> ss = new AtomicReference<>(null);


        System.out.println(finalState.compareAndSet(null,tryFinalState));
        System.out.println(ss.compareAndSet("000","999"));
        System.out.println(finalState.get());
        System.out.println(ss.get());

        System.out.println(null==Integer.valueOf("999".hashCode()));

        System.out.println(null==tryFinalState);



    }

    enum FinalState { ABORTED, FAILED, SUCCEEDED }

    public  void test1(){

        Deque<String> deque = new ArrayDeque<>();
       /* deque.add("1");
        deque.addFirst("2");*/
        deque.addLast("3");

        String  last = deque.peekLast();




    }
}

class TestBean{

     private  String name;

     private  String sex;

    public TestBean(String name, String sex) {
        this.name = name;
        this.sex = sex;
    }
}
