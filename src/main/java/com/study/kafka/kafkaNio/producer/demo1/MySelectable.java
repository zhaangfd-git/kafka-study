package com.study.kafka.kafkaNio.producer.demo1;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 一个异步的接口，实现多通道的网络操作
 */
public interface MySelectable {

    int USE_DEFAULT_BUFFER_SIZE = -1;

    void connect(String id, InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException;


    void send(String id,String msg);

    void poll() throws IOException;
    /**
     * Wakeup this selector if it is blocked on I/O
     */
    void wakeup();

    /**
     * Close this selector
     */
    void close();

    /**
     * Close the connection identified by the given id
     */
    void close(String id);



    /**
     * The list of connections that completed their connection on the last {@link #poll(long) poll()}
     * call.
     */
    List<String> connected();

    /**
     * returns true  if a channel is ready
     * @param id The id for the connection
     */
    boolean isChannelReady(String id);
}
