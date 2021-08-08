package com.study.kafka.nio.demo1_nio.demo3_fileChannel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.RandomAccess;

public class MainClass {

    public static void main(String[] args) throws IOException {

      String filePath = "src\\main\\java\\com\\study\\kafka\\nio\\demo1_nio\\demo3_fileChannel\\testFile.txt";

       fileChannelWrite(filePath);
       //  testWrite();


       /* FileInputStream fis = new FileInputStream(filePath);
        byte[] buf = new byte[20]; //数据中转站 临时缓冲区
        int length = 0;
        while((length = fis.read(buf)) != -1){
            System.out.print(new String(buf, 0, length));
        }*/

    }

    private static void fileChannelWrite(String filePath) {
        FileChannel fileChannel = null;
        try {
            fileChannel = new RandomAccessFile(new File(filePath),"rw").getChannel();
            fileChannel.position(0);
            long start = System.currentTimeMillis();
            for (int i=0; i<8000;i++){

                fileChannel.position(fileChannel.size());
                String msg = "friend!..........."+String.format("%0" + 7 + "d", i)+"\n";
                fileChannel.write(ByteBuffer.wrap(msg.getBytes()));

            }
            fileChannel.force(true);
            long end = System.currentTimeMillis();
            System.out.println(end-start);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != fileChannel){
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void testWrite() throws IOException {
        String fileName =  "testFile.txt";
        File file = new File(fileName);
        OutputStreamWriter outputStreamWriter =  null;


        try{
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file,false),"utf-8");
            long start = System.currentTimeMillis();
            for (int i=0; i<10000;i++) {
                String msg = "friend!..........."+i+"\n";
                outputStreamWriter.write(msg);
                outputStreamWriter.flush();

            }
            long end = System.currentTimeMillis();
            System.out.println(end-start);


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != outputStreamWriter){
                outputStreamWriter.close();
            }

        }




    }
}
