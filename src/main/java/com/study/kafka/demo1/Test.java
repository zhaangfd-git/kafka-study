package com.study.kafka.demo1;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class Test {

    public static void main(String[] args) {
        //System.out.println(InetAddress.getCanonicalHostName());
       /* System.out.println(Math.abs("order_process".hashCode())%50);
        AtomicLong currentThread = new AtomicLong(-1L);
        System.out.println(currentThread.compareAndSet(-1L,2L));
        System.out.println(currentThread.get());

        int count = Integer.MAX_VALUE;
        char[] copy = new char[count];*/
        int drainIndex =0;
        int start = drainIndex = drainIndex % 5;



        do{

            drainIndex = (drainIndex + 1) % 5;
            System.out.println(drainIndex);
        }while(start != drainIndex);

    }
}
