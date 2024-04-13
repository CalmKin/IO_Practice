package com.calmkin.WebServer.SingleThread.Selector.WriteData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        // 客户端绑定socket
        sc.connect(new InetSocketAddress("localhost", 8080));

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        while(true)
        {
            int read = sc.read(buffer);
            System.out.println(read);
        }
    }
}
