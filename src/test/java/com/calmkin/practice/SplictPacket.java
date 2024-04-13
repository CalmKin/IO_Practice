package com.calmkin.practice;

import java.nio.ByteBuffer;

import static com.calmkin.util.ByteBufferUtil.debugAll;

public class SplictPacket {
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        //                     11            24
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);

        source.put("w are you?\nhaha!\n".getBytes());
        split(source);
    }

    public static void split(ByteBuffer source) {

        // 先切换到读模式
        source.flip();

        for (int i = 0; i < source.limit(); i++) {
            byte ch = source.get(i);
            // 遇到分隔符
            if ((char) ch == '\n') {
                // 计算需要写入的长度
                // \n的位置和position位置之差
                int len = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(len);

                // 从position位置开始，读len个字符
                for (int j = 0; j < len; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        // 由于半包问题，所以上一次写入可能没写完
        // 所以应该从下一个位置开始写
        // 所以不能用clear方法
        source.compact();
    }
}
