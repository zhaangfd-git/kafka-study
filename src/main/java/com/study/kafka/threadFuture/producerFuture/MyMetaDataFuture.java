package com.study.kafka.threadFuture.producerFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyMetaDataFuture  implements Future<String> {

    private  String msg;

    private final   MyRequestResult result;

    public  MyMetaDataFuture(String msg,MyRequestResult result){
        this.result = result;
        this.msg = msg;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result.completed();
    }

    @Override
    public String get() throws InterruptedException, ExecutionException {
        result.await();
        return "test get";
    }

    @Override
    public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        result.await(timeout,unit);
        return "test get time !";
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MyRequestResult getResult() {
        return result;
    }


}
