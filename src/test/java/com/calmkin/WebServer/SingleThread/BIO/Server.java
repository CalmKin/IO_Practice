package com.calmkin.WebServer.SingleThread.BIO;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.calmkin.util.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 创建socket
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 绑定端口
        ssc.bind(new InetSocketAddress(8080));

        // 存放连接集合
        List<SocketChannel> list = new ArrayList<>();

        // 不断监听端口
        while (true)
        {
            // 调用accept 建立与客户端连接
            // socketChannel 用来和客户端之间通信
            log.debug("connecting...");
            // 默认阻塞，如果没有连接过来，就会阻塞在这里
            SocketChannel clientChannel = ssc.accept();
            log.debug("connected");


            log.debug("before read");

            // 从channel里面读取数据到buffer
            // 阻塞读取，如果客户端没有发送数据，就会阻塞在这里
            clientChannel.read(buffer);
            // 切换为读模式
            buffer.flip();

            // 从缓冲区里面读取数据
            debugRead(buffer);
            // 清空缓冲区
            buffer.clear();

            log.debug("after read");

        }

    }
}
