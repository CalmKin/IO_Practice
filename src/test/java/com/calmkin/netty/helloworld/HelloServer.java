package com.calmkin.netty.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        // 1. 服务器启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                // 2. BossEventLoop，WorkerEventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择服务器的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                // 4. boss负责处理连接，child其实就是前面的worker，负责处理读写
                // handler决定了 worker能执行哪些操作
                .childHandler(
                        // ChannelInitializer也是个handler，它负责添加其他handler
                        // ChannelInitializer会在连接建立后被调用，做一个初始化操作
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 将ByteBuf转换为字符串, 同时交给下一个处理器
                                nioSocketChannel.pipeline().addLast(new StringDecoder());
                                // 自定义handler
                                // 多个handler按照添加的顺序依次执行
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override   // 读事件
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 打印上一步转换好的字符串
                                        System.out.println(msg);
                                    }
                                });
                            }
                        }
                )
                // 绑定监听端口
                .bind(8080);


    }
}
