package com.study.kafka.demo4;

import java.util.List;

public class DemoThread implements  Runnable {


    private List<String> list ;

    public  DemoThread(){

    }

    public  DemoThread(List<String> list){
            this.list =list;
    }


    private volatile  boolean running=true;

    @Override
    public void run() {
       while(running){
           System.out.println("测试守护线程。。。。。");

           if(null != list && !list.isEmpty()){
               System.out.println("list的长度为...."+list.size());
           }
           try {
               Thread.sleep(100);
           }catch (Exception e){

           }

       }

    }
}
