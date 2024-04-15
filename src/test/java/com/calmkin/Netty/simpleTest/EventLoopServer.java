package com.calmkin.Netty.simpleTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {

        // 专门用于处理耗时任务
        DefaultEventLoopGroup group = new DefaultEventLoopGroup();

        new ServerBootstrap()
                // 指定前一个作为Boss，处理Accept事件，后一个作为Worker，处理读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                nioSocketChannel.pipeline().addLast("IO-Group",new ChannelInboundHandlerAdapter(){
                                        @Override                                       // 如果没有指定解码器。那么这里得到的是byteBuf
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(StandardCharsets.UTF_8) );
                                        // 必须要调用这个方法,才能将上下文传递给下一个handler
                                        ctx.fireChannelRead(msg);
                                    }
                                }).addLast(group,"耗时-Group",new ChannelInboundHandlerAdapter(){
                                    @Override                                       // 如果没有指定解码器。那么这里得到的是byteBuf
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug("处理耗时任务" + buf.toString(StandardCharsets.UTF_8) );
                                    }
                                });
                            }
                        }
                )
                .bind(8080);



    }
}
