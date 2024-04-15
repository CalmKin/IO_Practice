package com.calmkin.Netty.simpleTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

public class TestSilce
{
    public static void main(String[] args) {
        // 分配10个字节的buf
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h'});

        // 切片,从下标0开始，分配10个byte
        ByteBuf slice1 = buf.slice(0, 4);

        ByteBuf slice2 = buf.slice(4, 4);

        System.out.println(slice1.toString(Charset.defaultCharset()));
        System.out.println(slice2.toString(Charset.defaultCharset()));


        // 验证切片
        slice1.setByte(0,'b');

        System.out.println(slice1.toString(Charset.defaultCharset()));
        System.out.println(buf.toString(Charset.defaultCharset()));

    }
}
