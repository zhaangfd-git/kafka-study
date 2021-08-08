package com.study.kafka.kafkaNio.producer.demo0;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
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
public class MySocketClient {

	public static final String  CHAR_SET = "UTF-8";
	
	private String ip;
	
	private int port;
	
	private SocketChannel socketChannel;
	
	private final Selector sel;
	
	public MySocketClient() throws IOException {
		sel = Selector.open();
	}
	
    public MySocketClient(String ip, int port) throws IOException {
    	this();
		this.ip = ip;
		this.port = port;

	}

    private void init() throws IOException{
    	// 创建socketChannel对象
    	socketChannel = SocketChannel.open();
		/*Socket socket = socketChannel.socket();
		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);*/
    	//绑定网络
		boolean connect = socketChannel.connect(new InetSocketAddress(ip, port));
		socketChannel.configureBlocking(false);
    	//注册读事件,把socketChannel注册到选择器上
		SelectionKey selectionKey = socketChannel.register(sel, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
		selectionKey.attach("test......"+Thread.currentThread().getName());



    }
    
    public synchronized void sendMessage(String message,SocketChannel socketChannel) {
		try {

			ByteBuffer bf = ByteBuffer.allocate(message.getBytes().length);
			bf.clear();
			bf.put(message.getBytes(CHAR_SET));
			bf.flip();
			socketChannel.write(bf);//写数据给服务端
			bf=null;


		}catch (Exception e){
             System.out.println(e.getMessage());
		}

    }
    
    public static void main(String[] args) throws Exception {
    	
    	MySocketClient client = new MySocketClient("localhost",8888);
		Executor executors = Executors.newFixedThreadPool(10);
		for (int i=0; i <10; i++){
			client.init();//放在这里每次都创建一个网络连接
			executors.execute(() -> client.sendMessage(Thread.currentThread().getName()+": client:-->>>> 我是好人！",client.socketChannel));
		}
		new SocketClientReadProcess(client.sel).run();

	}

}
