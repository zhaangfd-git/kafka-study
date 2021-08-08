package com.study.kafka.threadFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableTest1 implements Callable<Integer>{

	public Integer call() throws Exception {

		for(int i = 0; i < 5; i++){
			System.out.println(Thread.currentThread().getName()+"------->>"+i);
		}
		return 1000;
	}
	
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		//1、实际上s是ThreadPollExecutor的对象，
		ExecutorService s = Executors.newFixedThreadPool(2);
		
		//2、s.submit实际上调用的是AbstractExecutorService里的submit方法
		Future<Integer> f = s.submit(new CallableTest1());
		Future<Integer> f1 = s.submit(new CallableTest1());
		Future<Integer> f2 = s.submit(new CallableTest1());
		//3、获取执行结果
		System.out.println(f.get());
		System.out.println(f1.get());
		System.out.println(f2.get());
		//4、关闭线程，
		s.shutdownNow();
 
	} 

}
