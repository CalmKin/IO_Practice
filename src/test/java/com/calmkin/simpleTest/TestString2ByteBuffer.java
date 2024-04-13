package com.calmkin.simpleTest;

import com.calmkin.util.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.calmkin.util.ByteBufferUtil.debugAll;

public class TestString2ByteBuffer {
    public static void main(String[] args) {
        // 1. put方法(不会自动切换读模式)
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("abcd".getBytes());
        debugAll(buffer);

        // 2. Charset类encode方法编码
        // 自动切换读模式，大小和字符串一致
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("abcde");
        debugAll(buffer1);

        // 3.wrap方式
        // 自动切换读模式，大小和字符串一致
        ByteBuffer buffer2 = ByteBuffer.wrap("abcde".getBytes());
        debugAll(buffer2);

        // bytebuffer转换为String
        // charset类解码
        // 如果是第一种方式，还需要先切换到读模式
        // 否则会有问题，因为是从position往后开始找
        String string = StandardCharsets.UTF_8.decode(buffer2).toString();

    }
}
