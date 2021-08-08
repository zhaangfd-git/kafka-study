package com.study.kafka.nio.demo1_nio.demo3_fileChannel;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
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
public class MySocketClient_FileInputStream {

	public static final String  CHAR_SET = "UTF-8";

	private String ip;

	private int port;

	private SocketChannel socketChannel;

	private Selector sel;

	public MySocketClient_FileInputStream() throws IOException {

	}

    public MySocketClient_FileInputStream(String ip, int port) throws IOException {
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
    
    public synchronized void sendMessage(String message) {
		try {
			init();//放在这里每次都创建一个网络连接
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
    	
    	MySocketClient_FileInputStream client = new MySocketClient_FileInputStream("localhost",8888);
		Executor executors = Executors.newFixedThreadPool(1);
		for (int i=0; i <1; i++){
			String filePath = "src\\main\\java\\com\\study\\kafka\\nio\\demo1_nio\\demo3_fileChannel\\testFile.txt";
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buf = new byte[26]; //数据中转站 临时缓冲区
			int length = 0;
			long start = System.currentTimeMillis();
			while((length = fis.read(buf)) != -1){
				String msg = new String(buf, 0, length);
				executors.execute(() -> client.sendMessage(msg));
			}
			long end = System.currentTimeMillis();
            System.out.println(end-start);
		}

	}

}
