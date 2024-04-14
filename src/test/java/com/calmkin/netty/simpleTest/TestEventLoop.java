package com.calmkin.netty.simpleTest;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);

        EventLoop eventLoop = eventLoopGroup.next();


        // 执行普通任务
        eventLoop.submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("处理耗时任务");
        });

        //执行定时任务
        eventLoop.next().scheduleAtFixedRate(()->{
            log.debug("执行定时任务");
        }, 0, 1, TimeUnit.SECONDS);

    }
}
