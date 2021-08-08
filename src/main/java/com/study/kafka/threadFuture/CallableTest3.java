package com.study.kafka.threadFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
* @author：zhangfd
* @version 1.0.0
* @date  2018年12月28日 下午2:23:30
* @description
*/
public class CallableTest3 implements Callable<String> {

	@Override
	public String call() throws Exception {
		 Thread.sleep(10); 
		return Thread.currentThread().getName();
	}

	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		 
        ExecutorService s = Executors.newFixedThreadPool(2);

        Collection<CallableTest3> tasks = new ArrayList<CallableTest3>();
        tasks.add(new CallableTest3());
        tasks.add(new CallableTest3());
        tasks.add(new CallableTest3());
        
        List<Future<String>> fList = s.invokeAll(tasks);
        for (Future<String> future : fList) {
        	 System.out.println(future.get());
		}
		
        
        s.shutdownNow();
         
	}
	
	
}
