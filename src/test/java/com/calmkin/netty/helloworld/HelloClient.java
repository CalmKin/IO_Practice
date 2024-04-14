package com.calmkin.netty.helloworld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 启动类
        new Bootstrap()
                // 添加eventLoop，处理服务端发送的数据
                .group(new NioEventLoopGroup())
                // 选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 添加处理器
                .handler(
                        // 在建立连接后被调用
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 客户端对字符串进行编码（把字符串转为ByteBuf）
                                // 服务端对字符串进行解码
                                nioSocketChannel.pipeline().addLast(new StringEncoder());
                            }
                        }
                )
                // 连接到服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync() // 阻塞方法，直到连接建立
                .channel()// 代表的是客户端和服务端的连接对象SocketChannel
                // 向服务器发数据（无论收发数据，都会走handler）
                .writeAndFlush("hello, server");


    }
}
