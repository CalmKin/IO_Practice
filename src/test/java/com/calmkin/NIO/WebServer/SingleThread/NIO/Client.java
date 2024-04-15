package com.calmkin.NIO.WebServer.SingleThread.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        // 客户端绑定socket
        socketChannel.connect(new InetSocketAddress("localhost", 8080));

        // 向socket写数据
        socketChannel.write(Charset.defaultCharset().encode("hello~"));


    }
}
