package com.calmkin.WebServer.SingleThread.NIO;

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

        // 切换成非阻塞模式
        // 影响的是调用accept方法的这一步
        ssc.configureBlocking(false);

        // 绑定端口
        ssc.bind(new InetSocketAddress(8080));

        // 存放连接集合
        List<SocketChannel> list = new ArrayList<>();

        // 不断监听端口
        while (true)
        {
            // 调用accept 建立与客户端连接
            // socketChannel 用来和客户端之间通信
//            log.debug("connecting...");



            // 非阻塞，如果没有连接过来
            // 返回的是null
            SocketChannel clientChannel = ssc.accept();

            // 如果有连接
            if(clientChannel != null)
            {
                // 将客户端socket设置为非阻塞
                // 影响的是read这一步
                clientChannel.configureBlocking(false);
                list.add(clientChannel);
                log.debug("connected");
            }

            for (SocketChannel channel : list) {
//                log.debug("before read");

                // 从channel里面读取数据到buffer
                // 非阻塞，如果没有读到数据，会返回0
                int read = channel.read(buffer);

                // 如果没有读到数据，不做任何处理
                if(read == 0 ) continue;

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
}
