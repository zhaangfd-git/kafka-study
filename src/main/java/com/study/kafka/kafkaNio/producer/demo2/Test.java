package com.study.kafka.kafkaNio.producer.demo2;

import java.nio.channels.SelectionKey;

public class Test {

    public static void main(String[] args) {
        System.out.println(SelectionKey.OP_WRITE);
        System.out.println(SelectionKey.OP_READ);
        System.out.println(SelectionKey.OP_CONNECT);
        System.out.println(SelectionKey.OP_ACCEPT);
    }
}
