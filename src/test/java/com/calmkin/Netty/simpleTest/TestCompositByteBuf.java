package com.calmkin.Netty.simpleTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import java.nio.charset.Charset;

public class TestCompositByteBuf {
    public static void main(String[] args) {

        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{'a', 'b', 'c', 'd'});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{'e', 'f', 'g', 'h'});

        CompositeByteBuf buf3 = ByteBufAllocator.DEFAULT.compositeBuffer();

        // true 表示增加新的 ByteBuf 自动递增 write index, 否则 write index 会始终为 0
        buf3.addComponents(true, buf1, buf2);

        // 增加引用计数，防止buf3被回收时影响
        buf1.retain();
        buf2.retain();

        System.out.println(buf3.toString(Charset.defaultCharset()));
    }
}
