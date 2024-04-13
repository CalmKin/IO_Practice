package com.calmkin.simpleTest;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class ByteBufferDemo {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("test.txt").getChannel()) {

            // 分配10个字节
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // 从channel里面读取数据，放到缓冲区
            // len是读取到数据的字节数,如果读取到-1，说明没数据了
            while (true) {
                int len = channel.read(buffer);
                log.debug("读取到数据长度{}", len);

                if(len == -1) break;
                // 从buffer里面读取数据
                // 切换为读模式
                 buffer.flip();

                 // 当缓冲区还有数据
                 while (buffer.hasRemaining())
                 {
                     byte b = buffer.get();
                     log.debug("读取到字符:{}",(char)b);
                 }

                 // 清空缓冲区，否则没法读
                 buffer.clear();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
