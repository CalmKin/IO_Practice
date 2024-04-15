package com.calmkin.Netty.simpleTest;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) {
        // 1. 准备EventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();

        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);


        new Thread(()->{

            try {

                log.debug("开始计算");
                Thread.sleep(1000);

                // 模拟中间发生了异常
                int i = 1 / 0;

                promise.setSuccess(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }

        }).start();


        // 同步获取
        try {
            log.debug("获取结果:{}", promise.get());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
