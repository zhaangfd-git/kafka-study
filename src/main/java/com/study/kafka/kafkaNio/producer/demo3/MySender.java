package com.study.kafka.kafkaNio.producer.demo3;

import javax.validation.constraints.NotNull;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MySender implements Runnable{

    private ClientNetwork network;

    private Map<InetSocketAddress,List<String>> totalMsg;


    public MySender(@NotNull ClientNetwork network, @NotNull Map<InetSocketAddress,List<String>> totalMsg){

        this.network = network;

        this.totalMsg = totalMsg;


    }


    @Override
    public void run() {
         while(true){
             runOnce();
         }
    }

    public void runOnce(){

        if(null !=totalMsg && totalMsg.size()>0){

            for(Map.Entry<InetSocketAddress, List<String>> entry:totalMsg.entrySet()){
                InetSocketAddress inetSocketAddress =  entry.getKey();
                String id  = ThreadLocalRandom.current().nextInt(10)+"";
                network.connect(id,inetSocketAddress);
                List<String> msgs = entry.getValue();
                StringBuilder msgAppend = new StringBuilder(20);
                for (String msg :msgs){
                    msgAppend.append(msg+"&");
                }
                network.send(id,msgAppend.toString().substring(0,msgAppend.toString().length()-2));
            }

            totalMsg.clear();
        }
        network.poll(300);

    }



    public ClientNetwork getNetwork() {
        return network;
    }

    public void setNetwork(ClientNetwork network) {
        this.network = network;
    }


}
