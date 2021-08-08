package com.study.kafka.nio.demo1_nio.demo3_fileChannel;

import org.apache.kafka.common.record.FileRecords;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
* @author:zhangfd
* @version:1.0 
* @date:2017年12月7日 下午10:09:13
* @description:
*/
public class MySocketClient_FileChannel {

	public static final String  CHAR_SET = "UTF-8";
	
	private String ip;
	
	private int port;
	
	private SocketChannel socketChannel;
	
	private Selector sel;
	
	public MySocketClient_FileChannel() throws IOException {
		
	}
	
    public MySocketClient_FileChannel(String ip, int port) throws IOException {
    	this();
		this.ip = ip;
		this.port = port;

	}

    private void init() throws IOException{
    	// 创建socketChannel对象
    	socketChannel = SocketChannel.open();
    	sel = Selector.open();
    	//绑定网络
    	socketChannel.connect(new InetSocketAddress(ip,port));
    	socketChannel.configureBlocking(false);
    	//注册读事件,把socketChannel注册到选择器上
		SelectionKey selectionKey = socketChannel.register(sel, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
		selectionKey.attach("test......"+Thread.currentThread().getName());
		//另外启动一个线程等待服务端返回的结果,主线程可以其做其他事件
		new SocketClientReadThread(sel);


    }
    
    public synchronized void sendMessage(FileChannel fileChannel,int position, int count ) {
		try {
			init();//放在这里每次都创建一个网络连接
			fileChannel.transferTo(position,count,socketChannel);

		}catch (Exception e){
             System.out.println(e.getMessage());
		}

    }
    
    public static void main(String[] args) throws Exception {

    	MySocketClient_FileChannel client = new MySocketClient_FileChannel("localhost",8888);


		String filePath = "src\\main\\java\\com\\study\\kafka\\nio\\demo1_nio\\demo3_fileChannel\\testFile.txt";
		FileChannel fileChannel =  new FileInputStream(filePath).getChannel();
		long start = System.currentTimeMillis();
		Executor executors = Executors.newFixedThreadPool((int) (fileChannel.size()/25+1));
		for (int j =0; j <=fileChannel.size();j=j+26){
			int count = j;
			executors.execute(()->client.sendMessage(fileChannel,count,count+25));
		}
		long end = System.currentTimeMillis();
		System.out.println(end-start);


	}


	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
}
