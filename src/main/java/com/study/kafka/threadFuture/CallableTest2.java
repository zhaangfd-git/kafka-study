package com.study.kafka.threadFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CallableTest2 implements Callable<Integer>{

	public Integer call() throws Exception {

		for(int i = 0; i < 5; i++){
			System.out.println(Thread.currentThread().getName()+"------->>"+i);
		}
		return 1000;
	}
	
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		FutureTask<Integer> futureTask1 = new FutureTask<Integer>(new CallableTest2());
	    FutureTask<Integer> futureTask2 = new FutureTask<Integer>(new CallableTest2());  
	    FutureTask<Integer> futureTask3 = new FutureTask<Integer>(new CallableTest2());  

	    //执行任务
	    executor.execute(futureTask1);    
        executor.execute(futureTask2);
        executor.execute(futureTask3);
	    
		System.out.println(futureTask1.get());
		System.out.println(futureTask2.get());
		System.out.println(futureTask3.get());
        
        executor.shutdownNow();
 
	} 

}
