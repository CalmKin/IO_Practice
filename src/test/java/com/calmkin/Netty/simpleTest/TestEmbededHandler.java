package com.calmkin.Netty.simpleTest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;

public class TestEmbededHandler {
    public static void main(String[] args) {

        ChannelInboundHandlerAdapter h1 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                System.out.println(1);
                ctx.fireChannelRead(msg); // 2
            }
        };
        ChannelInboundHandlerAdapter h2 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                System.out.println(2);
                ctx.fireChannelRead(msg); // 2
            }
        };

        ChannelOutboundHandlerAdapter h3 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg,
                              ChannelPromise promise) {
                System.out.println(3);
                ctx.write(msg, promise); // 5
            }
        };

        ChannelOutboundHandlerAdapter h4 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg,
                              ChannelPromise promise) {
                System.out.println(4);
                ctx.write(msg, promise); // 5
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(h1, h2, h3, h4);

        channel.writeInbound("inbound");
        channel.writeOutbound("outbound");

    }
}
