package com.calmkin.netty.simpleTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                // 指定前一个作为Boss，处理Accept事件，后一个作为Worker，处理读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                        @Override                                       // 如果没有指定解码器。那么这里得到的是byteBuf
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(StandardCharsets.UTF_8) );
                                    }
                                });
                            }
                        }
                )
                .bind(8080);



    }
}
