package com.calmkin.Netty.simpleTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        // 启动类
        ChannelFuture channelFuture = new Bootstrap()
                // 添加eventLoop，处理服务端发送的数据
                .group(eventLoopGroup)
                // 选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 添加处理器
                .handler(
                        // 在建立连接后被调用
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 添加输出日志的处理器
                                nioSocketChannel.pipeline().addLast(new LoggingHandler());
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


        // 方式二：添加回调钩子，等连接建立好之后，执行后续操作
//        channelFuture.addListener(new ChannelFutureListener() {
//            // nio线程建立好连接之后，会调用operationComplete方法
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                Channel channel = channelFuture.channel();// 代表的是客户端和服务端的连接对象SocketChannel
//                channel.writeAndFlush("123456");
//            }
//        });


        // 需求：控制台接收用户输入，然后发送给服务端，当不想发送的时候，输入一个Q，断开连接
        new Thread(
            () -> {
                while(true)
                {
                    Scanner scanner = new Scanner(System.in);
                    String line = scanner.nextLine();

                    if("q".equals(line))
                    {
                        log.debug("关闭连接");
                        channel.close();
                        break;
                    }
                    channel.writeAndFlush(line);
                }
            }
        ,"input").start();

        ChannelFuture closeFuture = channel.closeFuture();

        // 方式一：主线程同步等待channel关闭，然后进行善后工作
//        closeFuture.sync();
//        log.debug("善后工作。。。。");

        // 方式二：添加回调钩子，等NIO线程关闭连接之后，再由NIO线程调用回调钩子
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("善后工作");

                // 此时EventLoopGroup里面可能还有线程在执行任务
                // 我们要等这些线程执行完毕，然后再关闭线程池
                // 所以调用的是shutdownGracefully
                eventLoopGroup.shutdownGracefully();
            }
        });


    }
}
