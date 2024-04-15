package com.calmkin.NIO.simpleTest;


import com.calmkin.util.ByteBufferUtil;

import java.nio.ByteBuffer;

import static com.calmkin.util.ByteBufferUtil.debugAll;

/**
 * @author CalmKin
 * @description NIO的ByteBuffer不同方式读取内容
 * @version 1.0
 * @date 2024/4/7 11:22
 */
public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        // 切换到读模式
        buffer.flip();

        // rewind方式
        // 获取byte数组的长度，然后一次性读取那么多内容
        buffer.get(new byte[4]);
        debugAll(buffer);
        // rewind从头开始读，position变成0
        buffer.rewind();
        System.out.println((char)buffer.get());


        // mark & reset方式
        System.out.println((char)buffer.get());
        // 记录一下位置，下一次重置回到这个位置
        buffer.mark();
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());

        // 索引回到2，打印c
        buffer.reset();
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());


        // get(i) 读取指定位置的内容
        // 但是不会改变postion位置
        System.out.println((char)buffer.get(3));
        debugAll(buffer);

    }
}
