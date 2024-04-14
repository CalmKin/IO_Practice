package com.calmkin.netty.simpleTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 启动类
        ChannelFuture channelFuture = new Bootstrap()
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
                .connect(new InetSocketAddress("localhost", 8080));


        // 方式一：阻塞方法，直到连接建立
        channelFuture.sync();
        Channel channel = channelFuture.channel();// 代表的是客户端和服务端的连接对象SocketChannel
        channel.writeAndFlush("123456");


        // 方式二：添加回调钩子，等连接建立好之后，执行后续操作
        channelFuture.addListener(new ChannelFutureListener() {
            // nio线程建立好连接之后，会调用operationComplete方法
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();// 代表的是客户端和服务端的连接对象SocketChannel
                channel.writeAndFlush("123456");
            }
        });


        // 向服务器发数据（无论收发数据，都会走handler）
        System.out.println(channel);
        System.out.println("");

    }
}
