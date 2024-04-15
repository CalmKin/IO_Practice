package com.calmkin.NIO.simpleTest;

import com.calmkin.util.ByteBufferUtil;

import java.nio.ByteBuffer;

import static com.calmkin.util.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)0x61);

        debugAll(buffer);
        buffer.put(new byte[]{0x62,0x63,0x64});

        debugAll(buffer);

        // 如果直接读的话，读取到的值是0
        // System.out.println(buffer.get());

        // 切换到读模式
        buffer.flip();

         System.out.println(buffer.get());

         debugAll(buffer);

         // 把未读取的数据移动到开头
        buffer.compact();

        debugAll(buffer);

        buffer.put(new byte[]{0x65,0x66});

        debugAll(buffer);

    }
}
