package com.study.kafka.demo1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 需要先提前手动创建好topic=topic_patr1
 */
public class MyProducer1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        Map<String, Object> configs = new HashMap<>();
        // 指定初始连接用到的broker地址
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.121.128:9092");
        // 指定key的序列化类
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        // 指定value的序列化类
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //每次请求的最大字节数，默认1M，这里设置10M，这个要根据实际业务情况来计算，且他的值要小于batch.size参数的值，否则一个batch
        //只放入一条数据，那么kafka的batch机制也就失效了。
        //如果改变此值，需要注意服务器也需要进行相应设置，因为服务器也有接收消息的大小限制。
        configs.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,10485760);

        //默认16kb，这里设置128kb，设置的太小的话，一条业务数据就占用一个batch，batch机制也就浪费了；
        //也不能设置的太大，设置的太大的话，可能导致瞬间消息大量积压到内存找中，导致oom；
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG,131072);//默认16kb;
        //默认0ms，设置为100ms，代表的含义是batch中即使数据没有满，配合batch.size一起使用,超过100ms的话，也要发送个broker
        //当我们发送的消息都比较小的时候，可以通过设置linger.ms来减少请求的次数，批次中累积更多的消息后再发送，提高了吞吐量，减少了IO请求。
        //如果设置的太大，则消息会被延迟更长的时间发送。
        configs.put(ProducerConfig.LINGER_MS_CONFIG,100);


        //默认32m,这里设置64m，生产者可发送发到缓冲区的总字节数，超过的话就block住 ProducerConfig.MAX_BLOCK_MS_CONFIG (默认60s)，
        //若还是没有缓冲区释放内存，就抛出异常；
        //当然如果使用压缩机制，当缓冲区内存不足时，就触发压缩机制，若还是不足，在阻塞60s后尝试获取缓存区的内存，若还是没获取到抛异常；
        configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG,67108864);
        configs.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,60 * 1000);



        //-1 : 消息发送出去，不等待任何反馈，设置的reties值也不生效；offset的值返回一直是-1；
        //1：只能保证leader分区已经成功写了消息，不保证follower分区同步到到数据，
        //all: 等待leader和follower分区都成功写入消息后返回，
       configs.put(ProducerConfig.ACKS_CONFIG, "all");

       //请求重试次数，可能导致同一个分区的消息不是按顺序发送的，可通过设置MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION=1从而保证消息的顺序性，但这样付出
        //的代价是牺牲性能，默认值是重试Integer.MAX_VALUE次;
        //建议通过delivery.timeout.ms来控制重试超时时间，不管设置重试几次，超过这个时间都不在重试，直接抛出异常
       configs.put(ProducerConfig.RETRIES_CONFIG, 1);
        // 默认120s，linger.ms+request.timeout.ms一定要小于这个值
        //调用send()返回后收到成功或失败的时间上限。这限制了消息在发送之前被延迟的总时间、
        // 等待broker确认的时间（如果可预期的话）以及允许可重试发送失败的时间。如果遇到不可恢复的错误、重试次数已用尽、消息被添加到过期批次中，则会发生异常
        configs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,120*1000);

       //当请求失败了下次重试的间隔时间，默认100ms,根据实际业务，当设置retries为非0时配合使用；
       configs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,100L);

        //可自定义分区规则，只要实现org.apache.kafka.clients.producer.Partitioner接口，在这已配置即可，
        //是否要重写，看自己的业务需求，一般不需要；
        configs.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class);

        //请求超时时间，默认30s没有收到响应则重试，若设置retries=0，则直接抛异常
        configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,30*1000);


       KafkaProducer<Integer, String> producer = new KafkaProducer<Integer, String>(configs);

        // 用于设置用户自定义的消息头字段
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("biz.name", "producer.demo".getBytes()));

        ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(
                "topic_patr1",null,null,
                9,
                "hello2 word.。。。。。。。。。。。。。。。",
                headers
        );

        // 消息的同步确认
        final Future<RecordMetadata> future = producer.send(record);
        RecordMetadata recordMetadata = future.get(30000, TimeUnit.SECONDS);
        final RecordMetadata metadata = future.get();
        System.out.println("消息的主题：" + metadata.topic());
        System.out.println("消息的分区号：" + metadata.partition());
        System.out.println("消息的偏移量：" + metadata.offset());

        // 消息的异步确认
       /* producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println("消息的主题：" + metadata.topic());
                    System.out.println("消息的分区号：" + metadata.partition());
                    System.out.println("消息的偏移量：" + metadata.offset());
                } else {
                    System.out.println("异常消息：" + exception.getMessage());
                }
            }
        });*/

        // 关闭生产者
        producer.close();
    }
}
