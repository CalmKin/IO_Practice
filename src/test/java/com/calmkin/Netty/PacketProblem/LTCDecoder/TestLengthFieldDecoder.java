package com.calmkin.Netty.PacketProblem.LTCDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFieldDecoder {
    public static void main(String[] args) {

        EmbeddedChannel channel = new EmbeddedChannel(
                // 最大帧长度
                // 长度字段从第几个字符开始
                // 长度字段有几个字节
                // 长度字段之后跳过多少字节才是真正的内容
                // 把从头开始多少个字节剥离（1+4+4）
                new LengthFieldBasedFrameDecoder(1024, 1, 4, 4, 9),
                new LoggingHandler(LogLevel.DEBUG));

        // 4个字节的长度 + 实际内容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        writeContent(buf, "Hello World");
        writeContent(buf, "123456789");
        writeContent(buf, "abcdefght");
        channel.writeInbound(buf);
    }

    private static void writeContent(ByteBuf buf, String content) {
        buf.writeByte(8);
        buf.writeInt(content.getBytes().length);
        buf.writeInt(4);
        buf.writeBytes(content.getBytes());
    }
}
