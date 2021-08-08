package com.study.kafka.threadFuture.consumerFuture;

public class MyClientResponse {

   private String name;

    public MyClientResponse() {
    }

    public MyClientResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
