package com.study.kafka.threadFuture.consumerFuture;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class MyConsumerNetworkClient {


    // We do not need high throughput, so use a fair lock to try to avoid starvation
    private final ReentrantLock lock = new ReentrantLock(true);

    private final ConcurrentLinkedQueue<MyConsumerNetworkClient.RequestFutureCompletionHandler> pendingCompletion = new ConcurrentLinkedQueue<>();

    /**
     * 存放即将发送的消息
     */
    private final ConcurrentMap<String, RequestFutureCompletionHandler> unsent = new ConcurrentHashMap<>();

    /**
     *
     * @param msg  待发送的消息
     * @return  返回给调用者的是future
     */
    public  MyRequestFuture<MyClientResponse> send(String msg){

        RequestFutureCompletionHandler completionHandler = new RequestFutureCompletionHandler();
        unsent.putIfAbsent(msg,completionHandler);

        return  completionHandler.future;

    }


    public void poll (MyRequestFuture<?> future){
        //强制轮训，只有消息发送了才可以
        while(!future.isDone()){
            poll();
        }

    }


    private void poll(){
        //处理已经有返回值得消息
        firePendingCompletedRequests();
        //对待发送的消息加锁
        lock.lock();
        try {

            Iterator<Map.Entry<String, RequestFutureCompletionHandler>> iterator = unsent.entrySet().iterator();
            while(iterator.hasNext()){

                Map.Entry<String, RequestFutureCompletionHandler> next = iterator.next();
                System.out.println("开始处理请求..."+next.getKey()+"..发送网络请求....");

                Thread.sleep(100);
                System.out.println("模拟返回数据报文......."+Thread.currentThread().getName());
                MyClientResponse response = new MyClientResponse("test");
                //通知已经处理完请求数据
                next.getValue().onComplete(response);
            }
            unsent.clear();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        firePendingCompletedRequests();
    }


    private void firePendingCompletedRequests() {
        boolean completedRequestsFired = false;
        for (;;) {
            RequestFutureCompletionHandler completionHandler = pendingCompletion.poll();
            if (completionHandler == null)
                break;

            completionHandler.fireCompletion();
            completedRequestsFired = true;
        }

    }



    private class RequestFutureCompletionHandler  {
        private final MyRequestFuture<MyClientResponse> future;
        private MyClientResponse response;
        private RuntimeException e;

        private RequestFutureCompletionHandler() {
            this.future = new MyRequestFuture<>();
        }

        public void fireCompletion() {
            if (e != null) {
                future.raise(e);
            } else {
                future.complete(response);
            }
        }

        public void onFailure(RuntimeException e) {
            this.e = e;
            pendingCompletion.add(this);
        }

        public void onComplete(MyClientResponse response) {
            this.response = response;
            pendingCompletion.add(this);
        }
    }

}
